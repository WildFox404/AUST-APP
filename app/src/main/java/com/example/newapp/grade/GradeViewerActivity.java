package com.example.newapp.grade;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.R;
import com.example.newapp.entries.SharedViewModel;
import com.example.newapp.entries.User;
import com.google.gson.*;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Random;


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
                Log.e("成绩获取", "处理结果时出现异常: " + e.getMessage());
            }
            return null;
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
//        ImageView grade_background =findViewById(R.id.grade_background);
//        int[] backgroundImages = {R.drawable.background};
        Random rand = new Random();
//        // 找到子视图并设置内容
//        int backgroundRandomIndex = rand.nextInt(backgroundImages.length);
//        grade_background.setImageResource(backgroundImages[backgroundRandomIndex]);


        int[] dragonImages = {R.drawable.dragon1, R.drawable.dragon2, R.drawable.dragon3, R.drawable.dragon4, R.drawable.dragon5,
                R.drawable.dragon6, R.drawable.dragon7, R.drawable.dragon8, R.drawable.dragon9,
                R.drawable.dragon10,R.drawable.dragon11,R.drawable.dragon12};

        LinearLayout grade_content_linearlayout = findViewById(R.id.grade_content_linearlayout);
        grade_content_linearlayout.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView gpaResult=findViewById(R.id.gpaResult);
        gpaResult.setText("绩点:"+total_gp+" \t学分:"+total_credits);
        gpaResult.setTextSize(20);

        if (semester_lessons != null) {
        for (JsonElement semester_lesson : semester_lessons) {
            JsonObject semesterLessonObject = semester_lesson.getAsJsonObject();
            String code=semesterLessonObject.get("code").getAsString();
            String semester_credits=semesterLessonObject.get("semester_credits").getAsString();
            String semester_gp=semesterLessonObject.get("semester_gp").getAsString();
            JsonArray lessons =semesterLessonObject.getAsJsonArray("lessons");

            int partLength = 4; // 计算每部分的长度

            // 第一部分
            String part1 = code.substring(0, partLength);
            // 第二部分
            String part2 = code.substring(partLength, 2 * partLength);
            // 第三部分（余数部分也加入第三部分）
            String part3 = code.substring(2 * partLength);
            if(part3.equals("01")){
                part3="第一学期";
            }else{
                part3="第二学期";
            }
            View contentView =new View(this);
            contentView = inflater.inflate(R.layout.gradeviewcontent, null);

            //确保找到视图后再操作，避免空指针异常
            TextView course_name_content = contentView.findViewById(R.id.course_name);
            if (course_name_content != null) {
                course_name_content.setText(part1 + "-" + part2 + " " + part3);
            }

            TextView credit_content = contentView.findViewById(R.id.credit_content);
            if (credit_content != null) {
                credit_content.setText("学分:" + semester_credits);
            }

            TextView gpa_content = contentView.findViewById(R.id.gpa_content);
            if (gpa_content != null) {
                gpa_content.setText("绩点:" + semester_gp);
            }

            LinearLayout gradeview_content_child = contentView.findViewById(R.id.gradeview_content_child);
            // 如果gradeview_content_child是一个LinearLayout，确保在使用之前进行类型检查


            for (JsonElement lesson : lessons) {
                JsonObject lessonJson = lesson.getAsJsonObject();
                String course_name=lessonJson.get("course_name").getAsString();
                String course_credit=lessonJson.get("course_credit").getAsString();
                String course_gp=lessonJson.get("course_gp").getAsString();
                String score_text=lessonJson.get("score_text").getAsString();

                View contentChildView =new View(this);
                contentChildView = inflater.inflate(R.layout.gradeviewcontentchild, null);

                // 找到子视图并设置内容
                int randomIndex = rand.nextInt(dragonImages.length);
                ImageView dragon_icon = contentChildView.findViewById(R.id.dragon_icon);
                if (dragon_icon != null) {
                    dragon_icon.setImageResource(dragonImages[randomIndex]);
                }

                TextView course_name_child = contentChildView.findViewById(R.id.course_name_child);
                if (course_name_child != null) {
                    course_name_child.setText(course_name);
                }

                TextView score_content_child = contentChildView.findViewById(R.id.score);
                if (score_content_child != null) {
                    String fullText = "成绩:"+score_text;
                    if(Float.parseFloat(score_text)>=60){
                        SpannableString spannableString = new SpannableString(fullText);

                        // 设置从第五个字符到最后一个字符的文本颜色
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.green1)); // 替换成你想要的颜色
                        spannableString.setSpan(colorSpan, 3, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        score_content_child.setText(spannableString);
                    }else{
                        SpannableString spannableString = new SpannableString(fullText);

                        // 设置从第五个字符到最后一个字符的文本颜色
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.fire_red)); // 替换成你想要的颜色
                        spannableString.setSpan(colorSpan, 3, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        score_content_child.setText(spannableString);
                    }
                }

                TextView credit_content_child = contentChildView.findViewById(R.id.credit);
                if (credit_content_child != null) {
                    credit_content_child.setText("学分:" + course_credit);
                }

                TextView gpa_content_child = contentChildView.findViewById(R.id.gpa);
                if (gpa_content_child != null) {
                    gpa_content_child.setText("绩点:" + course_gp);
                }

                if (gradeview_content_child instanceof LinearLayout) {
                    gradeview_content_child.addView(contentChildView);
                }
            }
            
            grade_content_linearlayout.addView(contentView);
            LinearLayout empty_linearlayout =new LinearLayout(this);
            LinearLayout.LayoutParams linearlayout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20); // 这里的数字是宽和高，单位是像素
            empty_linearlayout.setLayoutParams(linearlayout_params);
            grade_content_linearlayout.addView(empty_linearlayout);
        }}
    }
}
