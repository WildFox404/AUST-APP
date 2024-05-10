package com.example.newapp;

import android.util.Log;
import android.widget.EditText;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class User {
    private String ids;
    private String username;
    private String password;
    private OkHttpClient client;


    // Create a custom CookieJar
    CookieJar cookieJar = new CookieJar() {
        private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<>();
        }
    };

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        Log.d("UserCreation", "对象创建成功");
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

    public void loginIn() throws IOException {
        String Url = "http://jwgl.aust.edu.cn/eams/login.action";
        Request request = new Request.Builder()
                .url(Url)
                .build();
        Log.d("UserCreation", request.toString());

        Response response = client.newCall(request).execute();
        Log.d("response", response.toString());
        String content = null;
        System.out.println(response);
        Pattern pattern = Pattern.compile("CryptoJS\\.SHA1\\('([a-f0-9-]+)' \\+ form\\['password'\\]\\.value\\);");
        Matcher matcher = pattern.matcher(response.body().string());
        if (matcher.find()) {
            content = matcher.group(1);
            Log.d("UserCreation", content.toString());
        }
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String password = content + this.password;
        String encryptedPassword = SHA1(password);
        Log.d("UserCreation", encryptedPassword.toString());
        RequestBody requestBody = new FormBody.Builder()
                .add("username", this.username)
                .add("password", encryptedPassword)
                .add("encodedPassword", "")
                .add("session_locale", "zh_CN")
                .build();

        Request loginRequest = new Request.Builder()
                .url(Url)
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Mobile Safari/537.36 Edg/115.0.1901.188")
                .post(requestBody)
                .build();

        try (Response loginResponse = client.newCall(loginRequest).execute()) {
            Log.d("Cookie", loginResponse.headers("Cookie").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get_grade(String semesterId) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://jwgl.aust.edu.cn/eams/teach/grade/course/person!search.action")
                .newBuilder()
                .addQueryParameter("semesterId", semesterId);

        String second_url = urlBuilder.build().toString();

        Request second_request = new Request.Builder()
                .url(second_url)
                .build();
//        List<Cookie> currentCookies = client.cookieJar().loadForRequest(HttpUrl.parse("http://jwgl.aust.edu.cn/eams/login.action"));
//            for (Cookie cookie : currentCookies) {
//                Log.d("UserCreation", "Cookie: " + cookie.name() + "=" + cookie.value());
//            }

        Log.d("UserCreation", "开始请求");
        try (Response result = client.newCall(second_request).execute()) {
            return result.body().string();
        }
    }
    public String get_test() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://jwgl.aust.edu.cn/eams/stdExamTable!examTable.action")
                .newBuilder()
                .addQueryParameter("examBatch.id", "1062");

        String second_url = urlBuilder.build().toString();

        Request second_request = new Request.Builder()
                .url(second_url)
                .build();
//        List<Cookie> currentCookies = client.cookieJar().loadForRequest(HttpUrl.parse("http://jwgl.aust.edu.cn/eams/login.action"));
//            for (Cookie cookie : currentCookies) {
//                Log.d("UserCreation", "Cookie: " + cookie.name() + "=" + cookie.value());
//            }

        Log.d("UserCreation", "开始请求");
        try (Response result = client.newCall(second_request).execute()) {
            return result.body().string();
        }
    }
    public void get_ids() throws IOException{
        String ids_result;
        String pattern = "bg\\.form\\.addInput\\(form,\"[^\"]+\",\"([^\"]+)\"\\);";
        Pattern r = Pattern.compile(pattern);
        long timestamp = System.currentTimeMillis();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://jwgl.aust.edu.cn/eams/courseTableForStd.action").newBuilder();
        urlBuilder.addQueryParameter("_", String.valueOf(timestamp));
        String ids_url = urlBuilder.build().toString();

        Request ids_request = new Request.Builder()
                .url(ids_url)
                .build();
        Response ids_response = client.newCall(ids_request).execute();
        ids_result=ids_response.body().string();
        Matcher m = r.matcher(ids_result);
        Log.d("UserCreation", ids_result);
        if (m.find()) {
            String input_value = m.group(1);
            ids=input_value;
        } else {
            ids="";
        }
        Log.d("UserCreation", ids);
    }
    public String get_class(String semesterId,String startweek,String ids) throws IOException {
        String url = "http://jwgl.aust.edu.cn/eams/courseTableForStd!courseTable.action";
        FormBody requestBody = new FormBody.Builder()
                .add("semester.id", semesterId)
                .add("setting.kind", "std")
                .add("project.id", "1")
                .add("ids", ids)
                .add("startWeek", startweek)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.d("UserCreation", "开始请求");
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("UserCreation", responseBody);
            return responseBody;
        }
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
}