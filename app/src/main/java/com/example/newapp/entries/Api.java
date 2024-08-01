package com.example.newapp.entries;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class Api {
    private Gson gson = new Gson();
    private OkHttpClient client=new OkHttpClient();
    public String get_sentence() throws IOException {
        String url = "https://api.xygeng.cn/one";//每日一句
        Log.d("get_sentence", "get_sentence开始");
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded");

        // Prepare the request body
        String requestBody = "";
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                Log.d("get_sentence", "get_sentence: "+responseData);

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json != null && json.has("data")) {
                    JsonObject businessData = json.get("data").getAsJsonObject();

                    // 检查值是否为对象
                    if (businessData != null && businessData.has("content")) {

                        String content = businessData.get("content").getAsString();
                        String origin = businessData.get("origin").getAsString();
                        return content+"--"+origin;
                        // 处理 business_data 内容
                        // ...
                    } else {
                        // 处理 business_data 字段不是对象的情况
                        System.out.println("business_data 字段不是对象");
                    }
                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("business_data 字段不存在");
                }
            } else {
                System.out.println("考试成绩获取失败：" + response.message());
            }
        }
        return "";
    }
}
