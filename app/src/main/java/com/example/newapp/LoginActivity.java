package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    User user= new User("","");
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        EditText eduUserName =findViewById(R.id.editTextAccount);
        EditText eduPassword =findViewById(R.id.editTextPassword);
        ImageView agreementFalse = findViewById(R.id.agreementFalse);
        ImageView agreementTrue = findViewById(R.id.agreementTrue);
        ImageView loginView = findViewById(R.id.loginButton);
        agreementFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreementFalse.getVisibility() == View.VISIBLE) {
                    agreementFalse.setVisibility(View.INVISIBLE); // 隐藏 agreementFalse
                    agreementTrue.setVisibility(View.VISIBLE); // 显示 agreementTrue
                } else {
                    agreementFalse.setVisibility(View.VISIBLE); // 显示 agreementFalse
                    agreementTrue.setVisibility(View.INVISIBLE); // 隐藏 agreementTrue
                }
            }
        });

        agreementTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreementTrue.getVisibility() == View.VISIBLE) {
                    agreementTrue.setVisibility(View.INVISIBLE); // 隐藏 agreementTrue
                    agreementFalse.setVisibility(View.VISIBLE); // 显示 agreementFalse
                } else {
                    agreementTrue.setVisibility(View.VISIBLE); // 显示 agreementTrue
                    agreementFalse.setVisibility(View.INVISIBLE); // 隐藏 agreementFalse
                }
            }
        });

        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = eduUserName.getText().toString();
                String password = eduPassword.getText().toString();

                if (!userName.isEmpty() && !password.isEmpty() && agreementTrue.getVisibility() == View.VISIBLE) {
                    // 两个 EditText 内容不为空且 agreementTrue 可见
                    user.setUsername(userName);
                    user.setPassword(password);
                    new GetIdsTask().execute();
                } else {
                    Toast.makeText(LoginActivity.this, "请填写完整信息并同意协议", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class GetIdsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("",user.getUsername());
            Log.d("",user.getPassword());
            try {
                user.loginIn();
                user.get_ids();
            } catch (IOException e) {
                Log.d("e",e.toString());
                return "False";
            }
            return user.getIds();
        }

        @Override
        protected void onPostExecute(String ids) {
            Log.d("",ids);
            if(ids=="False"){
                Toast.makeText(LoginActivity.this, "链接失败 检查链接校园网", Toast.LENGTH_SHORT).show();
            }else if(ids==""){
                Toast.makeText(LoginActivity.this, "登录失败 账号或密码错误", Toast.LENGTH_SHORT).show();
            }else{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ids", ids);
                editor.putString("username", user.getUsername());
                editor.putString("password", user.getPassword());
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, BottomNavigationView.class);
                startActivity(intent);
                finish();
            }
        }
    }
}