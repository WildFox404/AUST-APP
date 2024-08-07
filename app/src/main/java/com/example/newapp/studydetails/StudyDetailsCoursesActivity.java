package com.example.newapp.studydetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StudyDetailsCoursesActivity extends AppCompatActivity {
    private JsonArray jsonArray=null;
    private String group_course_type_name;
    private GradientDrawable border;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studydetailscoursesview);
        Intent intent = getIntent();
        String jsonArrayString = intent.getStringExtra("jsonArray");
        // 使用Gson库将JsonArray字符串转换为JsonArray对象
        Gson gson = new Gson();
        jsonArray = gson.fromJson(jsonArrayString, JsonArray.class);
        group_course_type_name=intent.getStringExtra("group_course_type_name");

        // 创建一个GradientDrawable对象
        border = new GradientDrawable();
        // 设置边框颜色和宽度
        border.setStroke(1, Color.BLACK);
        // 设置圆角半径
        border.setCornerRadius(10);

        ImageView exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //设置背景颜色
        initUI();
    }
    // 辅助方法，安全获取字符串字段
    private String getStringFromJson(JsonObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            return jsonObject.get(key).getAsString();
        }
        return ""; // 如果字段不存在或者值为null，返回空字符串或者适当的默认值
    }

    private void initUI() {
        Log.d("StudyDetailsCoursesActivity", "initUI开始");
        TextView activityHeader =findViewById(R.id.activityheader);
        activityHeader.setText(group_course_type_name);

        LinearLayout study_details_courses_linearLayout=findViewById(R.id.study_details_courses_linearLayout);

        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (jsonArray!=null){
            for (JsonElement course_result : jsonArray) {
                if (course_result != null && !course_result.isJsonNull() && course_result.isJsonObject()) {
                    JsonObject course_object = course_result.getAsJsonObject();

                    // 检查每个字段是否存在，避免NullPointerException
                    String compulsory = getStringFromJson(course_object, "compulsory");
                    String course_code = getStringFromJson(course_object, "course_code");
                    String course_name = getStringFromJson(course_object, "course_name");
                    String credits = getStringFromJson(course_object, "credits");
                    String passed = getStringFromJson(course_object, "passed");
                    String score = getStringFromJson(course_object, "score");
                    String remark = getStringFromJson(course_object, "remark");

                    // 继续处理你的逻辑，例如将获取到的数据存入对象或者界面展示等
                    // 加载XML定义的布局文件
                    View view =new View(this);
                    view = inflater.inflate(R.layout.studydetailslevel3, null);
                    // 获取id为"course"的TextView
                    TextView course_type_name = view.findViewById(R.id.course_type_name);
                    String course_name_result = "";
                    int lineLength = 10;
                    for (int i = 0; i < course_name.length(); i += lineLength) {
                        if (i + lineLength < course_name.length()) {
                            course_name_result += course_name.substring(i, i + lineLength) + "\n";
                        } else {
                            course_name_result += course_name.substring(i);
                        }
                    }
                    course_type_name.setText("["+course_code+"]"+course_name_result);

                    ImageView passed_status =view.findViewById(R.id.passed_status);
                    TextView group_required_credits =view.findViewById(R.id.group_required_credits);
                    group_required_credits.setText(credits);

                    TextView group_completion_credits =view.findViewById(R.id.group_completion_credits);
                    if(passed.equals("true")){
                        group_completion_credits.setText(credits);
                        passed_status.setImageResource(R.drawable.passed);
                    }else{
                        group_completion_credits.setText("0.0");
                        group_completion_credits.setTextColor(getResources().getColor(R.color.red));
                        passed_status.setImageResource(R.drawable.unpassed);
                    }

                    if(compulsory.equals("false")){
                        ImageView plan_status =view.findViewById(R.id.plan_status);
                        plan_status.setImageResource(R.drawable.unplan);
                    }

                    if(remark.equals("在读")){
                        ImageView onReading =view.findViewById(R.id.onReading);
                        onReading.setImageResource(R.drawable.onreading);
                    }

                    TextView group_score =view.findViewById(R.id.group_score);
                    group_score.setText(score);
                    if(score.equals("--")){
                        group_score.setTextColor(getResources().getColor(R.color.red));
                    }

                    LinearLayout main_layout =view.findViewById(R.id.main_layout);
                    main_layout.setBackground(border);

                    study_details_courses_linearLayout.addView(view);
                }
            }
        }

        Log.d("StudyDetailsCoursesActivity", "initUI结束");
    }
}
