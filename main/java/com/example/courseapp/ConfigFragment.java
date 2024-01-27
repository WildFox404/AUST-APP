package com.example.courseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ConfigFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private EditText studentNumberEditText;
    private EditText passwordEditText;
    private SharedViewModel viewModel;

    public ConfigFragment() {
        // Required empty public constructor
    }
    // 获取EditText的内容

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        Button configSaveButton = view.findViewById(R.id.config_save);
        studentNumberEditText = view.findViewById(R.id.config_edittext_number);
        passwordEditText = view.findViewById(R.id.config_edittext_password);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // 从SharedPreferences加载之前保存的内容
        String savedStudentNumber = sharedPreferences.getString("studentNumber", "");
        String savedPassword = sharedPreferences.getString("password", "");
        studentNumberEditText.setText(savedStudentNumber);
        passwordEditText.setText(savedPassword);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        User user = new User(savedStudentNumber, savedPassword);
        viewModel.setUser(user);
        // 保存按钮点击事件
        configSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存用户输入的内容到SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("studentNumber", studentNumberEditText.getText().toString());
                editor.putString("password", passwordEditText.getText().toString());
                editor.apply();
                Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                // 重新加载当前Fragment
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.detach(ConfigFragment.this).attach(ConfigFragment.this).commit();
                User user = new User(studentNumberEditText.getText().toString(), passwordEditText.getText().toString());
                viewModel.setUser(user);
            }
        });

        return view;
    }

}