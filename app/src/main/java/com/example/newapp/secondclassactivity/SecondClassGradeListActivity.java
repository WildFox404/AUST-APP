package com.example.newapp.secondclassactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.view.RadarView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class SecondClassGradeListActivity extends AppCompatActivity {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private Integer pageNum=1;
    private long lastTime = System.currentTimeMillis()-120;
    private JsonObject GradeList;
    private JsonObject GradeListDetail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassgradelistview);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView ranking_textview = findViewById(R.id.ranking_textview);
        ranking_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondClassGradeListActivity.this, SecondClassRankingActivity.class);
                startActivity(intent);
            }
        });
        new GradeListAsyncTask().execute();
        new GradeListDetailAsyncTask().execute();
    }

    private class GradeListAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getGradeList();
            } catch (Exception e) {
                Log.e("用户信息", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                GradeList = result;
                Log.d("用户信息", "用户信息获取成功");
                Log.d("用户信息", result.toString());
                updateGradeListUI();
            }
        }
    }

    private void updateGradeListUI() {
        JsonObject user = GradeList.get("user").getAsJsonObject();
        String hours_result = user.get("hours").getAsString();
        String code = user.get("code").getAsString();
        String className = user.get("className").getAsString();
        String score_result = user.get("score").getAsString();
        String collegeName = user.get("collegeName").getAsString();
        String grade_result = user.get("grade").getAsString();
        String name_user = user.get("name").getAsString();
        String majorName = user.get("majorName").getAsString();

        TextView user_name = findViewById(R.id.user_name);
        TextView user_depart = findViewById(R.id.user_depart);
        TextView user_major = findViewById(R.id.user_major);
        TextView hours = findViewById(R.id.hours);
        TextView score = findViewById(R.id.score);
        TextView grade = findViewById(R.id.grade);

        //姓名
        user_name.setText(name_user);
        //学院
        user_depart.setText(collegeName);
        //专业
        user_major.setText(majorName);
        //学时
        hours.setText(hours_result);
        //学分
        score.setText(score_result);
        //年纪
        grade.setText(grade_result);

        ArrayList<Double> percentList = new ArrayList<>(); // 创建Double类型的动态数组
        ArrayList<String> titleList = new ArrayList<>(); // 创建String类型的动态数组

        JsonArray tags = GradeList.get("tags").getAsJsonArray();
        for (JsonElement tag : tags) {
            try {
                JsonObject tag_object = tag.getAsJsonObject();
                String showUserValue = tag_object.get("showUserValue").getAsString();
                Double UserValue = Double.valueOf(showUserValue);
                Double minHours = tag_object.get("minHours").getAsDouble();
                String name_type = tag_object.get("name").getAsString();
                titleList.add(name_type);
                if(!minHours.equals(0.0)){
                    percentList.add(UserValue/minHours);
                }else{
                    percentList.add(1.0);
                }
            }catch (Exception e){
                Log.e("二课成绩单", "发生错误: "+e.toString());
            }
        }

        // 将动态数组转换为数组
        Double[] percents = percentList.toArray(new Double[0]);
        String[] titles = titleList.toArray(new String[0]);
        LinearLayout graph_view = findViewById(R.id.graph_view);
        RadarView radarView = new RadarView(this);
        radarView.setPercents(percents);
        radarView.setTitles(titles);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        radarView.setLayoutParams(params);

        graph_view.addView(radarView);
    }

    private class GradeListDetailAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getGradeListDetail(pageNum);
            } catch (Exception e) {
                Log.e("用户信息", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                GradeListDetail = result;
                Log.d("用户信息", "用户信息获取成功");
                Log.d("用户信息", result.toString());
                updateGradeListDetailUI();
            }
        }
    }

    private void updateGradeListDetailUI() {
        LinearLayout grade_list_layout = findViewById(R.id.grade_list_layout);

        Integer total = GradeListDetail.get("total").getAsInt();
        Integer pageSize = GradeListDetail.get("pageSize").getAsInt();
        JsonArray list_results = GradeListDetail.get("list").getAsJsonArray();

        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();
        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String minHours = list_object.get("minHours").getAsString();
                String minScore = list_object.get("minScore").getAsString();
                String rtHours = list_object.get("rtHours").getAsString();
                String classifyName = list_object.get("classifyName").getAsString();

                View grade_list_view =new View(this);
                grade_list_view = inflater.inflate(R.layout.secondclassgradelistcontent, null);
                TextView type_name = grade_list_view.findViewById(R.id.type_name);
                TextView high_low_grade = grade_list_view.findViewById(R.id.high_low_grade);
                TextView grade_view = grade_list_view.findViewById(R.id.grade_view);
                TextView require_grade = grade_list_view.findViewById(R.id.require_grade);

                type_name.setText(classifyName);
                high_low_grade.setText("在校最低"+minHours+"学时\t在校最低"+minScore+"学分");
                grade_view.setText("\t"+ rtHours);
                if(Double.valueOf(rtHours)<Double.valueOf(minHours)){
                    require_grade.setText("还差\t"+String.valueOf(Double.valueOf(minHours)-Double.valueOf(rtHours))+"学时");
                    require_grade.setTextColor(getResources().getColor(R.color.fire_red));
                }else{
                    require_grade.setText("已修满");
                    require_grade.setTextColor(getResources().getColor(R.color.green));
                }

                grade_list_layout.addView(grade_list_view);
            }catch (Exception e){
                Log.e("二课成绩单详情", "发生错误: "+e.toString());
            }
        }
    }
}
