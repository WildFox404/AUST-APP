package com.example.newapp.entries;

import android.content.Context;
import android.content.SharedPreferences;
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

public class User {
    private String ids;
    private String token;
    private String username;
    private String password;
    private static User instance;
    private OkHttpClient client;
    private String[] years;
    private int coverage_year;
    private Gson gson = new Gson();

    public static final String PREFS_NAME = "MyPrefs";

    public void updateUser(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.setUsername(prefs.getString("username", ""));
        this.setPassword(prefs.getString("password", ""));
    }


//    // Create a custom CookieJar
//    CookieJar cookieJar = new CookieJar() {
//        private final Map<String, List<Cookie>> cookieStore = new HashMap<>();
//
//        @Override
//        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//            cookieStore.put(url.host(), cookies);
//        }
//
//        @Override
//        public List<Cookie> loadForRequest(HttpUrl url) {
//            List<Cookie> cookies = cookieStore.get(url.host());
//            return cookies != null ? cookies : new ArrayList<>();
//        }
//    };
    public User(){
        this.username="";
        this.password="";
        this.token="";
        this.client = new OkHttpClient();
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token="";
        this.client = new OkHttpClient();
    }

    public static synchronized User getInstance(String username,String password) {
        Log.d("User", "User创建成功"+username+"/"+password);
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User(username,password);
                }
            }
        }
        return instance;
    }
    public static synchronized User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

    // 加密字符串为 Base64
    private String encodeToBase64(String content, String charsetName) {
        Log.d("Base64", "传入加密数据: "+content);
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        try {
            byte[] contentByte = content.getBytes(charsetName);
            Log.d("Base64", "加密成功: "+Base64.encodeToString(contentByte, Base64.DEFAULT));
            return Base64.encodeToString(contentByte, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 解码 Base64 字符串为原始字符串
    private String decodeFromBase64(String content, String charsetName) {
        Log.d("Base64", "传入解密数据: "+content);
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        byte[] contentByte = Base64.decode(content, Base64.DEFAULT);
        try {
            String result= new String(contentByte, charsetName);
            Log.d("Base64", "解密成功: "+result);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public JsonObject decodeFromBase64ToJsonObj(String content, String charsetName) {
        Log.d("Base64", "传入解密数据: " + content);
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        byte[] contentByte = Base64.decode(content, Base64.DEFAULT);
        try {
            String jsonString = new String(contentByte, charsetName);
            Log.d("Base64", "解密成功: " + jsonString);

            // 使用 Gson 将解密后的字符串转换为 JsonArray
            JsonObject jsonArray = gson.fromJson(jsonString, JsonObject.class);
            return jsonArray;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            Log.e("Base64", "解密后的字符串不是有效的 JSON: " + e.getMessage());
        }
        return null; // 返回 null 以指示解析失败
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
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

    public String SHA1(String data) {
        String encryptedData = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            encryptedData = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        Log.d("UserCreation", "SHA1加密完成");
        return encryptedData;
    }

    public void login() throws IOException {
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/login";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded");

        // Prepare the request body
        String encodedPassword = encodeToBase64(this.password,"");
        String requestBody = "passwd=" + encodedPassword + "&user_code=" + this.username;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("business_data")) {
                    String businessData = json.get("business_data").getAsString();

                    // 检查值是否为对象
                    if (!businessData.isEmpty()) {

                        // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                        JsonObject loginMessage = decodeFromBase64ToJsonObj(businessData, "");
                        System.out.println("登录消息：" + loginMessage.toString());
                        saveUserMessage(loginMessage);
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
                System.out.println("登录失败：" + response.message());
                this.token = "";
            }
        }
    }

    private void saveUserMessage(JsonObject loginMessage) {
        if (username.length() >= 4) {
            String firstFourChars = username.substring(0, 4);
            if (firstFourChars.matches("\\d+")) {
                coverage_year=Integer.parseInt(firstFourChars);
                int year = Integer.parseInt(firstFourChars);
                years = new String[]{year + "-" + (year + 1), (year + 1) + "-" + (year + 2), (year + 2) + "-" + (year + 3), (year + 3) + "-" + (year + 4)};
                Log.d("入学年份", "入学年份:" + firstFourChars);
            }
        }
        if (loginMessage != null) {
            // 检查 token 是否存在
            if (loginMessage.has("token") && !loginMessage.get("token").isJsonNull()) {
                String token = loginMessage.get("token").getAsString();
                // 处理 token
                Log.d("saveToken", "token获取成功: " + token);
                this.token = token; // 保存 token
            } else {
                Log.e("saveToken", "token 不存在或为 null");
            }
        }
    }

    public JsonObject get_grade() throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/student/exam/grade/get-grades";
        Log.d("get_grade", "token: "+this.token);
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&kind=all&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                Log.d("get_grade", "get_grade: "+responseData);

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json != null && json.has("business_data")) {
                    String businessData = json.get("business_data").getAsString();

                    // 检查值是否为对象
                    if (!businessData.isEmpty()) {

                        // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                        JsonObject loginMessage = decodeFromBase64ToJsonObj(businessData, "");
                        System.out.println("登录消息：" + loginMessage.toString());
                        return loginMessage;
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
        return null;
    }
    public JsonArray get_test(String semesterId) throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/student/exam/schedule/lesson/get-exam-tasks";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&semester_id="+semesterId+"&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("business_data")) {
                    String businessData = json.get("business_data").getAsString();

                    // 检查值是否为对象
                    if (!businessData.isEmpty()) {
                        JsonArray jsonArray = null;
                        // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                        String loginMessage = decodeFromBase64(businessData, "");
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(loginMessage);

                        if (element.isJsonArray()) {
                            jsonArray = element.getAsJsonArray();
                        }else{
                            Log.d("test_json_process", "考试安排数据不符合json格式");
                        }
                        System.out.println("考试安排消息："+jsonArray.toString());
                        return jsonArray;
                    } else {
                        // 处理 business_data 字段不是对象的情况
                        System.out.println("business_data 字段不是对象");
                    }
                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("business_data 字段不存在");
                }
            } else {
                System.out.println("考试安排获取失败：" + response.message());
            }
        }
        return null;
    }

    public JsonArray get_class(String semesterId,String start_date,String end_date) throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/student/course/schedule/get-course-tables";
        Log.d("get_class", "token: "+this.token);
        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&end_date="+end_date+"&start_date="+start_date+"&semester_id="+semesterId+"&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();
                Log.d("get_class", "get_class内容: "+responseData);
                Log.d("get_class", "get_class长度: "+responseData.length());
                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);
                if(json!=null) {
                    // 检查 "business_data" 字段是否存在
                    String err_code = json.get("err_code").getAsString();
                    Log.d("get_class", "err_code: " + err_code);
                    if (err_code.equals("00000")) {
                        String businessData = json.get("business_data").getAsString();
                        Log.d("class businessData", String.valueOf(businessData.length()));
                        // 检查值是否为对象
                        if (!businessData.isEmpty()) {
                            // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                            String loginMessage = decodeFromBase64(businessData, "");
                            //                        int maxLogSize = 1000;
                            //                        for(int i = 0; i <= loginMessage.length() / maxLogSize; i++) {
                            //                            int start = i * maxLogSize;
                            //                            int end = (i+1) * maxLogSize;
                            //                            end = end > loginMessage.length() ? loginMessage.length() : end;
                            //                            Log.d("JSON Part " + (i+1), loginMessage.substring(start, end));
                            //                        }
                            JsonArray classMessage = gson.fromJson(loginMessage, JsonArray.class);
                            System.out.println("登录消息：" + classMessage.toString());
                            return classMessage;
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
                }
            } else {
                System.out.println("考试成绩获取失败：" + response.message());
            }
        }
        return null;
    }

    public JsonArray get_empty_classrooms() throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/room/borrow/campus/building/search";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

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

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                if(json!=null) {
                    // 检查 "business_data" 字段是否存在
                    if (json.has("business_data")) {
                        String businessData = json.get("business_data").getAsString();

                        // 检查值是否为对象
                        if (!businessData.isEmpty()) {
                            JsonArray jsonArray = null;
                            // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                            String loginMessage = decodeFromBase64(businessData, "");
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(loginMessage);

                            if (element.isJsonArray()) {
                                jsonArray = element.getAsJsonArray();
                            } else {
                                Log.d("test_json_process", "考试安排数据不符合json格式");
                            }
                            System.out.println("考试安排消息：" + jsonArray.toString());
                            return jsonArray;
                        } else {
                            // 处理 business_data 字段不是对象的情况
                            System.out.println("business_data 字段不是对象");
                        }
                    } else {
                        // 处理没有 business_data 字段的情况
                        System.out.println("business_data 字段不存在");
                    }
                }
            } else {
                System.out.println("考试安排获取失败：" + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    public JsonArray get_empty_classrooms_byId(String building_id,String start_date) throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        Log.d("get_empty_classrooms_byId", building_id+"/"+start_date);
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/room/borrow/occupancy/search";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "building_id="+building_id+"&start_date="+start_date+"&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                // 检查 "business_data" 字段是否存在
                if (json.has("business_data")) {
                    String businessData = json.get("business_data").getAsString();

                    // 检查值是否为对象
                    if (!businessData.isEmpty()) {
                        JsonArray jsonArray = null;
                        // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                        String loginMessage = decodeFromBase64(businessData, "");
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(loginMessage);

                        if (element.isJsonArray()) {
                            jsonArray = element.getAsJsonArray();
                        }else{
                            Log.d("test_json_process", "考试安排数据不符合json格式");
                        }
                        System.out.println("考试安排消息："+jsonArray.toString());
                        return jsonArray;
                    } else {
                        // 处理 business_data 字段不是对象的情况
                        System.out.println("business_data 字段不是对象");
                    }
                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("business_data 字段不存在");
                }
            } else {
                System.out.println("考试安排获取失败：" + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public JsonObject update_semester() throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/common/get-semester";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);
                if(json == null){
                    Log.d("update_semester", "请求结果为null");
                    return null;
                }
                // 检查 "business_data" 字段是否存在
                if (json.has("business_data")) {
                    String businessData = json.get("business_data").getAsString();

                    // 检查值是否为对象
                    if (!businessData.isEmpty()) {
                        JsonObject jsonobject = null;
                        // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                        String loginMessage = decodeFromBase64(businessData, "");
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(loginMessage);

                        if (element.isJsonObject()) {
                            jsonobject = element.getAsJsonObject();
                        }else{
                            Log.d("update_semester", "考试安排数据不符合json格式");
                            return null;
                        }
                        System.out.println("考试安排消息："+jsonobject.toString());
                        return jsonobject;
                    } else {
                        // 处理 business_data 字段不是对象的情况
                        System.out.println("business_data 字段不是对象");
                    }
                } else {
                    // 处理没有 business_data 字段的情况
                    System.out.println("business_data 字段不存在");
                }
            } else {
                System.out.println("考试安排获取失败：" + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public JsonObject get_plan_completion() throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/student/course/plan/completion/my-plan-completion";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                if(json!=null){
                    // 检查 "business_data" 字段是否存在
                    if (json.has("business_data")) {
                        String businessData = json.get("business_data").getAsString();

                        // 检查值是否为对象
                        if (!businessData.isEmpty()) {
                            JsonObject jsonObject = null;
                            // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                            String loginMessage = decodeFromBase64(businessData, "");
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(loginMessage);

                            if (element.isJsonObject()) {
                                jsonObject = element.getAsJsonObject();
                            }else{
                                Log.d("get_plan_completion", "不符合json格式");
                            }
                            return jsonObject;
                        } else {
                            // 处理 business_data 字段不是对象的情况
                            System.out.println("business_data 字段不是对象");
                        }
                    } else {
                        // 处理没有 business_data 字段的情况
                        System.out.println("business_data 字段不存在");
                    }
                }
            } else {
                System.out.println("get_plan_completion失败：" + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public JsonObject get_incubation_programs() throws IOException {
        if (this.token.isEmpty()){
            login();
        }
        String url = "https://jwglyd.aust.edu.cn/app-ws/ws/app-service/student/course/plan/my-plan";

        // Prepare headers
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", this.token);

        // Prepare the request body
        String requestBody = "biz_type_id=1&token=" + this.token;
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/x-www-form-urlencoded"));

        // Build the request
        Request request = requestBuilder.post(body).build();
        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应数据转换为字符串
                String responseData = response.body().string();

                // 将字符串转换为 JSON 对象
                JsonObject json = gson.fromJson(responseData, JsonObject.class);

                if(json!=null){
                    // 检查 "business_data" 字段是否存在
                    if (json.has("business_data")) {
                        String businessData = json.get("business_data").getAsString();

                        // 检查值是否为对象
                        if (!businessData.isEmpty()) {
                            JsonObject jsonObject = null;
                            // 假设响应包含 base64 编码的数据，将其解码为 JSON 对象
                            String loginMessage = decodeFromBase64(businessData, "");
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(loginMessage);

                            if (element.isJsonObject()) {
                                jsonObject = element.getAsJsonObject();
                            }else{
                                Log.d("get_plan_completion", "不符合json格式");
                            }
                            return jsonObject;
                        } else {
                            // 处理 business_data 字段不是对象的情况
                            System.out.println("business_data 字段不是对象");
                        }
                    } else {
                        // 处理没有 business_data 字段的情况
                        System.out.println("business_data 字段不存在");
                    }
                }
            } else {
                System.out.println("get_plan_completion失败：" + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCoverage_year() {
        return coverage_year;
    }

    public void setCoverage_year(int coverage_year) {
        this.coverage_year = coverage_year;
    }

    public String[] getYears() {
        return years;
    }

    public void setYears(String[] years) {
        this.years = years;
    }
}