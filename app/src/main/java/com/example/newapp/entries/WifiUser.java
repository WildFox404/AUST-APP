package com.example.newapp.entries;

import android.util.Log;
import com.example.newapp.utils.AesEcbUtil;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/** @noinspection ALL*/
public class WifiUser {
    private OkHttpClient client;
    private Gson gson = new Gson();
    private String yunying;
    private String cookie;
    private String currentAccount;
    private String currentPassword;
    public WifiUser(String yunying, String cookie, String currentAccount, String currentPassword) throws Exception {
        this.yunying=yunying;
        this.cookie=cookie;
        this.currentAccount=currentAccount;
        this.currentPassword=currentPassword;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new X509TrustManager[]{trustManager}, new SecureRandom());
        this.client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.getUnsafeSocketFactory(), (X509TrustManager) new UnsafeTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .build();

    }

    public Boolean getLogin() {
        String url = "http://10.255.0.19/drcom/login";
        Log.d("getLogin", "函数开始执行");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Cookie", "PHPSESSID="+cookie)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", "http://10.255.0.19/a79.htm")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("callback", "dr1004")
                .addQueryParameter("DDDDD", currentAccount+yunying)
                .addQueryParameter("upass", currentPassword)
                .addQueryParameter("0MKKey", "123456")
                .addQueryParameter("R1", "0")
                .addQueryParameter("R3", "0")
                .addQueryParameter("R6", "0")
                .addQueryParameter("para", "00")
                .addQueryParameter("v6ip", "");
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        for(int i=0;i<5;i++){
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // 将响应数据转换为字符串
                    String responseData = response.body().string();
                    Log.d("getLogin", responseData);
                    if(responseData.contains("error")||responseData.contains("Error")){
                        //请求出错
                    }else{
                        //请求成功
                        return true;
                    }
                }
            }catch (IOException e){
                Log.d("WifiUser", "发送错误: "+e.getMessage());
            }
            // 等待5秒钟
            try {
                Thread.sleep(500); // 5000毫秒 = 5秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    final X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
}
