package com.example.newapp.entries;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.FaceDetector;
import com.example.newapp.utils.AesEcbUtil;
import com.example.newapp.utils.DateUtils;
import com.google.gson.*;
import android.text.TextUtils;
import android.util.Log;
import okhttp3.*;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
public class SecondClassUser {
    private String token;
    private String username;
    private String password;
    private String id;
    private static SecondClassUser instance;
    private OkHttpClient client;
    private Gson gson = new Gson();
    private String collegeName;
    private String[] years;
    private Integer grade;
    public SecondClassUser(){
        this.username="";
        this.password="";
        this.token="";
        this.client = new OkHttpClient();

    }
    public SecondClassUser(String username, String password) {
        this.username = username;
        this.password = password;
        this.token="";
        this.client = new OkHttpClient();
    }

    public static synchronized SecondClassUser getInstance(String username,String password) {
        Log.d("SecondClassUser", "SecondClassUser创建成功"+username+"/"+password);
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new SecondClassUser(username,password);
                }
            }
        }
        return instance;
    }
    public static synchronized SecondClassUser getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new SecondClassUser();
                }
            }
        }
        return instance;
    }

    public void login() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/token";
        Log.d("二课token", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"schoolCode\":\"10361\",\"code\":\""+this.username+"\",\"password\":\""+this.password+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课token", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课token", "登录成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        String access_token = data.get("access_token").getAsString();
                        System.out.println("二课token：" + access_token);
                        this.token=access_token;
                    }else{
                        //请求失败
                        Log.d("二课token", "登录失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("business_data 字段不存在");
                }
            } else {
                System.out.println("登录失败：" + response.message());
                this.token = "";
            }
        }
        getMyInfo();
    }

    public void getMyInfo() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/user/my-person-info";
        Log.d("个人信息", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("个人信息", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("个人信息", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        id = data.get("id").getAsString();
                        grade = data.get("grade").getAsInt();
                        collegeName = data.get("collegeName").getAsString();
                        if(collegeName.contains("医")){
                            years = new String[]{grade + "-" + (grade + 1), (grade + 1) + "-" + (grade + 2), (grade + 2) + "-" + (grade + 3), (grade + 3) + "-" + (grade + 4),(grade + 4) + "-" + (grade + 5)};
                        }else{
                            years = new String[]{grade + "-" + (grade + 1), (grade + 1) + "-" + (grade + 2), (grade + 2) + "-" + (grade + 3), (grade + 3) + "-" + (grade + 4)};
                        }
                    }else{
                        //请求失败
                        Log.d("个人信息", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("个人信息字段不存在");
                }
            } else {
                System.out.println("个人信息：" + response.message());
            }
        }
    }

    public JsonObject getMyInfoReturn() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/user/my-person-info";
        Log.d("个人信息", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("个人信息", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("个人信息", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("个人信息", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("个人信息字段不存在");
                }
            } else {
                System.out.println("个人信息：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getUserInfo(String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/message/chat/follow/user/info";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"id\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getUserInfoActivity(Integer pageNum,String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/other-list";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"userId\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonArray getMyGradeClassify() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/achievement/by-classify-list";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonArray data = json.get("data").getAsJsonArray();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonArray getMyGradeTerm() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/achievement/by-term-list";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonArray data = json.get("data").getAsJsonArray();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getRankingList(Integer pageNum,Integer level) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/achievement/rank";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"level\":"+level+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getRankingSelf(Integer level) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/achievement/self/rank";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"level\":"+level+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getGradeList() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/achievement/detail-app";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getGradeListDetail(Integer pageNum) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/user/transcript";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"scoreStatus\":0}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public Boolean followUserInfo(String id,Boolean isFollowed) throws Exception {
        String url="";
        if (isFollowed){
            //被关注了,取消关注
            url = "https://win.9xueqi.com/api/app/client/v1/message/chat/follow/delete";
        }else{
            //未关注,进行关注
            url = "https://win.9xueqi.com/api/app/client/v1/message/chat/follow/add";
        }
        Log.d("二课关注", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"id\":\""+id+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课关注", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课关注", "成功");
                        //请求成功
                        return true;
                    }else{
                        //请求失败
                        Log.d("二课关注", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课关注的评论字段不存在");
                }
            } else {
                System.out.println("二课关注的评论：" + response.message());
            }
        }
        return false;
    }

    public JsonArray getSchoolComment(Integer pageNum) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/list";
        Log.d("二课评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":20}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        JsonArray comment_list = data.get("list").getAsJsonArray();
                        System.out.println("二课评论：" + comment_list);
                        return comment_list;
                    }else{
                        //请求失败
                        Log.d("二课评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论字段不存在");
                }
            } else {
                System.out.println("二课评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getSchoolCommentContent(String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/detail";
        Log.d("二课评论内容", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"id\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论内容", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论内容", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论内容", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论内容字段不存在");
                }
            } else {
                System.out.println("二课评论内容：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getSchoolCommentComment(Integer pageNum,String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/comment/list";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"dynamicId\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getSchoolCommentLike(Integer pageNum,String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/like/members";
        Log.d("二课评论的评论", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"dynamicId\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论的评论", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论的评论", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课评论的评论", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论的评论字段不存在");
                }
            } else {
                System.out.println("二课评论的评论：" + response.message());
            }
        }
        return null;
    }

    public String schoolLikeContent(String id,String islike) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/like";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"isLike\":"+islike+",\"dynamicId\":\""+id+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论点赞", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论点赞", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        String isLike = data.get("isLike").getAsString();
                        return isLike;
                    }else{
                        //请求失败
                        Log.d("二课评论点赞", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论点赞字段不存在");
                }
            } else {
                System.out.println("二课评论点赞：" + response.message());
            }
        }
        return null;
    }

    public Boolean postAddCommentComment(String content,String id,String parentId,String replyCommentUserId) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/comment/add";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"dynamicId\":"+id+",\"comments\":\""+content+"\",\"parentId\":\""+parentId+"\",\"replyCommentUserId\":\""+replyCommentUserId+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("评论的评论发布", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("评论的评论发布", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("评论的评论发布", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("评论的评论发布字段不存在");
                }
            } else {
                System.out.println("评论的评论发布：" + response.message());
            }
        }
        return false;
    }

    public Boolean postAddActivityComment(String content,String activityId,String parentId,String starScore,String appraiseTag) throws Exception {
        //活动评论
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/comment/add";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"comments\":\""+content+"\",\"activityId\":"+activityId+",\"parentId\":\""+parentId+"\",\"starScore\":"+starScore+",\"appraiseTag\":\""+appraiseTag+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("评论的评论发布", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("评论的评论发布", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("评论的评论发布", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("评论的评论发布字段不存在");
                }
            } else {
                System.out.println("评论的评论发布：" + response.message());
            }
        }
        return false;
    }

    public Boolean postCreateComment(String content,String topicId) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/create";
        Log.d("二课评论创建", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"content\":\""+content+"\",\"topicId\":"+topicId+",\"attachUrl\":\"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课评论创建", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课评论创建", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("二课评论创建", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课评论创建发布字段不存在");
                }
            } else {
                System.out.println("二课评论创建：" + response.message());
            }
        }
        return false;
    }

    public Boolean postDeleteCommentComment(String contentId) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/comment/delete";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"id\":"+contentId+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("评论的评论发布", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("评论的评论发布", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("评论的评论发布", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("评论的评论发布字段不存在");
                }
            } else {
                System.out.println("评论的评论发布：" + response.message());
            }
        }
        return false;
    }

    public Boolean postDeleteActivityComment(String contentId) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/comment/delete";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"id\":"+contentId+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("评论的评论发布", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("评论的评论发布", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("评论的评论发布", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("评论的评论发布字段不存在");
                }
            } else {
                System.out.println("评论的评论发布：" + response.message());
            }
        }
        return false;
    }

    public Boolean postDeleteComment(String contentId) throws Exception {
        //删除主页帖子
        String url = "https://win.9xueqi.com/api/app/client/v1/dynamic/delete";
        Log.d("二课评论点赞", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");

        String params = AesEcbUtil.Encrypt("{\"id\":"+contentId+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // Prepare headers
        RequestBody requestBody = new FormBody.Builder()
                .add("params", params)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader)
                .post(requestBody);

        // Build the request
        Request request = requestBuilder.build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("评论的评论发布", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("评论的评论发布", "成功");
                        return true;
                    }else{
                        //请求失败
                        Log.d("评论的评论发布", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("评论的评论发布字段不存在");
                }
            } else {
                System.out.println("评论的评论发布：" + response.message());
            }
        }
        return false;
    }

    public JsonObject getHomeContent() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/user/modules";
        Log.d("二课首页", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课首页", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课首页", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        System.out.println("二课首页：" + data);
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课首页", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课首页字段不存在");
                }
            } else {
                System.out.println("二课首页：" + response.message());
            }
        }
        return null;
    }

    public JsonArray getTodoMessage() throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/message/notice/list";
        Log.d("二课首页", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+"1"+",\"pageSize\":40}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课首页", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课首页", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        JsonArray result = data.get("list").getAsJsonArray();
                        System.out.println("二课首页：" + result);
                        return result;
                    }else{
                        //请求失败
                        Log.d("二课首页", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课首页字段不存在");
                }
            } else {
                System.out.println("二课首页：" + response.message());
            }
        }
        return null;
    }

    public JsonArray getPersonalRewards(String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/student/personal-rewards-penalties";
        Log.d("二课消息", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"id\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课消息", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课消息", "成功");
                        //请求成功
                        JsonArray data = json.get("data").getAsJsonArray();
                        System.out.println("二课消息：" + data);
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课消息", "失败");

                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课消息字段不存在");
                }
            } else {
                System.out.println("二课消息：" + response.message());
            }
        }
        return null;
    }

    public JsonArray getActivity(String ctime) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/page/activity/list";
        Log.d("二课活动", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"search\":\"\",\"pageNum\":1,\"pageSize\":50,\"sortType\":1,\"activityTime\":"+DateUtils.convertDateToTimestamp(ctime)+",\"classifyId\":0,\"status\":\"4,5,6,7\",\"level\":0,\"organizationName\":\"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课活动", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课活动", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        JsonArray result = data.get("list").getAsJsonArray();
                        System.out.println("二课活动：" + result);
                        return result;
                    }else{
                        //请求失败
                        Log.d("二课活动", "失败");
                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课活动字段不存在");
                }
            } else {
                System.out.println("二课活动：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getActivityContent(String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/detail/participant";
        Log.d("二课活动", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"id\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课活动", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课活动", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        System.out.println("二课活动：" + data);
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课活动", "失败");
                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课活动字段不存在");
                }
            } else {
                System.out.println("二课活动：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getActivityContentComment(Integer pageNum,String id,String tag) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/comment/list";
        Log.d("二课活动", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"pageNum\":"+pageNum+",\"pageSize\":10,\"appraiseTag\":\""+tag+"\",\"activityId\":\""+id+"\"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课活动", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课活动", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        System.out.println("二课活动：" + data);
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课活动", "失败");
                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课活动字段不存在");
                }
            } else {
                System.out.println("二课活动：" + response.message());
            }
        }
        return null;
    }

    public JsonObject getActivityContentCommentTag(String id) throws Exception {
        String url = "https://win.9xueqi.com/api/app/client/v1/activity/comment/tag/list";
        Log.d("二课活动", "函数开始执行");
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTimeMillis);
        String authorization = AesEcbUtil.Encrypt("{\"token\":\""+this.token+"\",\"platform\":3,\"version\":\"2.0.5\",\"device\":\"FAAE6E21-013F-42D1-B722-599DE3DC340F\",\"timestamp\":\"" +currentTimeString+"\"}");
        String authHeader = authorization.trim().replaceAll("[\\t\\n\\r]+", "");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader);

        String params = AesEcbUtil.Encrypt("{\"activityId\":"+id+"}");
        params=params.replace("+", "%2B");
        params = params.trim().replaceAll("[\\t\\n\\r]+", "");

        // 如果有参数，拼接参数到URL中
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("params", params);
        String finalUrl = urlBuilder.build().toString();

        // Build the request (GET请求无需设置RequestBody)
        Request request = requestBuilder.url(finalUrl).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                responseData=AesEcbUtil.Decrypt(responseData);
                Log.d("二课活动", "请求获取成功"+responseData);
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("code")) {
                    Integer code = json.get("code").getAsInt();
                    if(code.equals(0)){
                        Log.d("二课活动", "成功");
                        //请求成功
                        JsonObject data = json.get("data").getAsJsonObject();
                        System.out.println("二课活动：" + data);
                        return data;
                    }else{
                        //请求失败
                        Log.d("二课活动", "失败");
                    }

                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("二课活动字段不存在");
                }
            } else {
                System.out.println("二课活动：" + response.message());
            }
        }
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String[] getYears() {
        return years;
    }

    public void setYears(String[] years) {
        this.years = years;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
