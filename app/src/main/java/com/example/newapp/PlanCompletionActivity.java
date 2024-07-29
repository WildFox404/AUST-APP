package com.example.newapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlanCompletionActivity extends AppCompatActivity {
    private DeviceDataUtils deviceDataUtils;
    private JsonObject plan_completion_results;
    private User user =User.getInstance();
    private LinearLayout plan_complete_linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plancompletionview);
        deviceDataUtils = DeviceDataUtils.getInstance();
        ImageView exitButton = findViewById(R.id.exitButton);
        plan_complete_linearLayout = findViewById(R.id.plan_complete_linearLayout);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new PlanCompletionAsyncTask().execute();
    }
    private class PlanCompletionAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return user.get_plan_completion();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                plan_completion_results = result;
                Log.d("PlanCompletionAsyncTask", "PlanCompletionAsyncTask获取成功");
                initUI();
            }else {
                Log.d("PlanCompletionAsyncTask", "PlanCompletionAsyncTask获取失败");
            }
        }
    }

    private void initUI() {



//        "audit_date_time": "2024-07-28 14:48:24",
//                "audit_result": "未通过 预审，有在读课程",
//                "code": "2022305306",
//                "completed_credits": "106.5",
//                "direction": "",
//                "gpa": "3.15",
//                "grade": "2022",
//                "major": "信息与计算科学",
//                "major_depart": "数学与大数据学院",
//                "name": "薛景",
//                "required_credits": "182.5",
//                "std_type": "本科"
        if(plan_completion_results!=null){
            if(plan_completion_results.has("plan_completion_base_info")){
                JsonObject plan_completion_base_info=plan_completion_results.get("plan_completion_base_info").getAsJsonObject();
                String audit_date_time = plan_completion_base_info.get("audit_date_time").getAsString();
                String audit_result = plan_completion_base_info.get("audit_result").getAsString();
                String code = plan_completion_base_info.get("code").getAsString();
                String completed_credits = plan_completion_base_info.get("completed_credits").getAsString();
                String gpa = plan_completion_base_info.get("gpa").getAsString();
                String grade = plan_completion_base_info.get("grade").getAsString();
                String major = plan_completion_base_info.get("major").getAsString();
                String name = plan_completion_base_info.get("name").getAsString();
                String major_depart = plan_completion_base_info.get("major_depart").getAsString();
                String required_credits = plan_completion_base_info.get("required_credits").getAsString();
                String std_type = plan_completion_base_info.get("std_type").getAsString();

                // 创建一个Map来存储键值对
                Map<String, String> PlankeyValuePairs = new HashMap<>();

                // 将值和名字形成键值对存储在Map中
                PlankeyValuePairs.put("审核时间:", audit_date_time);
                PlankeyValuePairs.put("审核结果:", audit_result);
                PlankeyValuePairs.put("学号:", code);
                PlankeyValuePairs.put("GPA:", gpa);
                PlankeyValuePairs.put("专业:", major);
                PlankeyValuePairs.put("姓名:", name);
                PlankeyValuePairs.put("专业院系:", major_depart);
                //1
                initCirculeViewUI(completed_credits,required_credits);
                //2
                TextView creditsTextView =new TextView(this);
                creditsTextView.setText(completed_credits+"/"+required_credits+"\n"+"实修/要求");
                creditsTextView.setGravity(Gravity.CENTER);
                plan_complete_linearLayout.addView(creditsTextView);
                //3
                initContentUI(PlankeyValuePairs);
                //4
                LinearLayout warning_linearlayout = new LinearLayout(this);
                warning_linearlayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams warningParams = new LinearLayout.LayoutParams(50, 50); // 这里的数字是宽和高，单位是像素
                warningParams.gravity = Gravity.CENTER_VERTICAL;
                ImageView warningImageView =new ImageView(this);
                warningImageView.setImageResource(R.drawable.warning);
                warningImageView.setLayoutParams(warningParams);

                TextView warningTextView =new TextView(this);
                warningTextView.setText("培养计划完成情况，不作为最终审核结果");
                warningTextView.setTextColor(getResources().getColor(R.color.grey));
                warningTextView.setGravity(Gravity.CENTER);
                warning_linearlayout.addView(warningImageView);
                warning_linearlayout.addView(warningTextView);
                plan_complete_linearLayout.addView(warning_linearlayout);

                //5
                // 设置顶部Margin
                LinearLayout.LayoutParams studyDetailsButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                studyDetailsButtonParams.setMargins(0, 20, 0, 0);

                Button studyDetailsButton =new Button(this);
                studyDetailsButton.setText("修读详情");
                studyDetailsButton.setLayoutParams(studyDetailsButtonParams);
                studyDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 将JsonArray转换为String
                        JsonArray plan_course_group_audit_results = plan_completion_results.getAsJsonArray("plan_course_group_audit_results");
                        // 将JsonArray转换为String
                        String jsonString = plan_course_group_audit_results.toString();
                        //跳转修读详情
                        // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                        Intent intent = new Intent(PlanCompletionActivity.this, StudyDetailsActivity.class);
                        intent.putExtra("json_data", jsonString);
                        startActivity(intent);
                    }
                });

                // 设置圆角背景
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20); // 这里的数字表示圆角半径
                shape.setColor(getResources().getColor(R.color.bule_white1)); // 背景颜色

                // 将背景应用到按钮上
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    studyDetailsButton.setBackgroundDrawable(shape);
                } else {
                    studyDetailsButton.setBackground(shape);
                }
                plan_complete_linearLayout.addView(studyDetailsButton);
            }
        }
    }

    private void initContentUI(Map<String, String> PlankeyValuePairs) {
        LinearLayout plan_completion_content_linearlayout = new LinearLayout(this);
        plan_completion_content_linearlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,5,0,5);

        String[] PlanKeys={"学号:","姓名:","专业院系:","专业:","GPA:","审核结果:","审核时间:"};
        LinearLayout.LayoutParams textViewParams=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        for (String key : PlanKeys) {
            String value = PlankeyValuePairs.get(key);
            LinearLayout linearLayout =new LinearLayout(this);
            linearLayout.setLayoutParams(params);
            TextView TextViewHeader =new TextView(this);
            TextViewHeader.setText(key);
            TextViewHeader.setLayoutParams(textViewParams); // 设置权重为1，使其占据右侧空间
            TextViewHeader.setTextSize(13);
            TextView TextViewContent =new TextView(this);
            TextViewContent.setGravity(Gravity.RIGHT); // 将内容设置为右对齐
            TextViewContent.setText(value);
            TextViewContent.setLayoutParams(textViewParams); // 设置权重为1，使其占据右侧空间
            TextViewContent.setTextSize(13);
            linearLayout.addView(TextViewHeader);
            linearLayout.addView(TextViewContent);
            plan_completion_content_linearlayout.addView(linearLayout);
        }

        plan_complete_linearLayout.addView(plan_completion_content_linearlayout);
    }

    private void initCirculeViewUI(String completed_credits,String required_credits){
        MyView myView = new MyView(this);
        // 创建LayoutParams并设置宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 420);
        myView.setLayoutParams(params);
        float v = (Float.parseFloat(completed_credits)/Float.parseFloat(required_credits))*360;
        myView.setAnnularData(v, 0, 0, 0);
        plan_complete_linearLayout.addView(myView);
    }
}
