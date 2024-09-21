package com.example.newapp.webview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;

public class SchoolWifiWebActivity extends AppCompatActivity {
    private static final String KEY_WIFI_SELECT = "wifi_yunying";
    private static final String KEY_WIFI_ACCOUNT = "wifi_account";
    private static final String KEY_WIFI_PASSWORD = "wifi_password";
    private static final String KEY_WIFI_COOKIE = "wifi_cookie";
    private String account = null;
    private String password = null;
    private WebView webView;
    private SharedPreferences sharedPreferences;
    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifiwebview);



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String account = sharedPreferences.getString(KEY_WIFI_ACCOUNT, "");
        String password = sharedPreferences.getString(KEY_WIFI_PASSWORD, "");
        Integer seletView = sharedPreferences.getInt(KEY_WIFI_SELECT, 1);
        String yunying;
        if(seletView==1){
            yunying="@unicom";
        }else if(seletView == 2){
            yunying="@cmcc";
        }else{
            yunying="@aust";
        }
        Log.d("SchoolWifiWebActivity", account);
        Log.d("SchoolWifiWebActivity", password);
        Log.d("SchoolWifiWebActivity", yunying);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setMixedContentMode(0);
        webView.getSettings().setBuiltInZoomControls(false);//123
        webView.getSettings().setDomStorageEnabled(true);
        //设置适应屏幕
        webView.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        webView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);//设置是否支持插件
        webView.getSettings().setSupportZoom(true); //支持缩放
        webView.getSettings().setDisplayZoomControls(false); //隐藏原生的缩放控件
        //设置存储模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        //设置支持本地存储
        webView.getSettings().setDatabaseEnabled(true);
        webView.loadUrl("http://10.255.0.19/a79.htm");
        webView.setWebViewClient(new WebViewClient() { // from class: wpxiao.connect.austwifi.MainActivity.1
            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView2, String str2) {
                super.onPageFinished(webView2, str2);
                webView.evaluateJavascript("document.getElementsByName('upass')[0].value = '" + password + "';document.getElementsByName('DDDDD')[0].value='" + account + "';", null);
                webView.evaluateJavascript("function selectOptionByNameAndValue(selectName, optionValue) {\n" +
                        "      // 找到 name 属性匹配的 select 元素\n" +
                        "      const selectElement = document.querySelector(`select[name=\"${selectName}\"]`);\n" +
                        "    \n" +
                        "      if (selectElement) {\n" +
                        "        // 设置选中的 value\n" +
                        "        selectElement.value = optionValue;\n" +
                        "        \n" +
                        "        // 触发 change 事件以确保选项被正确选择\n" +
                        "        const event = new Event('change', { bubbles: true });\n" +
                        "        selectElement.dispatchEvent(event);\n" +
                        "      } else {\n" +
                        "        console.error(`Select element with name \"${selectName}\" not found.`);\n" +
                        "      }\n" +
                        "    }\n" +
                        "    \n" +
                        "    // 使用示例\n" +
                        "    selectOptionByNameAndValue('ISP_select', '"+yunying+"');", null);
                webView.evaluateJavascript(" function clickInputByType(inputType) {\n" +
                        "      // 找到 type 属性匹配的 input 元素\n" +
                        "      const inputElement = document.querySelector(`input[type=\"${inputType}\"]`);\n" +
                        "    \n" +
                        "      if (inputElement) {\n" +
                        "        // 模拟点击事件\n" +
                        "        inputElement.click();\n" +
                        "      } else {\n" +
                        "        console.error(`Input element with type \"${inputType}\" not found.`);\n" +
                        "      }\n" +
                        "    }\n" +
                        "    \n" +
                        "    // 使用示例\n" +
                        "    clickInputByType('submit');", null);
            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                // 创建一个ObjectAnimator对象，设置旋转属性为rotation，旋转范围为0到360度
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl("http://10.255.0.19/a79.htm");
            }
        });

        findViewById(R.id.refresh_text).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(findViewById(R.id.refresh), "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl("http://10.255.0.19/a79.htm");
            }
        });
    }
}
