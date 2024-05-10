package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {


    private SharedPreferences sharedPreferences;
    private SharedViewModel viewModel;

    int completedTasks=0;
    private TextView courseProcess;
    private LinearLayout courseWait;

    private MyDBHelper myDBHelper;
    private static final String KEY_YEAR = "year";
    private static final String KEY_TERM = "term";
    private static final String KEY_WEEK = "week";
    private String savedIds;
    private User user;
    List<List<String>> result;

    private List<Map<String, String>> grade_result;
    private String savedUsername;
    private String savedPassword;
    HashMap<Integer, List<Course>> course_result;
    int term=0;
    int year=0;
    int week=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    String savedWeek;
    private GridLayout gridLayout;
    private CountDownLatch latch;

    private void saveSelection(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0; // 默认返回第一个选项
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象

        View view = inflater.inflate(R.layout.home, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        gridLayout = view.findViewById(R.id.gridlayout);

        Spinner yearSpinner = view.findViewById(R.id.classYearSpinner);
        Spinner termSpinner = view.findViewById(R.id.classTermSpinner);
        Spinner weekSpinner = view.findViewById(R.id.classWeekSpinner);

        // 从SharedPreferences加载之前保存的内容
        savedUsername = sharedPreferences.getString("username", "");
        savedPassword = sharedPreferences.getString("password", "");
        savedIds =sharedPreferences.getString("ids","");

        // 从 SharedPreferences 中加载上次存储的数据
        savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        savedWeek = sharedPreferences.getString(KEY_WEEK, "0");
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));
        weekSpinner.setSelection(getIndex(weekSpinner, savedWeek));

        courseWait =view.findViewById(R.id.courseWait);
        courseProcess = view.findViewById(R.id.courseProcess);
        if(viewModel.getUserExist()){
            user=viewModel.getUser();
            user = new User(savedUsername,savedPassword);
        }else{
            user = new User(savedUsername,savedPassword);
            viewModel.setUser(user);
            viewModel.setUserExist(true);
        }

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_YEAR, selectedYear);
                    String firstFourChars = selectedYear.substring(0, 4); // 获取前四个字符
                    int number = Integer.parseInt(firstFourChars); // 转换成int类型
                    year = number;
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateCourseTable();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selectedTerm);

                    if (selectedTerm.equals("第一学期")) {
                        term = 1;
                    } else if (selectedTerm.equals("第二学期")) {
                        term = 2;
                    } else {
                        // 其他情况的处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateCourseTable();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedWeek = (String) parent.getItemAtPosition(position);
                String savedWeek = sharedPreferences.getString(KEY_WEEK, "0");

                if (!selectedWeek.equals(savedWeek)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_WEEK, selectedWeek);

                    Pattern pattern = Pattern.compile("第(\\d+)周");
                    Matcher matcher = pattern.matcher(selectedWeek);
                    if (matcher.find()) {
                        String weekNumber = matcher.group(1);
                        week = Integer.parseInt(weekNumber);
                    } else {
                        // 未找到匹配的内容处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateCourseTable();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myDBHelper.getCourseCountByUsername(user.getUsername().toString()) == 0) {
            Log.d("数据获取", "开始获取数据");
            int year = Integer.parseInt(user.getUsername().substring(0, 4));
            int findYear = 0;
            int findTerm = 0;
            int totalTasks = 4 * 2 * 21 + 8; // 总的异步任务数量
            // 创建 CountDownLatch，参数为异步任务的数量
//            latch = new CountDownLatch(totalTasks); // 假设有 2 * 2 * 21 个异步任务

            // 将视图设置为可见
            courseWait.setVisibility(View.VISIBLE);

            for (int y = year; y <= year + 4; y++) {
                for (int term = 1; term <= 2; term++) {
                    for (int week = 1; week <= 21; week++) {
                        findYear = (y - 2018) * 40 + 20;
                        if (term == 1) {
                            findTerm = 0;
                        } else if (term == 2) {
                            findTerm = 20;
                        } else {
                            Log.d("课表数据库存储", "出错了");
                        }
                        // 创建线程并启动
                        new Thread(new ClassThread(findTerm, findYear, week, term, y, week)).start();
                    }
                }
            }

            findYear=0;
            findTerm=0;
            year = Integer.parseInt(user.getUsername().substring(0, 4));
            for (int y = year; y <= year + 4; y++) {
                for (int term = 1; term <= 2; term++) {
                    findYear = (y - 2018) * 40 + 20;
                    if (term == 1) {
                        findTerm = 0;
                    } else if (term == 2) {
                        findTerm = 20;
                    } else {
                        Log.d("成绩数据库存储", "出错了");
                    }
                    // 创建线程并启动
                    new Thread(new HomeFragment.GradeThread(findTerm, findYear,term,y)).start();
                }
            }
            completedTasks=0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (completedTasks < totalTasks) {
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    courseProcess.setText(completedTasks + "/" + totalTasks);
                                }
                            });

                            // 暂停五秒钟
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 将视图设置为不可见
                            courseWait.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).start();
//            try {
//                // 等待所有异步操作完成
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        }
        String firstFourChars;
        if (savedYear.length() >= 5) {
            firstFourChars = savedYear.substring(0,4);
            // 继续你的代码
        } else {
            // 处理字符串长度不足的情况
            firstFourChars = "2024";
        }
        int number = Integer.parseInt(firstFourChars); // 转换成int类型
        year = number;
        if (savedTerm.equals("第一学期")) {
            term = 1;
        } else if (savedTerm.equals("第二学期")) {
            term = 2;
        } else {
            // 其他情况的处理
        }
        Pattern pattern = Pattern.compile("第(\\d+)周");
        Matcher matcher = pattern.matcher(savedWeek);
        if (matcher.find()) {
            String weekNumber = matcher.group(1);
            week = Integer.parseInt(weekNumber);
        }
        UpdateCourseTable();
    }
    private void UpdateCourseTable(){
        String yearString = String.valueOf(year);
        String termString = String.valueOf(term);
        String weekString = String.valueOf(week);
        Log.d("查询参数", "学年"+yearString+"学期"+termString+"周"+weekString);
        course_result = myDBHelper.getCourseData(savedUsername, yearString, termString, weekString);
        // 清空之前的内容
        gridLayout.removeAllViews();

        if (course_result != null && !course_result.isEmpty()) {
            for (Map.Entry<Integer, List<Course>> entry : course_result.entrySet()) {
                int section = entry.getKey();
                List<Course> courses = entry.getValue();

                for (Course course : courses) {
                    // 创建TextView来显示课程名称和地点
                    RoundedColorTextView roundedColorTextView = new RoundedColorTextView();
                    TextView textView = roundedColorTextView.createTextViewWithRoundedColorBackground(getContext(), "Your Text Here");
                    textView.setText(course.getCourseName() + "\n" + course.getCourseLocation());
                    textView.setTextSize(10);
                    textView.setGravity(Gravity.CENTER);
                    // 设置TextView在GridLayout中的位置
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.rowSpec = GridLayout.spec(section - 1, 1, 1f);  // 注意，GridLayout的行数从0开始，而节次从1开始，需要做适当的转换
                    params.columnSpec = GridLayout.spec(course.getWeek() - 1); // 同样，周数也需要适当的转换
                    params.setGravity(Gravity.FILL);
                    // 设置固定的宽度和高度
                    params.width = 146; // 设置为您希望的宽度，单位是像素或dp
                    params.height = 360; // 设置为您希望的高度，单位是像素或dp
                    textView.setLayoutParams(params);
                    // 将TextView添加到GridLayout中
                    gridLayout.addView(textView);
                }
            }
        } else {
            // 如果没有课程数据，可以添加相应的处理逻辑，例如显示一个提示信息
            TextView textView = new TextView(getContext());
            textView.setText("No courses available");
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.LTGRAY);
            gridLayout.addView(textView);
        }
    }
    // 定义线程任务
    class ClassThread implements Runnable {
        private int findTerm;
        private int findYear;
        private int findWeek;
        private int term;
        private int y;
        private int week;

        public ClassThread(int findTerm, int findYear, int findWeek, int term, int y, int week) {
            this.findTerm = findTerm;
            this.findYear = findYear;
            this.findWeek = week;
            this.term = term;
            this.y = y;
            this.week = week;
        }

        @Override
        public void run() {
            // 在此处执行需要在后台线程中进行的任务
            // 例如调用异步任务中的代码
            new ClassAsyncTask(findTerm, findYear, week, term, y, week).execute();
        }
    }
    private class ClassAsyncTask extends AsyncTask<Void, Void, List<List<String>>> {
        private Integer findTerm;
        private Integer findYear;
        private Integer findWeek;
        private Integer storeterm;
        private Integer storeyear;
        private Integer storeweek;
        public ClassAsyncTask(Integer findTerm,Integer findYear,Integer findweek,Integer term,Integer year,Integer week) {
            this.findTerm=findTerm;
            this.findYear=findYear;
            this.findWeek=findweek;
            this.storeterm=term;
            this.storeyear=year;
            this.storeweek=week;
        }
        @Override
        protected List<List<String>> doInBackground(Void... params) {
            try {
                user.loginIn();
                try {
                    Thread.sleep(500); // 500毫秒 = 0.5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result = user.getTd_class(user.get_class(String.valueOf(findTerm + findYear),String.valueOf(findWeek),String.valueOf(savedIds)));
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<List<String>> result) {
//            if (result != null && !result.isEmpty() && getView() != null) {
//                // 初始化容器
//                HashMap<Integer, List<Course>> courseContainer = new HashMap<>();
//                for (int i = 1; i <= 12; i++) {
//                    courseContainer.put(i, new ArrayList<>());
//                }
//
//                for (List<String> courseInfo : result) {
//                    String courseName = courseInfo.get(0);
//                    String courseLocation = courseInfo.get(1);
//
//                    for (int i = 2; i < courseInfo.size(); i += 2) {
//                        int week = Integer.parseInt(courseInfo.get(i)) + 1;
//                        int section = Integer.parseInt(courseInfo.get(i + 1)) / 2 + 1;
//                        Course course = new Course(courseName, courseLocation, week, section);
//                        courseContainer.get(section).add(course);
//                    }
//                }
//
//                // 将课程容器的数据存入数据库
//                HashMap<Integer, List<Course>> newCourseContainer = new HashMap<>();
//                for (int i = 1; i <= 12; i++) {
//                    List<Course> courses = courseContainer.get(i);
//                    if (!courses.isEmpty()) {
//                        newCourseContainer.put(i, courses);
//                    }
//                }
//                myDBHelper.insertCourseData(user.getUsername(), storeyear.toString(), storeterm.toString(), storeweek.toString(), newCourseContainer);
//                completedTasks++;
                // 打印新的课程容器中的内容
//                    for (int i = 1; i <= 6; i++) {
//                        List<Course> courses = newCourseContainer.get(i);
//                        if (courses != null) {
//                            Log.d("NewCourseContainer", "第" + i + "节课的课程有：");
//                            for (Course course : courses) {
//                                Log.d("NewCourseContainer", "课程名称：" + course.getCourseName());
//                                Log.d("NewCourseContainer", "上课地点：" + course.getCourseLocation());
//                                Log.d("NewCourseContainer", "上课周：" + course.getWeek());
//                                Log.d("NewCourseContainer", "节课数：" + course.getSection());
//                                Log.d("NewCourseContainer", "----------------------");
//                            }
//                        }
//                    }
            if (result != null && !result.isEmpty() && getView() != null) {
                // 初始化容器
                HashMap<Integer, List<Course>> courseContainer = new HashMap<>();
                for (int i = 1; i <= 12; i++) {
                    courseContainer.put(i, new ArrayList<>());
                }

                Set<String> uniqueCourseSet = new HashSet<>(); // 用于存储唯一课程标识

                for (List<String> courseInfo : result) {
                    String courseName = courseInfo.get(0);
                    String courseLocation = courseInfo.get(1);

                    for (int i = 2; i < courseInfo.size(); i += 2) {
                        int week = Integer.parseInt(courseInfo.get(i)) + 1;
                        int section = Integer.parseInt(courseInfo.get(i + 1)) / 2 + 1;
                        String courseKey = courseName + "_" + courseLocation + "_" + week + "_" + section; // 生成唯一标识

                        if (!uniqueCourseSet.contains(courseKey)) {
                            uniqueCourseSet.add(courseKey);

                            Course course = new Course(courseName, courseLocation, week, section);
                            courseContainer.get(section).add(course);
                        }
                    }
                }

                // 将课程容器的数据存入数据库
                HashMap<Integer, List<Course>> newCourseContainer = new HashMap<>();
                for (int i = 1; i <= 6; i++) {
                    List<Course> courses = courseContainer.get(i);
                    if (!courses.isEmpty()) {
                        newCourseContainer.put(i, courses);
                    }
                }
                myDBHelper.insertCourseData(user.getUsername(), storeyear.toString(), storeterm.toString(), storeweek.toString(), newCourseContainer);
                completedTasks++;
            }
        }
    }




    // 定义线程任务
    class GradeThread implements Runnable {
        private int findTerm;
        private int findYear;

        private int storeTerm;
        private int storeYear;
        public GradeThread(int findTerm, int findYear,int term ,int y) {
            this.findTerm = findTerm;
            this.findYear = findYear;
            this.storeTerm=term;
            this.storeYear=y;
        }

        @Override
        public void run() {
            // 在此处执行需要在后台线程中进行的任务
            // 例如调用异步任务中的代码
            new HomeFragment.GradeAsyncTask(findTerm, findYear,storeTerm,storeYear).execute();
        }
    }

    private class GradeAsyncTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
        private Integer Term;
        private Integer Year;
        private Integer storeTerm;
        private Integer storeYear;
        public GradeAsyncTask(Integer findTerm,Integer findYear,Integer Term,Integer Year) {
            this.Term=findTerm;
            this.Year=findYear;
            this.storeTerm=Term;
            this.storeYear=Year;
        }
        @Override
        protected List<Map<String, String>> doInBackground(Void... params) {
            try {
                user.loginIn();
                try {
                    Thread.sleep(500); // 500毫秒 = 0.5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                grade_result = user.getTd(user.get_grade(String.valueOf(Term + Year)));
                Log.d("UserCreation", grade_result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return grade_result;
        }
        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null && !result.isEmpty() && getView() != null) {
                myDBHelper.insertGradeData(user.getUsername(), storeYear.toString(), storeTerm.toString(), result);
                completedTasks++;
            }
        }
    }
}
