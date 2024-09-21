package com.example.newapp.entries;

import android.util.Log;
import com.example.newapp.utils.AesEcbUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class ChargeApi {
    private String skey="9ad295c6-008d-4e62-b21a-24234be3bd78";
    private Gson gson = new Gson();
    private OkHttpClient client=new OkHttpClient();
    public JsonArray getChargeStation(String stationid) throws IOException {
        Request request = new Request.Builder()
                .url("https://www.ahttcd.cn/towerchargewebapp/finedo/app/queryPile?stationid="+stationid+"&workingstatus=00&skey="+skey)
                .header("authority", "www.ahttcd.cn")
                .header("accept", "*/*")
                .header("accept-language", "zh-CN,zh;q=0.9")
                .header("content-type", "application/json")
                .header("referer", "https://servicewechat.com/wx70be2ceb5ab38169/169/page-frame.html")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "cross-site")
                .header("xweb_xhr", "1")
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body().string(); // 保存响应体内容
            Log.d("充电桩", "内容: " + responseData);
            if (response.isSuccessful()) {
                // 假设 responseString 是从 response 中获取到的 JSON 字符串
                JsonArray jsonArray = new JsonParser().parse(responseData).getAsJsonArray();
                return jsonArray;
            } else {
                System.out.println("getChargeStation获取失败：" + response.message());
            }
        }catch (Exception e){
            Log.e("充电桩", "出错了: "+e.getMessage());
        }
        return null;
    }
}
