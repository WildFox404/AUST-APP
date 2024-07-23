package com.example.newapp;

import com.google.gson.*;
import android.text.TextUtils;
import android.util.Log;
import okhttp3.*;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
//                String log_result=json.toString();
////                int maxLogSize = 1000;
////                for(int i = 0; i <= log_result.length() / maxLogSize; i++) {
////                    int start = i * maxLogSize;
////                    int end = (i+1) * maxLogSize;
////                    end = end > log_result.length() ? log_result.length() : end;
////                    Log.d("JSON Part " + (i+1), log_result.substring(start, end));
////                }
                // 检查 "business_data" 字段是否存在
                String err_code = json.get("err_code").getAsString();
                Log.d("get_class", "err_code: "+err_code);
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
                        JsonArray classMessage=gson.fromJson(loginMessage,JsonArray.class);
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
            } else {
                System.out.println("考试成绩获取失败：" + response.message());
            }
        }
        return null;
    }
    public static String removeHtmlTags(String text) {
        return text.replaceAll("<.*?>", "");
    }

    public List<List<String>> getTd_class(String text) {
        String tableScriptPattern = "</table>\\s*<script language=\"JavaScript\">\\s*(.*?)\\s*</script>";
        String activityPattern = "activity = new TaskActivity\\((.*?)\\);";
        String indexPattern = "index =(.*?);";
        String coursePattern = "actTeacherId\\.join\\('(.*?)'\\),actTeacherName\\.join\\('(.*?)'\\),\"(.*?)\\((.*?)\\)\",\"(.*?)\\((.*?)\\)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",null,null,assistantName,\"\",\"\"";
        String positionPattern = "(\\d+)\\*(.*?)\\+(\\d+)";

        Pattern tableScript = Pattern.compile(tableScriptPattern, Pattern.DOTALL);
        Matcher tableScriptMatcher = tableScript.matcher(text);

        List<List<String>> resultList = new ArrayList<>();

        if (tableScriptMatcher.find()) {
            String result = tableScriptMatcher.group(1);

            Pattern activity = Pattern.compile(activityPattern, Pattern.DOTALL);
            Matcher activityMatcher = activity.matcher(result);

            while (activityMatcher.find()) {
                String content = activityMatcher.group(1);

                Pattern course = Pattern.compile(coursePattern);
                Matcher courseMatcher = course.matcher(content);

                if (courseMatcher.find()) {
                    List<String> courseData = new ArrayList<>();
                    courseData.add(courseMatcher.group(5));
                    courseData.add(courseMatcher.group(8));

                    int endIndex = result.indexOf("var teachers", activityMatcher.end());
                    String activityText;
                    if (endIndex != -1) {
                        activityText = result.substring(activityMatcher.end(), endIndex);
                    } else {
                        // 处理找不到 "var teachers" 的情况，例如抛出异常或者给出默认值
                        activityText = result.substring(activityMatcher.end());;
                    }
                    Pattern index = Pattern.compile(indexPattern);
                    Matcher indexMatcher = index.matcher(activityText);

                    List<String> positions = new ArrayList<>();
                    while (indexMatcher.find()) {
                        Pattern position = Pattern.compile(positionPattern);
                        Matcher positionMatcher = position.matcher(indexMatcher.group(1));
                        while (positionMatcher.find()) {
                            positions.add(positionMatcher.group(1));
                            positions.add(positionMatcher.group(3));
                        }
                    }
                    courseData.addAll(positions);
                    resultList.add(courseData);
                }
            }
        }
        return resultList;
    }
    public static List<Map<String, String>> getTd_test(String text) {
        List<Map<String, String>> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("<tbody(.*?)>(.*?)</tbody>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String content = matcher.group(2);
            Pattern trPattern = Pattern.compile("<tr>(.*?)</tr>", Pattern.DOTALL);
            Matcher trMatcher = trPattern.matcher(content);
            List<List<String>> tdList = new ArrayList<>();
            while (trMatcher.find()) {
                String tdContent = trMatcher.group(1).trim();
                Pattern tdPattern = Pattern.compile("<td\\s*.*?>(.*?)</td>", Pattern.DOTALL);
                Matcher tdMatcher = tdPattern.matcher(tdContent);
                List<String> tdContentArray = new ArrayList<>();
                while (tdMatcher.find()) {
                    tdContentArray.add(removeHtmlTags(tdMatcher.group(1).trim()));
                }
                tdList.add(tdContentArray);
            }
            String[] headers = {"课程序号", "课程名称", "考试类别", "考试日期", "考试安排", "考试地点", "考场座位", "考试情况", "其他说明"};
            for (List<String> row : tdList) {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowMap.put(headers[i], row.get(i));
                }
                result.add(rowMap);
            }
        }
        return result;
    }


    public String cleanCourseName(String courseName) {
        String cleanName = courseName.replaceAll("<.*?>", "");  // Remove any HTML tags
        cleanName = cleanName.replace("\t", "");  // Remove tab characters
        cleanName = cleanName.replace("\n", "");  // Remove newline characters
        cleanName = cleanName.replace(" ", "");  // Remove any remaining spaces
        return cleanName.trim();  // Return the cleaned course name without leading or trailing spaces
    }
    public List<Map<String, String>> getTd(String text) {
        List<Map<String, String>> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("<tbody(.*?)>(.*?)</tbody>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String content = matcher.group(2);
            Pattern trPattern = Pattern.compile("<tr>(.*?)</tr>", Pattern.DOTALL);
            Matcher trMatcher = trPattern.matcher(content);
            while (trMatcher.find()) {
                String tr = trMatcher.group(1).trim();
                Pattern tdPattern = Pattern.compile("<td\\s*.*?>(.*?)</td>", Pattern.DOTALL);
                Matcher tdMatcher = tdPattern.matcher(tr);
                List<String> contentArray = new ArrayList<>();
                while (tdMatcher.find()) {
                    String tdContent = tdMatcher.group(1).trim();
                    contentArray.add(tdContent);
                }

                String[] headers = {"学年学期", "课程代码", "课程序号", "课程名称", "课程类别", "学分", "总评成绩", "最终", "绩点"};
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < headers.length && i < contentArray.size(); i++) {
                    if (headers[i].equals("课程名称")) {
                        rowMap.put(headers[i], cleanCourseName(contentArray.get(i)));  // Clean the course name
                    } else {
                        rowMap.put(headers[i], contentArray.get(i));
                    }
                }
                result.add(rowMap);
            }
        }
        return result;
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