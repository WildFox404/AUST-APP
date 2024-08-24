package com.example.newapp.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TestViewerActivity extends AppCompatActivity {
    private User user = User.getInstance(); // 获取User类的实例
    JsonArray test_results;
    private SharedPreferences sharedPreferences;
    private int year;
    private int term;
    private static final String KEY_YEAR = "test_year";
    private static final String KEY_TERM = "test_term";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testview);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
        Log.d("考试安排savedYear", "考试安排savedYear: "+savedYear);
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        Log.d("考试安排savedTerm", "考试安排savedTerm: "+savedTerm);
        Spinner yearSpinner = findViewById(R.id.testYearSpinner);
        Spinner termSpinner = findViewById(R.id.testTermSpinner);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user.getYears());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_YEAR, selectedYear);
                    String firstFourChars = selectedYear.substring(0, 4); // 获取前四个字符
                    int number = Integer.parseInt(firstFourChars); // 转换成int类型
                    year=(number-2020)*40+100;
                }
                new TestAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selectedTerm);

                    if (selectedTerm.equals("第一学期")) {
                        term=0;
                    } else if (selectedTerm.equals("第二学期")) {
                        term=20;
                    } else {
                        // 其他情况的处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    new TestAsyncTask().execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));

        int number;
        try {
            String firstFourChars = savedYear.substring(0, 4); // 获取前四个字符
            number = Integer.parseInt(firstFourChars); // 转换成int类型
        }catch (Exception e){
            Log.d("考试安排", "考试安排初始化出现问题");
            number=user.getCoverage_year();
        }
        year=(number-2020)*40+100;

        if (savedTerm.equals("第一学期")) {
            term = 0;
        } else if (savedTerm.equals("第二学期")) {
            term = 20;
        } else {
            term = 0;
        }

        new TestAsyncTask().execute();
    }


    private void saveSelection(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private class TestAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return user.get_test(String.valueOf(year+term));
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            JsonArray emptyArray = new JsonArray();
            return emptyArray;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                test_results = result;
                Log.d("考试安排获取", "考试安排获取成功");
                UpdateTestTable();
            }
        }
    }

    private void UpdateTestTable() {
        LinearLayout test_content_linearlayout=findViewById(R.id.test_content_linearlayout);
        test_content_linearlayout.removeAllViews();
        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for (JsonElement test_result : test_results) {
            JsonObject test_object = test_result.getAsJsonObject();
            String course_name=test_object.get("course_name").getAsString();
            String date=test_object.get("date").getAsString();
            String exam_type_name=test_object.get("exam_type_name").getAsString();
            String finished=test_object.get("finished").getAsString();
            String place_name=test_object.get("place_name").getAsString();
            String time_start=test_object.get("time_start").getAsString();
            String time_end=test_object.get("time_end").getAsString();
            View view =new View(this);
            view = inflater.inflate(R.layout.testviewcontent, null);
            TextView date_textview =view.findViewById(R.id.date);
            date_textview.setText(date);
            TextView time_textview =view.findViewById(R.id.time);
            time_textview.setText(time_start+"-"+time_end);
            TextView location_textview =view.findViewById(R.id.location);
            location_textview.setText(place_name);
            TextView test_status_textview =view.findViewById(R.id.test_status);
            test_status_textview.setText("考试状态:"+(finished.equals("true") ? "已完成" : "未完成"));
            if(finished.equals("true")){
                String fullText = "考试状态:已完成";
                SpannableString spannableString = new SpannableString(fullText);

                // 设置从第五个字符到最后一个字符的文本颜色
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.green)); // 替换成你想要的颜色
                spannableString.setSpan(colorSpan, 5, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                test_status_textview.setText(spannableString);
            }else{
                String fullText = "考试状态:未完成";
                SpannableString spannableString = new SpannableString(fullText);

                // 设置从第五个字符到最后一个字符的文本颜色
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.fire_red)); // 替换成你想要的颜色
                spannableString.setSpan(colorSpan, 5, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                test_status_textview.setText(spannableString);
            }
            TextView test_type_textview =view.findViewById(R.id.test_type);
            test_type_textview.setText("考试类型:"+exam_type_name);
            TextView course_name_textview =view.findViewById(R.id.course_name);
            course_name_textview.setText(course_name);

            test_content_linearlayout.addView(view);
            LinearLayout empty_linearlayout =new LinearLayout(this);
            LinearLayout.LayoutParams linearlayout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20); // 这里的数字是宽和高，单位是像素
            empty_linearlayout.setLayoutParams(linearlayout_params);
            test_content_linearlayout.addView(empty_linearlayout);
        }
    }

    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
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


