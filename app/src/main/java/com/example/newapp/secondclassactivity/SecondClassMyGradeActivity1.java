package com.example.newapp.secondclassactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.navigation.HomeFragment;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.w3c.dom.Text;

import java.util.ArrayList;


public class SecondClassMyGradeActivity1 extends AppCompatActivity {
    private static final String KEY_YEAR = "second_class_my_grade_year";
    private static final String KEY_TERM = "second_class_my_grade_term";
    private SharedPreferences sharedPreferences;
    private LayoutInflater inflater;
    private LinearLayout layout_content;
    private Boolean classify_selected=true;
    private JsonArray MyGradeClassify;
    private JsonArray MyGradeTerm;
    private String classify_name = "思想政治引领";
    private ArrayList<TextView> textview_list= new ArrayList<>();
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private String selectedYear;
    private Integer selectedTerm;
    private ArrayAdapter<String> adapter = null;
    private Boolean spinner_update=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassmygradeview);

        selectedYear = secondClassUser.getGrade() + "-" + (secondClassUser.getGrade() + 1);
        selectedTerm = 1;

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        inflater = getLayoutInflater();

        layout_content = findViewById(R.id.my_grade_layout);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, secondClassUser.getYears());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        selectedYear = savedYear;
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        if (savedTerm.equals("第一学期")) {
            selectedTerm=1;
        } else if (savedTerm.equals("第二学期")) {
            selectedTerm=2;
        } else {
            selectedTerm=1;
        }

        Spinner TermSpinner = findViewById(R.id.TermSpinner);
        Spinner YearSpinner = findViewById(R.id.YearSpinner);

        YearSpinner.setAdapter(adapter);
        YearSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        YearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                Log.d("二课学期统计", "学年选择: "+selected);
                if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_YEAR, selected);
                    selectedYear =selected;
                    Log.d("二课学期统计", "selectedYear被赋值: "+selectedYear);
                    updateMyGradeTermUI();
                    Log.d("二课学期统计", "updateMyGradeTermUI开始");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
        TermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selected.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selected);

                    if (selected.equals("第一学期")) {
                        selectedTerm=1;
                    } else if (selected.equals("第二学期")) {
                        selectedTerm=2;
                    } else {
                        selectedTerm=1;
                    }
                    Log.d("二课学期统计", "学期选择: "+selected);
                    updateMyGradeTermUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        YearSpinner.setSelection(getIndex(YearSpinner, savedYear));
        TermSpinner.setSelection(getIndex(TermSpinner, savedTerm));

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView classify_textview = findViewById(R.id.classify_textview);
        TextView term_textview = findViewById(R.id.term_textview);
        classify_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(classify_selected){
                    //已经选择了,啥也不做
                }else{
                    //没有选择,执行代码
                    classify_selected=true;
                    classify_textview.setTextColor(getResources().getColor(R.color.icon_blue));
                    term_textview.setTextColor(getResources().getColor(R.color.black));
                    layout_content.removeAllViews();
                    new MyGradeTermAsyncTask().execute();
                }
            }
        });
        term_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(classify_selected){
                    //没有选择,执行代码
                    classify_selected=false;
                    term_textview.setTextColor(getResources().getColor(R.color.icon_blue));
                    classify_textview.setTextColor(getResources().getColor(R.color.black));
                    layout_content.removeAllViews();
                    new MyGradeTermAsyncTask().execute();
                }else{
                    //已经选择了,啥也不做
                }
            }
        });
        new MyGradeClassifyAsyncTask().execute();
    }

    private class MyGradeClassifyAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return secondClassUser.getMyGradeClassify();
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                MyGradeClassify = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateMyGradeClassifyUI();
            }
        }
    }

    private void updateMyGradeClassifyUI() {
        View grade_classify_view =new View(this);
        grade_classify_view = inflater.inflate(R.layout.secondclassmygradeclassify, null);
        TextView selected1 = grade_classify_view.findViewById(R.id.selected1);
        TextView selected2 = grade_classify_view.findViewById(R.id.selected2);
        TextView selected3 = grade_classify_view.findViewById(R.id.selected3);
        TextView selected4 = grade_classify_view.findViewById(R.id.selected4);
        TextView selected5 = grade_classify_view.findViewById(R.id.selected5);
        TextView selected6 = grade_classify_view.findViewById(R.id.selected6);
        textview_list.add(selected1);
        textview_list.add(selected2);
        textview_list.add(selected3);
        textview_list.add(selected4);
        textview_list.add(selected5);
        textview_list.add(selected6);
        setTextViewListener(selected1,"思想政治引领",grade_classify_view);
        setTextViewListener(selected2,"实践能力培养",grade_classify_view);
        setTextViewListener(selected3,"社会责任担当",grade_classify_view);
        setTextViewListener(selected4,"创新创业孵化",grade_classify_view);
        setTextViewListener(selected5,"综合素质拓展",grade_classify_view);
        setTextViewListener(selected6,"菁英锻炼养成",grade_classify_view);

        updateMyGradeClassifyContentUI(grade_classify_view);
    }

    private void updateMyGradeClassifyContentUI(View grade_classify_view){
        TextView term_low_hours = grade_classify_view.findViewById(R.id.term_low_hours);
        TextView school_low_hours = grade_classify_view.findViewById(R.id.school_low_hours);
        TextView my_hours = grade_classify_view.findViewById(R.id.my_hours);
        TextView my_score = grade_classify_view.findViewById(R.id.my_score);
        LinearLayout my_grade_classify = grade_classify_view.findViewById(R.id.my_grade_classify);
        my_grade_classify.removeAllViews();

        for (JsonElement classify_result : MyGradeClassify) {
            try {
                JsonObject classify_object = classify_result.getAsJsonObject();
                String classifyName = classify_object.get("classifyName").getAsString();
                if(classifyName.equals(classify_name)){
                    String classifyHours = classify_object.get("classifyHours").getAsString();
                    String classifyScore = classify_object.get("classifyScore").getAsString();
                    String minHours = classify_object.get("minHours").getAsString();
                    String termMinHours = classify_object.get("termMinHours").getAsString();
                    JsonArray hoursRecordList = classify_object.get("hoursRecordList").getAsJsonArray();

                    term_low_hours.setText(termMinHours);
                    school_low_hours.setText(minHours);
                    my_hours.setText("总学时:"+classifyHours);
                    my_score.setText("总学分:"+classifyScore);

                    for (JsonElement hoursRecord_result : hoursRecordList) {
                        JsonObject hoursRecord_object = hoursRecord_result.getAsJsonObject();
                        String name_result = hoursRecord_object.get("name").getAsString();
                        String time_result = hoursRecord_object.get("time").getAsString();
                        String hours_result = hoursRecord_object.get("hours").getAsString();

                        View my_grade_content =new View(this);
                        my_grade_content = inflater.inflate(R.layout.secondclassmygradecontent, null);
                        TextView name = my_grade_content.findViewById(R.id.name);
                        TextView time = my_grade_content.findViewById(R.id.time);
                        TextView hours = my_grade_content.findViewById(R.id.hours);
                        TextView classify_name = my_grade_content.findViewById(R.id.classify_name);

                        name.setText(name_result);
                        time.setText(DateUtils.convertTimestampToDate(time_result));
                        hours.setText("+"+hours_result);
                        classify_name.setText(classifyName);

                        my_grade_classify.addView(my_grade_content);
                    }
                }
            }catch (Exception e){

            }
        }

        layout_content.addView(grade_classify_view);
    }

    private class MyGradeTermAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return secondClassUser.getMyGradeTerm();
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                MyGradeTerm = result;
                Log.d("考试安排获取", "考试安排获取成功");
                layout_content.removeAllViews();
                updateMyGradeTermUI();
            }
        }
    }

    private void updateMyGradeTermUI() {

        String savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        selectedYear = savedYear;
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        if (savedTerm.equals("第一学期")) {
            selectedTerm=1;
        } else if (savedTerm.equals("第二学期")) {
            selectedTerm=2;
        } else {
            selectedTerm=1;
        }

        View grade_term_view =new View(this);
        grade_term_view = inflater.inflate(R.layout.secondclassmygradeterm, null);

        Spinner TermSpinner = grade_term_view.findViewById(R.id.TermSpinner);
        Spinner YearSpinner = grade_term_view.findViewById(R.id.YearSpinner);

        if (adapter != null && YearSpinner != null) {
            YearSpinner.setAdapter(adapter);
            YearSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
            YearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = (String) parent.getItemAtPosition(position);
                    String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                    Log.d("二课学期统计", "学年选择: "+selected);
                    if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                        saveSelection(KEY_YEAR, selected);
                        selectedYear =selected;
                        Log.d("二课学期统计", "selectedYear被赋值: "+selectedYear);
                        updateMyGradeTermUI();
                        Log.d("二课学期统计", "updateMyGradeTermUI开始");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 当没有选择任何项时触发此方法
                }
            });
        }
        TermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selected.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selected);

                    if (selected.equals("第一学期")) {
                        selectedTerm=1;
                    } else if (selected.equals("第二学期")) {
                        selectedTerm=2;
                    } else {
                        selectedTerm=1;
                    }
                    Log.d("二课学期统计", "学期选择: "+selected);
                    updateMyGradeTermUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        YearSpinner.setSelection(getIndex(YearSpinner, savedYear));
        TermSpinner.setSelection(getIndex(TermSpinner, savedTerm));

        TextView my_hours = grade_term_view.findViewById(R.id.my_hours);
        LinearLayout my_grade_term = grade_term_view.findViewById(R.id.my_grade_term);

        for (JsonElement term_result : MyGradeTerm) {
            try {
                JsonObject term_object = term_result.getAsJsonObject();
                String termName = term_object.get("termName").getAsString();
                Integer termNumber = term_object.get("termNumber").getAsInt();
                Log.d("二课学期统计", "selectedYear:"+selectedYear+"selectedTerm"+selectedTerm);
                if (termName.contains(selectedYear)&& termNumber.equals(selectedTerm)){
                    String termHours = term_object.get("termHours").getAsString();
                    JsonArray hoursRecordList = term_object.get("hoursRecordList").getAsJsonArray();

                    my_hours.setText("总学时:"+termHours);
                    for (JsonElement hoursRecord_result : hoursRecordList) {
                        JsonObject hoursRecord_object = hoursRecord_result.getAsJsonObject();
                        String name_result = hoursRecord_object.get("name").getAsString();
                        String time_result = hoursRecord_object.get("time").getAsString();
                        String hours_result = hoursRecord_object.get("hours").getAsString();
                        String classifyName = hoursRecord_object.get("classifyName").getAsString();

                        View my_grade_content = new View(this);
                        my_grade_content = inflater.inflate(R.layout.secondclassmygradecontent, null);

                        TextView name = my_grade_content.findViewById(R.id.name);
                        TextView time = my_grade_content.findViewById(R.id.time);
                        TextView hours = my_grade_content.findViewById(R.id.hours);
                        TextView classify_name = my_grade_content.findViewById(R.id.classify_name);

                        name.setText(name_result);
                        time.setText(DateUtils.convertTimestampToDate(time_result));
                        hours.setText("+"+hours_result);
                        classify_name.setText(classifyName);

                        my_grade_term.addView(my_grade_content);
                    }
                }
            }catch (Exception e){
                Log.e("二课学期统计", "发生错误: "+e.toString());
            }
        }

        layout_content.addView(grade_term_view);
    }


    private void setTextViewListener(TextView textView, String string,View grade_classify_view){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classify_name = string;
                textview_list_select(textView);
                layout_content.removeAllViews();
                updateMyGradeClassifyContentUI(grade_classify_view);
            }
        });
    }
    private void textview_list_select(TextView selectTextView){
        for(TextView textView:textview_list){
            if(textView.equals(selectTextView)){
                textView.setTextColor(getResources().getColor(R.color.icon_blue));
            }else{
                textView.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
    private void saveSelection(String key, String value) {
        Log.d("HomeActivity", "saveSelection: "+key+":"+value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0; // 默认返回第一个选项
    }
}
