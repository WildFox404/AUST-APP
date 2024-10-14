package com.example.newapp.navigation;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.db.MyDBHelper;

public class WebViewFragment extends Fragment {
    private View view;
    private WebView webView;
    private String url = "https://gmis.aust.edu.cn/pyxx/login.aspx";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.webview, container, false);
        webView = view.findViewById(R.id.webView);


        ImageView exitButton =view.findViewById(R.id.exitButton);
        exitButton.setImageDrawable(null);
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
        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                // 创建一个ObjectAnimator对象，设置旋转属性为rotation，旋转范围为0到360度
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl(url);
            }
        });

        view.findViewById(R.id.refresh_text).setOnClickListener(new View.OnClickListener() { // from class: wpxiao.connect.austwifi.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(view.findViewById(R.id.refresh), "rotation", 0f, 360f);
                rotationAnimator.setDuration(400); // 设置旋转动画的持续时间为1秒
                rotationAnimator.start(); // 启动旋转动画
                webView.loadUrl(url);
            }
        });
        return view;
    }
}
