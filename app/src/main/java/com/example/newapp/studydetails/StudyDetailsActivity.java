package com.example.newapp.studydetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.utils.DeviceDataUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class StudyDetailsActivity extends AppCompatActivity {
    private DeviceDataUtils deviceDataUtils;
    private JsonArray jsonArrayDatas;
    private LinearLayout study_details_linearLayout;
    private ExpandableListView study_details_expandablelistview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studydetailsview);

        // 获取从上一个Activity传递过来的Json字符串
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("json_data");
        study_details_expandablelistview=findViewById(R.id.study_details_expandablelistview);
        // 将获取到的字符串解析成Json数组
        jsonArrayDatas = new Gson().fromJson(jsonString, JsonArray.class);

        deviceDataUtils = DeviceDataUtils.getInstance();
        ImageView exitButton = findViewById(R.id.exitButton);

        study_details_linearLayout = findViewById(R.id.study_details_linearLayout);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initUI();
    }

    private void initUI() {
        ArrayList<StudyDetailsLevel1> grade_list =new ArrayList<>();
        ArrayList<ArrayList<StudyDetailsLevel2>> item_list=new ArrayList<>();
        for (JsonElement jsonArrayData : jsonArrayDatas) {
            JsonObject jsonObjectData = jsonArrayData.getAsJsonObject();
            String course_type_name = jsonObjectData.get("course_type_name").getAsString();
            String credits_completed = jsonObjectData.get("credits_completed").getAsString();
            String credits_required = jsonObjectData.get("credits_required").getAsString();
            String num_completed = jsonObjectData.get("num_completed").getAsString();
            String num_required = jsonObjectData.get("num_required").getAsString();
            JsonArray plan_course_group_audit_results = jsonObjectData.getAsJsonArray("plan_course_group_audit_results");
            String relation_text = jsonObjectData.get("relation_text").getAsString();
            ArrayList<StudyDetailsLevel2> item_list_child=new ArrayList<>();
            for (JsonElement plan_course_group_audit_result : plan_course_group_audit_results) {
                JsonObject plan_course_group_Data = plan_course_group_audit_result.getAsJsonObject();
                String group_course_type_name = plan_course_group_Data.get("course_type_name").getAsString();
                String group_credits_completed = plan_course_group_Data.get("credits_completed").getAsString();
                String group_credits_required = plan_course_group_Data.get("credits_required").getAsString();
                String group_num_completed = plan_course_group_Data.get("num_completed").getAsString();
                String group_num_required = plan_course_group_Data.get("num_required").getAsString();
                JsonArray group_plan_course_audit_results = plan_course_group_Data.getAsJsonArray("plan_course_audit_results");
                String group_plan_course_audit_string_results = new Gson().toJson(group_plan_course_audit_results);
                Log.d("StudyDetailsActivity", "item创建"+group_plan_course_audit_string_results);
                StudyDetailsLevel2 item = new StudyDetailsLevel2(group_course_type_name,group_credits_completed,group_credits_required,group_num_completed,group_num_required,group_plan_course_audit_string_results);
                item_list_child.add(item);
            }
            item_list.add(item_list_child);
            StudyDetailsLevel1 grade =new StudyDetailsLevel1(course_type_name,credits_completed,credits_required,num_completed,num_required,relation_text);
            grade_list.add(grade);
        }
        if (grade_list.size() == 0) {
            //无记录
        } else {
            study_details_expandablelistview.setAdapter(new StudyDetailsAdapter(this, grade_list,item_list));
        }
    }
}
