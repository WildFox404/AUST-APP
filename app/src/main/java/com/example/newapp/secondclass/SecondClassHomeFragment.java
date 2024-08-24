package com.example.newapp.secondclass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.secondclassactivity.SecondClassActivity;
import com.example.newapp.secondclassactivity.SecondClassGradeListActivity;
import com.example.newapp.secondclassactivity.SecondClassMyGradeActivity;
import com.example.newapp.secondclassactivity.SecondClassPersonalRewardsActivity;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.android.material.internal.ViewUtils.hideKeyboard;

public class SecondClassHomeFragment extends Fragment {
    private View view;
    private JsonObject content_jsonobject;
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.secondclasshomeview, container, false);

        EditText find_edittext =view.findViewById(R.id.find_edittext);
        // 设置点击事件监听器
        find_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    // 在这里执行隐藏键盘的操作
                    hideKeyboard(find_edittext);
                    return true;
                }
                return false;
            }
        });

        LinearLayout my_grade = view.findViewById(R.id.my_grade);
        my_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassMyGradeActivity.class);
                startActivity(intent);
            }
        });

        ImageView activity_activity =view.findViewById(R.id.activity_activity);
        TextView more_activity =view.findViewById(R.id.more_activity);
        activity_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassActivity.class);
                startActivity(intent);
            }
        });
        more_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassActivity.class);
                startActivity(intent);
            }
        });

        ImageView grade_list = view.findViewById(R.id.grade_list);
        grade_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassGradeListActivity.class);
                startActivity(intent);
            }
        });

        new HomeContentAsyncTask().execute();
        return view;
    }

    private class HomeContentAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getHomeContent();
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                content_jsonobject = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateUI();
            }
        }
    }

    private void updateUI() {

        JsonObject stuData = content_jsonobject.get("stuData").getAsJsonObject();
        JsonArray stuData_list = stuData.get("list").getAsJsonArray();
        JsonObject stuActivity = content_jsonobject.get("stuActivity").getAsJsonObject();
        JsonArray stuActivity_list = stuActivity.get("list").getAsJsonArray();

        for (JsonElement stuData_list_element : stuData_list) {
            try {
                JsonObject stuData_list_object = stuData_list_element.getAsJsonObject();
                Integer orderNo = stuData_list_object.get("orderNo").getAsInt();
                String score = stuData_list_object.get("score").getAsString();
                switch(orderNo){
                    case 1 :
                        TextView scoreReportShow = view.findViewById(R.id.scoreReportShow);
                        scoreReportShow.setText(score);
                        break;
                    case 2 :
                        TextView myAcitivty = view.findViewById(R.id.myAcitivty);
                        myAcitivty.setText(score);
                        break;
                    case 3 :
                        TextView myProjectSuccess = view.findViewById(R.id.myProjectSuccess);
                        myProjectSuccess.setText(score);
                        break;
                    default :

                }
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }

        // 在Fragment中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout command_activity =view.findViewById(R.id.command_activity);
        for (JsonElement stuActivity_list_element : stuActivity_list) {
            try {
                JsonObject stuActivity_list_object = stuActivity_list_element.getAsJsonObject();
                String id = stuActivity_list_object.get("id").getAsString();
                String hours = stuActivity_list_object.get("hours").getAsString();
                String address = stuActivity_list_object.get("address").getAsString();
                String organizationName = stuActivity_list_object.get("organizationName").getAsString();
                String peopleLimit = stuActivity_list_object.get("peopleLimit").getAsString();
                String name = stuActivity_list_object.get("name").getAsString();
                String logo = stuActivity_list_object.get("logo").getAsString();
                String startTime = stuActivity_list_object.get("enrollStartTime").getAsString();
                String endTime = stuActivity_list_object.get("enrollEndTime").getAsString();
                String enrollCount = stuActivity_list_object.get("enrollCount").getAsString();
                String classifyName = stuActivity_list_object.get("classifyName").getAsString();

                View stuActivity_list_view =new View(getContext());
                stuActivity_list_view = inflater.inflate(R.layout.secondclassactivity, null);
                TextView activity_type = stuActivity_list_view.findViewById(R.id.activity_type);
                TextView score = stuActivity_list_view.findViewById(R.id.score);
                TextView activity_name = stuActivity_list_view.findViewById(R.id.activity_name);
                TextView activity_time = stuActivity_list_view.findViewById(R.id.activity_time);
                TextView enroll_count = stuActivity_list_view.findViewById(R.id.enroll_count);
                TextView organization = stuActivity_list_view.findViewById(R.id.organizationName);
                TextView activity_address = stuActivity_list_view.findViewById(R.id.address);
                TextView enroll =stuActivity_list_view.findViewById(R.id.enroll);
                ImageView image = stuActivity_list_view.findViewById(R.id.image);
                //是否可报名
                if(Integer.parseInt(peopleLimit)>Integer.parseInt(enrollCount)){
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

                command_activity.addView(stuActivity_list_view);
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }
    }
}
