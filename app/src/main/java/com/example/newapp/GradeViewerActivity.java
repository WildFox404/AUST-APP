package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.*;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;


public class GradeViewerActivity extends AppCompatActivity {

    private static final String KEY_GRADE_YEAR = "gradeyear";
    private static final String KEY_GRADE_TERM = "gradeterm";
    private JsonObject grade_result;
    private SharedViewModel viewModel;
    private User user;
    private Gson gson = new Gson();
    private MyDBHelper myDBHelper;
    private String total_credits;
    private String total_gp;
    private JsonArray semester_lessons;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    private SharedPreferences sharedPreferences;



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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradeview);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象
        user = User.getInstance(); // 获取User类的实例
        ImageView exitButton =findViewById(R.id.exitButton);


        new GradeAsyncTask().execute();

        UpdateGradeTable();


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
    }
    private class GradeAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return user.get_grade();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                grade_result = result;
                total_credits = grade_result.get("total_credits").getAsString();
                total_gp = grade_result.get("total_gp").getAsString();
                semester_lessons = grade_result.getAsJsonArray("semester_lessons");
                Log.d("成绩获取", "成绩获取成功");
                UpdateGradeTable();
            }
        }
    }
    private void UpdateGradeTable() {
        TableLayout gpaViewer = findViewById(R.id.gpa_tableLayout);
        TableLayout headertable = findViewById(R.id.headerRow);
        TextView gpaResult =findViewById(R.id.gpaResult);
        gpaResult.setText("绩点:"+total_gp+" 学分:"+total_credits);
        gpaResult.setTextSize(20);
        headertable.removeAllViews();
        int i =0 ;
        // 动态创建表头
        TableRow headerRow = new TableRow(this);
        // 创建表头的每一列
        // 创建并添加总评成绩列
        TextView totalScoreHeader = new TextView(this);
        totalScoreHeader.setText("总评成绩");
        totalScoreHeader.setTextSize(12); // 设置字号为12号
        headerRow.addView(totalScoreHeader);

        // 创建并添加课程名称列
        TextView courseNameHeader = new TextView(this);
        courseNameHeader.setText("课程名称");
        courseNameHeader.setTextSize(12); // 设置字号为12号
        headerRow.addView(courseNameHeader);

        // 创建并添加学分列
        TextView creditHeader = new TextView(this);
        creditHeader.setText("学分");
        creditHeader.setTextSize(12); // 设置字号为12号
        headerRow.addView(creditHeader);

        // 创建并添加绩点列
        TextView GPAHeader = new TextView(this);
        GPAHeader.setText("绩点");
        GPAHeader.setTextSize(12); // 设置字号为12号
        headerRow.addView(GPAHeader);

        // 将表头行添加到表格中
        headertable.addView(headerRow);
        if (semester_lessons != null) {
        for (JsonElement semester_lesson : semester_lessons) {
            JsonObject semesterLessonObject = semester_lesson.getAsJsonObject();
            String code=semesterLessonObject.get("code").getAsString();
            String semester_credits=semesterLessonObject.get("semester_credits").getAsString();
            String semester_gp=semesterLessonObject.get("semester_gp").getAsString();
            JsonArray lessons =semesterLessonObject.getAsJsonArray("lessons");

            TextView headCategory = new TextView(this);
            headCategory.setText(code+"学分:"+semester_credits+"绩点:"+semester_gp);
            headCategory.setTextSize(20); // 设置字号为12号
            headCategory.setTextColor(Color.parseColor("#344CAA"));
            headCategory.setMinHeight(180);
            gpaViewer.addView(headCategory);
            for (JsonElement lesson : lessons) {
                TableRow row = new TableRow(this);

                JsonObject lessonJson = lesson.getAsJsonObject();
                String course_name=lessonJson.get("course_name").getAsString();
                String course_credit=lessonJson.get("course_credit").getAsString();
                String course_gp=lessonJson.get("course_gp").getAsString();
                String score_text=lessonJson.get("score_text").getAsString();

                TextView totalScoreColumn = new TextView(this);
                String totalScore = score_text;
                totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
                totalScoreColumn.setTextSize(12); // 设置字号为12号
                totalScoreColumn.setMinHeight(200);
                row.addView(totalScoreColumn);

                TextView courseNameColumn = new TextView(this);
                String courseName = course_name;
                courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
                courseNameColumn.setTextSize(12); // 设置字号为12号
                courseNameColumn.setMinHeight(200);
                row.addView(courseNameColumn);

                TextView creditColumn = new TextView(this);
                String credit = course_credit;
                creditColumn.setText(credit != null ? insertNewlines(credit) : "");
                creditColumn.setTextSize(12); // 设置字号为12号
                creditColumn.setMinHeight(200);
                row.addView(creditColumn);

                TextView GPAColumn = new TextView(this);
                String GPA = course_gp;
                GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
                GPAColumn.setTextSize(12); // 设置字号为12号
                GPAColumn.setMinHeight(200);
                row.addView(GPAColumn);

                // 设置偶数行背景色为#BCBCBC
                if (i % 2 == 1) {
                    row.setBackgroundColor(Color.parseColor("#22BFFFFF"));
                }
                // 将表格行添加到表格中
                gpaViewer.addView(row);
                i+=1;
            }
        }}
    }
}
