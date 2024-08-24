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
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class SecondClassUserInfoActivity extends AppCompatActivity {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private JsonObject UserInfo;
    private JsonObject UserInfoActivity;
    private Integer pageNum=1;
    private String id;
    private ScrollView scrollView;
    private long lastTime = System.currentTimeMillis()-120;
    private Boolean follow_status=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassuserinfoview);

        id = getIntent().getStringExtra("user_id");

        scrollView = findViewById(R.id.user_info_activity);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new UserInfoAsyncTask().execute();
        new UserInfoActivityAsyncTask().execute();
    }

    private class UserInfoAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getUserInfo(id);
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
                UserInfo = result;
                Log.d("用户信息", "用户信息获取成功");
                Log.d("用户信息", result.toString());
                updateUserInfoUI();
            }
        }
    }

    private void updateUserInfoUI() {
        String hours_result = UserInfo.get("hours").getAsString();
        String politicsName_result = UserInfo.get("politicsName").getAsString();
        String gender_result = UserInfo.get("gender").getAsString();
        String organizationName_result = UserInfo.get("organizationName").getAsString();
        String avatar_result = UserInfo.get("avatar") != null && !UserInfo.get("avatar").isJsonNull() ? UserInfo.get("avatar").getAsString() : "";
        String dynamicCount_result = UserInfo.get("dynamicCount").getAsString();
        String organizationCount_result = UserInfo.get("organizationCount").getAsString();
        String activityCount_result = UserInfo.get("activityCount").getAsString();
        String collegeName_result = UserInfo.get("collegeName").getAsString();
        String isFollow_result = UserInfo.get("isFollow").getAsString();
        String name_result = UserInfo.get("name").getAsString();
        String majorName_result = UserInfo.get("majorName").getAsString();
        JsonArray personTag_results = UserInfo.get("personTag").getAsJsonArray();

        TextView hours = findViewById(R.id.hours);
        TextView politicsName = findViewById(R.id.politicsName);
        TextView gender = findViewById(R.id.gender);
        TextView organizationName = findViewById(R.id.organizationName);
        TextView dynamicCount = findViewById(R.id.dynamicCount);
        TextView organizationCount = findViewById(R.id.organizationCount);
        TextView activityCount = findViewById(R.id.activityCount);
        TextView collegeName = findViewById(R.id.collegeName);
        TextView name = findViewById(R.id.name);
        TextView majorName = findViewById(R.id.majorName);
        TextView personTag = findViewById(R.id.personTag);
        TextView follow_textview =findViewById(R.id.follow_textview);
        ImageView user_avatar = findViewById(R.id.user_avatar);

        //是否关注
        if(isFollow_result.equals("0")){
            //未关注
            follow_status=false;
            follow_textview.setBackground(getDrawable(R.drawable.button_background));
            follow_textview.setText("关注");
            follow_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new UserFollowAsyncTask().execute();
                }
            });
        }else{
            //已关注
            follow_status=true;
            follow_textview.setBackground(getDrawable(R.drawable.button_background1));
            follow_textview.setText("已关注");
            follow_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UserFollowAsyncTask().execute();
                }
            });
        }


        hours.setText("学时:"+hours_result);
        politicsName.setText("政治面貌:"+politicsName_result);
        if(gender_result.equals("2")){
            gender.setText("性别:女");
        }else{
            gender.setText("性别:男");
        }
        organizationName.setText("组织:"+organizationName_result);
        dynamicCount.setText("共"+dynamicCount_result+"个>");
        organizationCount.setText("共"+organizationCount_result+"个>");
        activityCount.setText("参与活动>共"+activityCount_result+"个");
        collegeName.setText("学院:"+collegeName_result);
        name.setText("姓名:"+name_result);
        majorName.setText("专业:"+majorName_result);
        //用户头像
        if (!avatar_result.equals("")) {
            Picasso.get().load(avatar_result).into(user_avatar);
        }

        String personTag_string ="";
        for (JsonElement personTag_result : personTag_results) {
            try {
                JsonObject personTag_object = personTag_result.getAsJsonObject();
                String personTag_name = personTag_object.get("name").getAsString();
                personTag_string += "["+personTag_name+"] ";
            }catch(Exception e){

            }
        }
        personTag.setText(personTag_string);
    }

    private class UserInfoActivityAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getUserInfoActivity(pageNum,id);
            } catch (Exception e) {
                Log.e("用户信息活动", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                UserInfoActivity = result;
                Log.d("用户信息活动", "用户信息活动获取成功");
                Log.d("用户信息活动", result.toString());
                updateUserInfoActivityUI();
            }
        }
    }

    private void updateUserInfoActivityUI() {
        LinearLayout layout_content = findViewById(R.id.layout_content);

        Integer total = UserInfoActivity.get("total").getAsInt();
        Integer pageSize = UserInfoActivity.get("pageSize").getAsInt();
        JsonArray list_results = UserInfoActivity.get("list").getAsJsonArray();

        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();
        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String id = list_object.get("id").getAsString();
                String hours = list_object.get("hours").getAsString();
                String address = list_object.get("address").getAsString();
                String organizationName = list_object.get("organizationName").getAsString();
                String name = list_object.get("name").getAsString();
                String logo = list_object.get("logo").getAsString();
                String startTime = list_object.get("startTime").getAsString();
                String endTime = list_object.get("endTime").getAsString();
                String enrollCount = list_object.get("enrollCount").getAsString();
                String classifyName = list_object.get("classifyName").getAsString();

                View stuActivity_list_view =new View(this);
                stuActivity_list_view = inflater.inflate(R.layout.secondclassactivity, null);
                LinearLayout main_layout =stuActivity_list_view.findViewById(R.id.main_layout);
                TextView activity_type = stuActivity_list_view.findViewById(R.id.activity_type);
                TextView score = stuActivity_list_view.findViewById(R.id.score);
                TextView activity_name = stuActivity_list_view.findViewById(R.id.activity_name);
                TextView activity_time = stuActivity_list_view.findViewById(R.id.activity_time);
                TextView enroll_count = stuActivity_list_view.findViewById(R.id.enroll_count);
                TextView organization = stuActivity_list_view.findViewById(R.id.organizationName);
                TextView activity_address = stuActivity_list_view.findViewById(R.id.address);
                TextView enroll =stuActivity_list_view.findViewById(R.id.enroll);
                ImageView image = stuActivity_list_view.findViewById(R.id.image);
                //跳转
                main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassUserInfoActivity.this, SecondClassActivityContent.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });
                enroll.setText("");
                //地点
                activity_address.setText(address);
                //主办方类型
                organization.setText(organizationName);
                //报名人数
                enroll_count.setText("报名人数:"+enrollCount);
                //活动时间
                activity_time.setText(DateUtils.convertTimestampToDate(startTime)+"--"+DateUtils.convertTimestampToDate(endTime));
                //活动学时
                score.setText(hours+"学时");
                //活动名称
                activity_name.setText(name);
                //活动类型
                activity_type.setText(classifyName);
                //活动图像
                Picasso.get().load(logo).into(image);

                layout_content.addView(stuActivity_list_view);
            }catch (Exception e){
                Log.e("用户的活动", "出现错误: "+e.toString());
            }
        }

        if(total<pageSize){
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });
        }else {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int height = scrollView.getChildAt(0).getHeight();
                    int scrollViewHeight = scrollView.getHeight();
                    int diff = height - scrollViewHeight;

                    if (System.currentTimeMillis() - lastTime < 100)
                        return;
                    else {
                        lastTime = System.currentTimeMillis();
                        // rest of your code
                        if (scrollY >= diff) {
                            // ScrollView滑动到底部
                            // 执行相应的操作
                            pageNum++;
                            Log.d("校友圈", "获取更多数据: ");
                            new UserInfoActivityAsyncTask().execute();
                        } else {
                            // ScrollView未滑动到底部
                        }
                    }
                }
            });
        }
    }

    private class UserFollowAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.followUserInfo(id,follow_status);
            } catch (Exception e) {
                Log.e("用户信息", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                new UserInfoAsyncTask().execute();
            }
        }
    }
}
