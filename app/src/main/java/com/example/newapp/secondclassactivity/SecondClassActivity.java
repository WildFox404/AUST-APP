package com.example.newapp.secondclassactivity;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class SecondClassActivity extends AppCompatActivity {
    private String params_ctime=DateUtils.getCurrentDate();
    private JsonArray activity_jsonarray;
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassactivityview);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new ActivityAsyncTask(params_ctime).execute();
    }

    private class ActivityAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        private String ctime;
        
        private ActivityAsyncTask(String ctime){
            this.ctime=ctime;
        }
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {
                return secondClassUser.getActivity(ctime);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                activity_jsonarray = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateUI();
            }
        }
    }

    private void updateUI() {
        // 在Fragment中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout activity_content =findViewById(R.id.activity_content);
        activity_content.removeAllViews();

        for (JsonElement activity_element : activity_jsonarray) {
            try {
                JsonObject activity_object = activity_element.getAsJsonObject();
                String id = activity_object.get("id").getAsString();
                String hours = activity_object.get("hours").getAsString();
                String address = activity_object.get("address").getAsString();
                String organizationName = activity_object.get("organizationName").getAsString();
                String peopleLimit = activity_object.get("peopleLimit").getAsString();
                String name = activity_object.get("name").getAsString();
                String logo = activity_object.get("logo").getAsString();
                String startTime = activity_object.get("enrollStartTime").getAsString();
                String endTime = activity_object.get("enrollEndTime").getAsString();
                String enrollCount = activity_object.get("enrollCount").getAsString();
                String classifyName = activity_object.get("classifyName").getAsString();

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
                        Intent intent = new Intent(SecondClassActivity.this, SecondClassActivityContent.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });
                //是否可报名
                if(Integer.parseInt(peopleLimit)>Integer.parseInt(enrollCount)&& DateUtils.isCurrentTimeInRange(startTime,endTime)){
                    enroll.setText("可报名");
                }else {
                    enroll.setText("已满员");
                }
                //地点
                activity_address.setText(address);
                //主办方类型
                organization.setText(organizationName);
                //报名人数
                enroll_count.setText(enrollCount+"/"+peopleLimit);
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

                activity_content.addView(stuActivity_list_view);
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }
    }
}
