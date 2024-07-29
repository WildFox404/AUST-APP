package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CampusCalendarActivity extends AppCompatActivity {
    private static final String KEY_YEAR = "CampusCalendarActivity_year";
    private static final String KEY_TERM = "CampusCalendarActivity_term";
    private String year;
    private String term;
    private MyDBHelper myDBHelper;
    private SharedPreferences sharedPreferences;
    private User user = User.getInstance(); // 获取User类的实例
    private JsonArray campus_calender_results;
    private String startDate;
    private String endDate;
    private ImageView image_left;
    private ImageView image_right;
    private String current_date;
    private String current_date_id;
    private String current_code;
    private String[] month_list ={};
    private Integer month_list_index=0;
    private Integer device_width;
    private Integer device_margin1dp;
    private DeviceDataUtils deviceDataUtils = DeviceDataUtils.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campuscalenderview);

        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象

        device_width=deviceDataUtils.getWidth();
        device_margin1dp=deviceDataUtils.getMargin_1dp();
        Spinner yearSpinner = findViewById(R.id.testYearSpinner);
        Spinner termSpinner = findViewById(R.id.testTermSpinner);
        ImageView exitButton = findViewById(R.id.exitButton);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedYear = sharedPreferences.getString(KEY_YEAR, String.valueOf(user.getCoverage_year()));
        Log.d("CampusCalendarActivity", "CampusCalendarActivity savedYear: "+savedYear);
        String savedTerm = sharedPreferences.getString(KEY_TERM, "第一学期");
        Log.d("CampusCalendarActivity", "CampusCalendarActivity savedTerm: "+savedTerm);
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
                Log.d("校历", selectedYear);
                String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_YEAR, selectedYear);
                    year=selectedYear.replace("-", "");

                    current_code=year+term;
                    Log.d("校历", String.valueOf(current_code));
                    try {
                        initUI();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
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
                        term="01";
                    } else if (selectedTerm.equals("第二学期")) {
                        term="02";
                    } else {
                        // 其他情况的处理
                    }
                    Log.d("校历", "开始更新课表: ");
                    current_code=year+term;
                    Log.d("校历", String.valueOf(current_code));
                    try {
                        initUI();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));

        try {
            year = savedYear.replace("-", "");; // 获取前四个字符
        }catch (Exception e){
            Log.d("考试安排", "考试安排初始化出现问题");
            year=String.valueOf(user.getCoverage_year())+String.valueOf(user.getCoverage_year()+1);
        }


        if (savedTerm.equals("第一学期")) {
            term = "01";
        } else if (savedTerm.equals("第二学期")) {
            term = "02";
        } else {
            term = "01";
        }
        current_code=year+term;
        new SemesterAsyncTask().execute();
    }

    private void initUI() throws ParseException {
        LinearLayout campus_calender_linearlayout = findViewById(R.id.campus_calender_linearLayout);
        campus_calender_linearlayout.removeAllViews();
        campus_calender_linearlayout.setOrientation(LinearLayout.VERTICAL);

        campus_calender_linearlayout.addView(initheaderUI(false,true));
        campus_calender_linearlayout.addView(initCalender());
    }

    private void initUI(Boolean left,Boolean right) throws ParseException {
        LinearLayout campus_calender_linearlayout = findViewById(R.id.campus_calender_linearLayout);
        campus_calender_linearlayout.removeAllViews();
        campus_calender_linearlayout.setOrientation(LinearLayout.VERTICAL);

        campus_calender_linearlayout.addView(initheaderUI(left,right));
        campus_calender_linearlayout.addView(initCalender());
    }

    private LinearLayout initCalender() {
        LinearLayout calender_linearlayout=new LinearLayout(this);
        calender_linearlayout.setOrientation(LinearLayout.VERTICAL);
        calender_linearlayout.addView(initCalenderHeader());
        calender_linearlayout.addView(initCalenderContent());
        return calender_linearlayout;
    }

    private View initCalenderContent() {
        Boolean startDateStatus=false;
        Boolean endDateStatus=false;
        if(month_list[month_list_index].equals(startDate.substring(0, 7))){
            //学期开头
            startDateStatus=true;
            Log.d("initCalenderContent", month_list[month_list_index]+"/"+startDate.substring(0, 7));
        }else if(month_list[month_list_index].equals(endDate.substring(0, 7))){
            //学期末尾
            endDateStatus=true;
            Log.d("initCalenderContent", month_list[month_list_index]+"/"+endDate.substring(0, 7));
        }else{
            //啥也不做
        }

        int textview_margin=2;
        int width =Math.round((device_width-device_margin1dp*20-device_margin1dp*textview_margin*10)/7);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(width, width);
        textParams.setMargins(4,0,4,0);

        List<List<String>> weeklyDatas;
        LinearLayout calender_content_linearlayout=new LinearLayout(this);
        calender_content_linearlayout.setOrientation(LinearLayout.VERTICAL);
        if(month_list!=null&&month_list.length>0){
            weeklyDatas=DateUtils.createWeeklyData(month_list[month_list_index]+"-01");
            Log.d("initCalenderContent", weeklyDatas.toString());
            // 打印每周数据
            for (List<String> weekData : weeklyDatas) {
                LinearLayout week_linearlayout =new LinearLayout(this);
                week_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
                week_linearlayout.setHorizontalGravity(Gravity.CENTER);
                for (String day : weekData) {
                    TextView day_textView =new TextView(this);
                    if(startDateStatus){
                        //开学
                        String startDay = startDate.substring(8,10);
                        Log.d("initCalenderContent", startDay);
                        if (startDay.startsWith("0")) {
                            startDay = startDay.substring(1);
                        }
                        Log.d("initCalenderContent", startDay);
                        if (!day.isEmpty() && !startDay.isEmpty()) {
                            if(Integer.parseInt(day)>=Integer.parseInt(startDay)){
                                day_textView.setTextColor(Color.BLUE);
                            }
                        }
                    }
                    else if(endDateStatus){
                        //期末
                        String endDay = endDate.substring(8,10);
                        Log.d("initCalenderContent", endDay);
                        if (endDay.startsWith("0")) {
                            endDay = endDay.substring(1);
                        }
                        Log.d("initCalenderContent", endDay);
                        if (!day.isEmpty() && !endDay.isEmpty()) {
                            if(Integer.parseInt(day)<=Integer.parseInt(endDay)){
                                day_textView.setTextColor(Color.BLUE);
                            }
                        }
                    }else{
                        //什么也不做
                        day_textView.setTextColor(Color.BLUE);
                    }
                    day_textView.setText(day);
                    day_textView.setTextSize(20);
                    day_textView.setGravity(Gravity.CENTER);
                    day_textView.setLayoutParams(textParams);
                    week_linearlayout.addView(day_textView);
                }
                calender_content_linearlayout.addView(week_linearlayout);
            }
        }
        Log.d("initCalenderContent", "initCalenderContent: OK");
        return calender_content_linearlayout;
    }

    private LinearLayout initCalenderHeader() {
        int textview_margin=2;
        int width =Math.round((device_width-device_margin1dp*20-device_margin1dp*textview_margin*10)/7);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(width, width);
        textParams.setMargins(4,0,4,0);

        LinearLayout calender_header_linearlayout=new LinearLayout(this);
        calender_header_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
        calender_header_linearlayout.setHorizontalGravity(Gravity.CENTER);
//        calender_header_linearlayout.setBackgroundColor(Color.BLUE);
        String weeks[]= {"日","一","二","三","四","五","六"};
        for(String week:weeks){
            TextView weekheader=new TextView(this);
            weekheader.setText(week);
            weekheader.setTextSize(20);
            weekheader.setGravity(Gravity.CENTER);
            weekheader.setLayoutParams(textParams);
//            calender_header_linearlayout.setBackgroundColor(Color.RED);
            calender_header_linearlayout.addView(weekheader);
        }
        return calender_header_linearlayout;
    }

    private LinearLayout initheaderUI(Boolean left,Boolean right) throws ParseException {
        LinearLayout header_linearlayout=new LinearLayout(this);
        header_linearlayout.removeAllViews();
        header_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
        header_linearlayout.setGravity(Gravity.CENTER);

        getDateRange();

        TextView header_textview=new TextView(this);
        if(month_list.length>0){
            header_textview.setText(month_list[month_list_index]);
        }else{
            header_textview.setText("未获取到信息");
        }

        header_textview.setTextSize(25);
        header_textview.setGravity(Gravity.CENTER);
        image_left=new ImageView(this);
        image_right=new ImageView(this);

        int width =Math.round((device_width-device_margin1dp*20)/6);
        // 获取文字视图的高度

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(width, width);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,width);
        header_textview.setLayoutParams(textParams);
        image_left.setLayoutParams(imageParams);
        image_right.setLayoutParams(imageParams);
        if(left){
            image_left.setImageResource(R.drawable.button_left_true);
        }else{
            image_left.setImageResource(R.drawable.button_left_false);
        }
        if(right){
            image_right.setImageResource(R.drawable.button_right_true);
        }else{
            image_right.setImageResource(R.drawable.button_right_false);
        }

        image_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("initheaderUI", "onClick:image_left");
                if(month_list_index>0){
                    Log.d("initheaderUI", String.valueOf(month_list_index));
                    month_list_index--;
                    Log.d("initheaderUI", String.valueOf(month_list_index));
                    Log.d("initheaderUI", month_list.toString());
                    if(month_list_index.equals(0)){
                        try {
                            initUI(false,true);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        try {
                            initUI(true,true);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                    //啥也不干
                }
            }
        });
        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("initheaderUI", "onClick:image_right");
                if(month_list_index<month_list.length-1){
                    Log.d("initheaderUI", String.valueOf(month_list_index));
                    month_list_index++;
                    Log.d("initheaderUI", String.valueOf(month_list_index));
                    Log.d("initheaderUI", month_list.toString());
                    if(month_list_index.equals(month_list.length-1)){
                        try {
                            initUI(true,false);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        try {
                            initUI(true,true);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                    //啥也不干
                }
            }
        });

        header_linearlayout.addView(image_left);
        header_linearlayout.addView(header_textview);
        header_linearlayout.addView(image_right);
        return header_linearlayout;
    }

    private void getDateRange() throws ParseException {
        Log.d("getDateRange", "getDateRange");
        Boolean find_Bool=false;
        if (campus_calender_results != null) {
            for (JsonElement campus_calender_result : campus_calender_results) {
                JsonObject campus_calender_object = campus_calender_result.getAsJsonObject();
                String code = campus_calender_object.get("code").getAsString();
                Log.d("getDateRange", code);
                Log.d("getDateRange", "current_date_id"+code);
                if(code.equals(current_code)){
                    find_Bool=true;
                    //找到对应的数据
                    startDate = campus_calender_object.get("start_date").getAsString();
                    endDate = campus_calender_object.get("end_date").getAsString();
                    Log.d("getDateRange", startDate+"x"+endDate);
                    month_list=DateUtils.getYearMonthArray(startDate,endDate);
                    // 使用 for-each 循环遍历数组
                    for (String month : month_list) {
                        Log.d("getDateRange", month);
                    }

                }
            }
            if(!find_Bool){
                LinearLayout calender_linearlayout=new LinearLayout(this);
                calender_linearlayout.removeAllViews();
            }
        } else {
            // 处理空数组或空对象的情况
            Log.e("getDateRange", "空数据");
        }
    }

    private class SemesterAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            JsonObject Semester_Date = null;
            try {
                Semester_Date = user.update_semester();
            } catch (IOException e) {

            }
            if(Semester_Date!=null){
                JsonArray semesters=Semester_Date.get("semesters").getAsJsonArray();
                current_date_id=Semester_Date.get("cur_semester_id").getAsString();
                return semesters;
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                campus_calender_results = result;
                Log.d("考试安排获取", "考试安排获取成功");
                try {
                    initUI();
                } catch (ParseException e) {
                    Log.d("SemesterAsyncTask", "初始化UI失败");
                }
            }
        }
    }
    private void saveSelection(String key, String value) {
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
