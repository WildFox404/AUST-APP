package com.example.newapp.secondclass;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.secondclassactivity.SecondClassCommentContent;
import com.example.newapp.secondclassactivity.SecondClassGradeListActivity;
import com.example.newapp.secondclassactivity.SecondClassMyGradeActivity;
import com.example.newapp.secondclassactivity.SecondClassUserInfoActivity;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class SecondClassUserFragment extends Fragment {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private View view;
    private JsonObject my_info_result;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.secondclassuserview, container, false);

        ImageView my_grade_button = view.findViewById(R.id.my_grade_button);
        my_grade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassGradeListActivity.class);
                startActivity(intent);
            }
        });

        new UserInfoAsyncTask().execute();
        return view;
    }

    private class UserInfoAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return secondClassUser.getMyInfoReturn();
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            if (result!=null) {
                // Assuming grade_result is a field in your class to store the JSON data
                my_info_result=result;
            }
            initUI();
        }
    }

    private void initUI() {
        LinearLayout user_info_layout = view.findViewById(R.id.user_info_layout);
        TextView user_name = view.findViewById(R.id.user_name);
        TextView user_depart = view.findViewById(R.id.user_depart);
        TextView user_major = view.findViewById(R.id.user_major);
        ImageView user_avatar = view.findViewById(R.id.user_avatar);

        String avatar = my_info_result.get("avatar") != null && !my_info_result.get("avatar").isJsonNull() ? my_info_result.get("avatar").getAsString() : "";
        String name = my_info_result.get("name").getAsString();
        String collegeName = my_info_result.get("collegeName").getAsString();
        String majorName = my_info_result.get("majorName").getAsString();
        String id = my_info_result.get("id").getAsString();

        //姓名
        user_name.setText(name);
        user_depart.setText(collegeName);
        user_major.setText(majorName);
        //用户头像
        if (!avatar.equals("")) {
            Picasso.get().load(avatar).into(user_avatar);
        }
        //跳转用户信息
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassUserInfoActivity.class);
                intent.putExtra("user_id",id);
                startActivity(intent);
            }
        });
        user_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SecondClassUserInfoActivity.class);
                intent.putExtra("user_id",id);
                startActivity(intent);
            }
        });
    }
}
