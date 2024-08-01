package com.example.newapp.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import com.example.newapp.*;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.Api;
import com.example.newapp.entries.SharedViewModel;
import com.example.newapp.entries.User;

import java.util.Random;

public class UserFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;
    private SharedViewModel viewModel;
    private User user=User.getInstance();
    private Api api =new Api();
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象

        view = inflater.inflate(R.layout.user, container, false);
        LinearLayout loginOut= view.findViewById(R.id.login_out);
        TextView user_name=view.findViewById(R.id.user_name);
        TextView depart_name=view.findViewById(R.id.depart_name);

        ImageView camping_image =view.findViewById(R.id.camping_image);
        int[] campingImages = {R.drawable.camping1, R.drawable.camping2,
                R.drawable.camping4, R.drawable.camping5, R.drawable.camping6, R.drawable.camping7,
                R.drawable.camping8, R.drawable.camping9, R.drawable.camping10, R.drawable.camping11,
                R.drawable.camping12};
        Random rand = new Random();
        int randomIndex = rand.nextInt(campingImages.length);
        camping_image.setImageResource(campingImages[randomIndex]);

        ImageView usericon=view.findViewById(R.id.usericon);
        int[] usericonImages = {R.drawable.useravataricon1, R.drawable.useravataricon3, R.drawable.useravataricon4, R.drawable.useravataricon5, R.drawable.useravataricon6,
                R.drawable.useravataricon7, R.drawable.useravataricon8, R.drawable.useravataricon9, R.drawable.useravataricon10,
                R.drawable.useravataricon11,R.drawable.useravataricon12,R.drawable.useravataricon13,R.drawable.useravataricon14,
                R.drawable.useravataricon15,R.drawable.useravataricon16};
        randomIndex = rand.nextInt(usericonImages.length);
        usericon.setImageResource(usericonImages[randomIndex]);

        user_name.setText(user.getUser_name());
        depart_name.setText(user.getDepart_name());
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取SharedPreferences的编辑器
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // 删除ids属性
                editor.remove("username");
                editor.remove("password");
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        new SentenceAsyncTask().execute();

        return view;
    }

    private class SentenceAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return api.get_sentence();
            } catch (Exception e) {
                Log.e("SentenceAsyncTask", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                Log.d("SentenceAsyncTask", "SentenceAsyncTask获取成功");
                InitSentence(result);
            }
        }
    }

    private void InitSentence(String result) {
        TextView sentence_textview =view.findViewById(R.id.sentence);
        sentence_textview.setText(result);
    }
}
