package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class UserFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;
    private SharedViewModel viewModel;
    private User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象

        View view = inflater.inflate(R.layout.user, container, false);
        ImageView loginOut= view.findViewById(R.id.loginOut);

        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取SharedPreferences的编辑器
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // 删除ids属性
                editor.remove("ids");
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
