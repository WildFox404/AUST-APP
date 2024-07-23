package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestViewerActivity extends AppCompatActivity {
    private List<Map<String, String>> grade_result;
    private User user = User.getInstance(); // 获取User类的实例
    private MyDBHelper myDBHelper;
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
        ImageView exitButton =findViewById(R.id.exitButton);
        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
        Log.d("考试安排savedYear", "考试安排savedYear: "+savedYear);
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        Log.d("考试安排savedTerm", "考试安排savedTerm: "+savedTerm);
        Spinner yearSpinner = findViewById(R.id.testYearSpinner);
        Spinner termSpinner = findViewById(R.id.testTermSpinner);
        //初始化UI界面
        initUI();
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
    private void initUI() {
        TableLayout headertable = findViewById(R.id.headerRow);
        int i =0 ;
        // 动态创建表头
        TableRow headerRow = new TableRow(this);
        // 创建表头的每一列
        // 创建并添加课程类别列
        TextView courseCategoryHeader = new TextView(this);
        courseCategoryHeader.setText("课程名称");
        headerRow.addView(courseCategoryHeader);

        // 创建并添加最终成绩列
        TextView finalGradeHeader = new TextView(this);
        finalGradeHeader.setText("考试日期");
        headerRow.addView(finalGradeHeader);

        // 创建并添加总评成绩列
        TextView totalScoreHeader = new TextView(this);
        totalScoreHeader.setText("考试类型");
        headerRow.addView(totalScoreHeader);

        // 创建并添加课程名称列
        TextView courseNameHeader = new TextView(this);
        courseNameHeader.setText("考试地点");
        headerRow.addView(courseNameHeader);

        // 创建并添加绩点列
        TextView GPAHeader = new TextView(this);
        GPAHeader.setText("情况");
        headerRow.addView(GPAHeader);

        // 创建并添加学分列
        TextView creditHeader = new TextView(this);
        creditHeader.setText("时间");
        headerRow.addView(creditHeader);

        // 将表头行添加到表格中
        headertable.addView(headerRow);

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        TableLayout testViewer = findViewById(R.id.test_tableLayout);
        testViewer.removeAllViews();
        int i=0;

        for (JsonElement test_result : test_results) {
            JsonObject test_object = test_result.getAsJsonObject();
            String course_name=test_object.get("course_name").getAsString();
            String date=test_object.get("date").getAsString();
            String exam_type_name=test_object.get("exam_type_name").getAsString();
            String finished=test_object.get("finished").getAsString();
            String place_name=test_object.get("place_name").getAsString();
            String time_start=test_object.get("time_start").getAsString();
            String time_end=test_object.get("time_end").getAsString();

            TableRow row = new TableRow(this);

            // 创建并填充每一列
            TextView courseCategoryColumn = new TextView(this);
            String courseCategory = course_name;
            courseCategoryColumn.setText(courseCategory != null ? insertNewlines(courseCategory) : "");
            courseCategoryColumn.setTextSize(11); // 设置字号为12号
            courseCategoryColumn.setMinHeight(200);
            row.addView(courseCategoryColumn);

            TextView finalGradeColumn = new TextView(this);
            String finalGrade = date;
            finalGradeColumn.setText(finalGrade != null ? insertNewlines(finalGrade) : "");
            finalGradeColumn.setTextSize(11); // 设置字号为12号
            finalGradeColumn.setMinHeight(200);
            row.addView(finalGradeColumn);

            TextView totalScoreColumn = new TextView(this);
            String totalScore = exam_type_name;
            totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
            totalScoreColumn.setTextSize(11); // 设置字号为12号
            totalScoreColumn.setMinHeight(200);
            row.addView(totalScoreColumn);

            TextView courseNameColumn = new TextView(this);
            String courseName = place_name;
            courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
            courseNameColumn.setTextSize(11); // 设置字号为12号
            courseNameColumn.setMinHeight(200);
            row.addView(courseNameColumn);

            TextView GPAColumn = new TextView(this);
            String GPA = (finished.equals("true") ? "已完成" : "未完成");
            GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
            GPAColumn.setTextSize(11); // 设置字号为12号
            GPAColumn.setMinHeight(200);
            row.addView(GPAColumn);

            TextView creditColumn = new TextView(this);
            String credit = time_start+time_end;
            creditColumn.setText(credit != null ? insertNewlines(credit) : "");
            creditColumn.setTextSize(11); // 设置字号为12号
            creditColumn.setMinHeight(200);
            row.addView(creditColumn);

            // 将表格行添加到表格中
            testViewer.addView(row);
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


