package com.example.newapp.navigation;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.secondclass.SecondClassNavigationViewActivity;

import java.io.IOException;
import java.util.Random;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

public class SecondClassActivity extends AppCompatActivity {
    private SecondClassUser secondClassUser;
    private String savedUsername;
    private String savedPassword;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclass);
        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("username") && sharedPreferences.contains("password")) {
            savedUsername = sharedPreferences.getString("Secondusername", "");
            savedPassword = sharedPreferences.getString("Secondpassword", "");
            secondClassUser = secondClassUser.getInstance(savedUsername, savedPassword);

            // 使用 AsyncTask 执行登录操作
            new GetLoginTask().execute();
        } else {
            //什么也没发生
        }
        int[] buildingImages = {R.drawable.chinabuilding1, R.drawable.chinabuilding2, R.drawable.chinabuilding3,
                R.drawable.chinabuilding4, R.drawable.chinabuilding5, R.drawable.chinabuilding8,
                R.drawable.chinabuilding10, R.drawable.chinabuilding11,
                R.drawable.chinabuilding12};
        Random rand = new Random();
        int randomIndex = rand.nextInt(buildingImages.length);
        LinearLayout loginview_background =findViewById(R.id.secondclassview_background);

        loginview_background.setBackground(getResources().getDrawable(buildingImages[randomIndex]));

        EditText eduUserName =findViewById(R.id.editTextAccount);
        eduUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("RestrictedApi")
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
            @SuppressLint("RestrictedApi")
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

        ImageView loginView = findViewById(R.id.loginButton);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = eduUserName.getText().toString();
                String password = eduPassword.getText().toString();

                if (!userName.isEmpty() && !password.isEmpty()) {
                    // 两个 EditText 内容不为空且 agreementTrue 可见
                    secondClassUser.setUsername(userName);
                    secondClassUser.setPassword(password);
                    new GetLoginTask().execute();
                } else {
                    Toast.makeText(SecondClassActivity.this, "信息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GetLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                secondClassUser.login();
                String token=secondClassUser.getToken();
                return !token.equals(""); // 返回登录是否成功
            } catch (IOException e) {
                Log.d("二课登录异步",e.toString());
                return false;
            } catch (Exception e) {
                Log.d("二课登录异步",e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Secondusername", secondClassUser.getUsername());
                editor.putString("Secondpassword", secondClassUser.getPassword());
                editor.apply();

                Intent intent = new Intent(SecondClassActivity.this, SecondClassNavigationViewActivity.class);
                startActivity(intent);
                finish();
            }else{

            }
        }
    }
}
