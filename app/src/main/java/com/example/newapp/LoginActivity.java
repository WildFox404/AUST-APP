package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.entries.User;
import com.example.newapp.navigation.BottomNavigationViewActivity;
import android.view.inputmethod.InputMethodManager;
import java.io.IOException;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private User user=User.getInstance();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        EditText eduUserName =findViewById(R.id.editTextAccount);
        eduUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    // 在这里执行隐藏键盘的操作
                    hideKeyboard(eduUserName);
                    return true;
                }
                return false;
            }
        });
        EditText eduPassword =findViewById(R.id.editTextPassword);
        eduPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    // 在这里执行隐藏键盘的操作
                    hideKeyboard(eduPassword);
                    return true;
                }
                return false;
            }
        });

        ImageView agreementFalse = findViewById(R.id.agreementFalse);
        ImageView agreementTrue = findViewById(R.id.agreementTrue);
        ImageView loginView = findViewById(R.id.loginButton);

        int[] buildingImages = {R.drawable.chinabuilding1, R.drawable.chinabuilding2, R.drawable.chinabuilding3,
                R.drawable.chinabuilding4, R.drawable.chinabuilding5, R.drawable.chinabuilding8, 
                R.drawable.chinabuilding9, R.drawable.chinabuilding10, R.drawable.chinabuilding11,
                R.drawable.chinabuilding12};
        Random rand = new Random();
        int randomIndex = rand.nextInt(buildingImages.length);
        LinearLayout loginview_background =findViewById(R.id.loginview_background);
        loginview_background.setBackground(getResources().getDrawable(buildingImages[randomIndex]));
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
                    new GetLoginTask().execute();
                } else {
                    Toast.makeText(LoginActivity.this, "请填写完整信息并同意协议", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private class GetLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                user.login();
                String token=user.getToken();
                return !token.equals(""); // 返回登录是否成功
            } catch (IOException e) {
                Log.d("e",e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", user.getUsername());
                editor.putString("password", user.getPassword());
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, BottomNavigationViewActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "登录失败 账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}