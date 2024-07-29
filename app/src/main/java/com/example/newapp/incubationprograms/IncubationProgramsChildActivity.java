package com.example.newapp.incubationprograms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.view.CircleView;
import com.example.newapp.R;
import com.example.newapp.entries.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class IncubationProgramsChildActivity extends AppCompatActivity {

    private User user =User.getInstance();
    private JsonObject incubation_programs_results;
    private LinearLayout incubation_linearlayout;
    private String incubation_data;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incubationprogramschildview);
        Intent intent =getIntent();
        incubation_data=intent.getStringExtra("incubation_data");

        incubation_linearlayout=findViewById(R.id.incubation);

        ImageView exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new IncubationProgramsAsyncTask().execute();
    }

    private class IncubationProgramsAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return user.get_incubation_programs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                incubation_programs_results = result;
                Log.d("考试安排获取", "考试安排获取成功");
            }
            if(incubation_data.equals("incubation1")){
                initUI1();
            }else if(incubation_data.equals("incubation2")){
                initUI2();

            }else{
                //啥也不做
            }
        }
    }

    private void initUI2() {
        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 加载XML定义的布局文件
        // 创建一个GradientDrawable对象
        GradientDrawable border = new GradientDrawable();
        // 设置边框颜色和宽度
        border.setStroke(1, Color.BLACK);
        // 设置圆角半径
        border.setCornerRadius(10);

        JsonArray plan_course_groups = incubation_programs_results.getAsJsonArray("plan_course_groups");
        for (JsonElement plan_course_group : plan_course_groups) {
            JsonObject plan_course_group_object = plan_course_group.getAsJsonObject();
            JsonArray plan_course_groups_jsonarrays = plan_course_group_object.get("plan_course_groups").getAsJsonArray();
            for (JsonElement plan_course_groups_jsonarray : plan_course_groups_jsonarrays) {
                JsonObject plan_course_groups_jsonobject = plan_course_groups_jsonarray.getAsJsonObject();
                JsonArray plan_courses = plan_course_groups_jsonobject.get("plan_courses").getAsJsonArray();
                for (JsonElement plan_course : plan_courses) {
                    JsonObject plan_course_object = plan_course.getAsJsonObject();
                    String compulsory = plan_course_object.get("compulsory").getAsString();
                    String course_code =plan_course_object.get("course_code").getAsString();
                    String course_name =plan_course_object.get("course_name").getAsString();
                    String credits =plan_course_object.get("credits").getAsString();
                    String period =plan_course_object.get("period").getAsString();

                    View view =new View(this);
                    view = inflater.inflate(R.layout.incubationprogramscourseview, null);
                    if(compulsory.equals("false")){
                        ImageView plan_status = view.findViewById(R.id.plan_status);
                        plan_status.setImageResource(R.drawable.unpassed);
                    }
                    TextView course_head =view.findViewById(R.id.course_head);
                    course_head.setText("["+course_code+"]"+course_name);

                    TextView course_credits=view.findViewById(R.id.course_credits);
                    course_credits.setText("学分"+credits);

                    TextView course_num=view.findViewById(R.id.course_num);
                    course_num.setText("学时"+period);

                    view.setBackground(border);
                    incubation_linearlayout.addView(view);
                }
            }
        }
    }
    private void initUI1() {
        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 加载XML定义的布局文件
        View view =new View(this);
        view = inflater.inflate(R.layout.incubationprogramscontent, null);
        ArrayList<TextView> textview1_list =new ArrayList<>();
        ArrayList<TextView> textview2_list =new ArrayList<>();
        textview1_list.add(view.findViewById(R.id.textview11));
        textview1_list.add(view.findViewById(R.id.textview21));
        textview1_list.add(view.findViewById(R.id.textview31));
        textview1_list.add(view.findViewById(R.id.textview41));

        textview2_list.add(view.findViewById(R.id.textview12));
        textview2_list.add(view.findViewById(R.id.textview22));
        textview2_list.add(view.findViewById(R.id.textview32));
        textview2_list.add(view.findViewById(R.id.textview42));

        ArrayList<Float> credits_list =new ArrayList<>();
        Float total_credits=(float)0;

        int i =0;
        LinearLayout incubation_programs_child_linearlayout =new LinearLayout(this);
        JsonArray plan_course_groups = incubation_programs_results.getAsJsonArray("plan_course_groups");
        for (JsonElement plan_course_group : plan_course_groups) {
            JsonObject plan_course_group_object = plan_course_group.getAsJsonObject();
            String course_type_name = plan_course_group_object.get("course_type_name").getAsString();
            Float credits = plan_course_group_object.get("credits").getAsFloat();
            credits_list.add(credits);
            total_credits+=credits;

            textview2_list.get(i).setText(String.valueOf(credits));
            i++;
        }

        ArrayList<Float> angle_list =new ArrayList<>();
        i=0;
        for(Float credits:credits_list){
            float v = (credits/total_credits)*360;
            String formattedResult = String.format("%.2f", v/360);
            textview1_list.get(i).setText(formattedResult+"%");
            Log.d("IncubationPrograms", String.valueOf(v));
            angle_list.add(v);
            i++;
        }
        CircleView circleView = new CircleView(this);
        // 创建LayoutParams并设置宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 420);
        circleView.setLayoutParams(params);
        circleView.setAnnularData(angle_list.get(0), angle_list.get(1), angle_list.get(2), angle_list.get(3));
        incubation_linearlayout.addView(circleView);
        incubation_linearlayout.addView(view);
    }

}
