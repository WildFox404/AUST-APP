package com.example.newapp.secondclassactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.navigation.HomeFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class SecondClassRankingActivity extends AppCompatActivity {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private Integer pageNum=1;
    private Integer level=6;
    private long lastTime = System.currentTimeMillis()-220;
    private JsonObject RankingList;
    private JsonObject RankingSelf;
    private static final String KEY_RANKING = "ranking";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassrankingview);

        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedRanking = sharedPreferences.getString(KEY_RANKING, "0");
        if (savedRanking.equals("班级")) {
            level=10;
        } else if (savedRanking.equals("专业")) {
            level=9;
        } else if (savedRanking.equals("学院")) {
            level=8;
        } else {
            level=6;
        }
        Spinner rankingSpinner = findViewById(R.id.rankingSpinner);
        rankingSpinner.setSelection(getIndex(rankingSpinner, savedRanking));
        rankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_RANKING, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_RANKING, selectedTerm);
                    if (selectedTerm.equals("班级")) {
                        level=10;
                    } else if (selectedTerm.equals("专业")) {
                        level=9;
                    } else if (selectedTerm.equals("学院")) {
                        level=8;
                    } else {
                        level=6;
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    LinearLayout ranking_list_layout = findViewById(R.id.ranking_list);
                    ranking_list_layout.removeAllViews();
                    pageNum=1;
                    new RankingAsyncTask().execute();
                    new RankingSelfAsyncTask().execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new RankingAsyncTask().execute();
        new RankingSelfAsyncTask().execute();
    }

    private class RankingAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return secondClassUser.getRankingList(pageNum,level);
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
                RankingList = result;
                Log.d("用户信息", "用户信息获取成功");
                Log.d("用户信息", result.toString());
                updateRankingUI();
            }
        }
    }

    private void updateRankingUI() {
        LinearLayout ranking_list_layout = findViewById(R.id.ranking_list);

        Integer total = RankingList.get("total").getAsInt();
        Integer pageSize = RankingList.get("pageSize").getAsInt();
        JsonArray list_results = RankingList.get("list").getAsJsonArray();

        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();
        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String hours = list_object.get("hours").getAsString();
                String grade_result = list_object.get("grade").getAsString();
                String avatar = list_object.get("avatar") != null && !list_object.get("avatar").isJsonNull() ? list_object.get("avatar").getAsString() : "";
                String rownum = list_object.get("rownum").getAsString();
                String score = list_object.get("score").getAsString();
                String name = list_object.get("name").getAsString();
                String majorName = list_object.get("majorName").getAsString();
                String id = list_object.get("id").getAsString();

                View ranking_view =new View(this);
                ranking_view = inflater.inflate(R.layout.secondclassrankingcontent, null);
                LinearLayout root = ranking_view.findViewById(R.id.root);
                TextView ranking =ranking_view.findViewById(R.id.ranking);
                TextView user_name =ranking_view.findViewById(R.id.user_name);
                TextView user_major =ranking_view.findViewById(R.id.user_major);
                TextView grade =ranking_view.findViewById(R.id.grade);
                TextView user_score =ranking_view.findViewById(R.id.user_score);
                TextView user_hours =ranking_view.findViewById(R.id.user_hours);
                ImageView user_avatar =ranking_view.findViewById(R.id.user_avatar);

                //跳转用户信息
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassRankingActivity.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",id);
                        startActivity(intent);
                    }
                });
                //排名
                ranking.setText(rownum);
                //姓名
                user_name.setText(name);
                //专业
                user_major.setText(majorName);
                //年级
                grade.setText(grade_result+"级");
                //学分
                user_score.setText(score);
                //学时
                user_hours.setText(hours);
                //用户头像
                if (!avatar.equals("")) {
                    Picasso.get().load(avatar).into(user_avatar);
                }

                ranking_list_layout.addView(ranking_view);

            }catch (Exception e){
                Log.e("成绩排行榜", "出现错误: "+e.toString());
            }
        }

        ScrollView ranking_scrollview = findViewById(R.id.ranking_scrollview);
        ranking_scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int height = ranking_scrollview.getChildAt(0).getHeight();
                int scrollViewHeight = ranking_scrollview.getHeight();
                int diff = height - scrollViewHeight;

                if (System.currentTimeMillis() - lastTime < 70)
                    return;
                else {
                    lastTime = System.currentTimeMillis();
                    // rest of your code
                    if (scrollY >= (diff -20)) {
                        // ScrollView滑动到底部
                        // 执行相应的操作
                        pageNum++;
                        Log.d("成绩排行榜", "获取更多数据: ");
                        new RankingAsyncTask().execute();
                    } else {
                        // ScrollView未滑动到底部
                    }
                }
            }
        });
    }

    private class RankingSelfAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getRankingSelf(level);
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
                RankingSelf = result;
                Log.d("用户信息", "用户信息获取成功");
                Log.d("用户信息", result.toString());
                updateRankingSelfUI();
            }
        }
    }

    private void updateRankingSelfUI() {
        String hours = RankingSelf.get("hours").getAsString();
        String grade_result = RankingSelf.get("grade").getAsString();
        String avatar = RankingSelf.get("avatar") != null && !RankingSelf.get("avatar").isJsonNull() ? RankingSelf.get("avatar").getAsString() : "";
        String rownum = RankingSelf.get("rownum").getAsString();
        String score = RankingSelf.get("score").getAsString();
        String name = RankingSelf.get("name").getAsString();
        String majorName = RankingSelf.get("majorName").getAsString();
        String id = RankingSelf.get("id").getAsString();

        TextView user_name = findViewById(R.id.user_name);
        TextView user_major = findViewById(R.id.user_major);
        TextView my_score = findViewById(R.id.my_score);
        TextView my_ranking = findViewById(R.id.my_ranking);
        TextView my_hours = findViewById(R.id.my_hours);
        ImageView user_avatar = findViewById(R.id.user_avatar);

        //排名
        my_ranking.setText(rownum);
        //姓名
        user_name.setText(name);
        //专业
        user_major.setText(majorName);
        //学分
        my_score.setText(score);
        //学时
        my_hours.setText(hours);
        //用户头像
        if (!avatar.equals("")) {
            Picasso.get().load(avatar).into(user_avatar);
        }
    }

    private void saveSelection(String key, String value) {
        Log.d("HomeActivity", "saveSelection: "+key+":"+value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private int getIndex(Spinner spinner, String item) {
        if (item.equals("班级")) {
            level=10;
        } else if (item.equals("专业")) {
            level=9;
        } else if (item.equals("学院")) {
            level=8;
        } else {
            level=10;
        }
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0; // 默认返回第一个选项
    }
}
