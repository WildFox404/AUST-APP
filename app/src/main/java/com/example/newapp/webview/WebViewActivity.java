package com.example.newapp.webview;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // 返回到 WebView 的上一页
        } else {
            super.onBackPressed(); // 如果 WebView 已经到达第一页，则执行默认的返回操作
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        String url = getIntent().getStringExtra("url");
        webView = findViewById(R.id.webView);


        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 加载网页
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //设置支持js
        webSettings.setJavaScriptEnabled(true);
        //设置适应屏幕
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setPluginState(WebSettings.PluginState.ON);//设置是否支持插件
        webSettings.setSupportZoom(true); //支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //设置存储模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        //setDomStorageEnabled解决了webview白屏问题  设置支持DomStorage
        webSettings.setDomStorageEnabled(true);
        //设置支持本地存储
        webSettings.setDatabaseEnabled(true);
        //设置缓存
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webView.setNetworkAvailable(true); // 设置网络可用
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                // 上面的参数中，url对应文件下载地址，mimetype对应下载文件的MIME类型
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // 在这里处理加载错误
                if (error != null) {
                    int errorCode = error.getErrorCode();
                    String description = error.toString();
                    // 输出错误信息
                    Log.e("WebView Error", "Error Code: " + errorCode + ", Description: " + description);
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                WebView.HitTestResult hit = view.getHitTestResult();
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (TextUtils.isEmpty(hit.getExtra()) || hit.getType() == 0) {
                    //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                    Log.e("重定向", "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                    Log.e("重定向", "GetURL: " + view.getUrl() + "\n" +"getOriginalUrl()"+ view.getOriginalUrl());
                    Log.d("重定向", "URL: " + url);
                }

                if (url.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                    view.loadUrl(url);
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走

                } else { //加载的url是自定义协议地址
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                // 创建一个ObjectAnimator对象，设置旋转属性为rotation，旋转范围为0到360度
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl(url);
            }
        });

        findViewById(R.id.refresh_text).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(findViewById(R.id.refresh), "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl(url);
            }
        });
    }
}
