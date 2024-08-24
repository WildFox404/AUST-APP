package com.example.newapp.secondclassactivity;

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
import com.example.newapp.secondclass.SecondClassTodoFragment;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SecondClassPersonalRewardsActivity extends AppCompatActivity {
    private JsonArray personalreward_jsonarray;
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclasspersonalrewards);
        String jumpInfo = getIntent().getStringExtra("jumpInfo");

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new PersonalRewardAsyncTask(jumpInfo).execute();
    }


    private class PersonalRewardAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        private String jumpInfo;
        private PersonalRewardAsyncTask(String jumpInfo){
            this.jumpInfo =jumpInfo;
        }
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return secondClassUser.getPersonalRewards(jumpInfo);
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
                personalreward_jsonarray = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateUI();
            }
        }
    }

    private void updateUI() {
        // 在Fragment中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout personal_rewards =findViewById(R.id.personal_rewards);
        personal_rewards.removeAllViews();

        for (JsonElement personalreward_element : personalreward_jsonarray) {
            try {
                JsonObject personalreward_object = personalreward_element.getAsJsonObject();
                String hours = personalreward_object.get("hours").getAsString();
                String name = personalreward_object.get("name").getAsString();
                String ctime = personalreward_object.get("ctime").getAsString();
                String remark = personalreward_object.get("remark").getAsString();
                String id = personalreward_object.get("id").getAsString();
                String classifyName = personalreward_object.get("classifyName").getAsString();

                View secondclasspersonalreward = new View(this);
                secondclasspersonalreward = inflater.inflate(R.layout.secondclasspersonalrewardscontent, null);
                TextView personal_rewards_type = secondclasspersonalreward.findViewById(R.id.personal_rewards_type);
                TextView personal_rewards_time = secondclasspersonalreward.findViewById(R.id.personal_rewards_time);
                TextView personal_rewards_name = secondclasspersonalreward.findViewById(R.id.personal_rewards_name);
                TextView personal_rewards_score = secondclasspersonalreward.findViewById(R.id.personal_rewards_score);
                TextView personal_rewards_remark = secondclasspersonalreward.findViewById(R.id.personal_rewards_remark);

                personal_rewards_type.setText(classifyName);
                personal_rewards_time.setText(DateUtils.convertTimestampToDate(ctime));
                personal_rewards_name.setText(name);
                personal_rewards_score.setText("+"+hours+"学时");
                personal_rewards_remark.setText("备注:"+remark);

                personal_rewards.addView(secondclasspersonalreward);
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }
    }
}
