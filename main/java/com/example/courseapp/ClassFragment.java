package com.example.courseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassFragment extends Fragment {
    User class_user;
    int term=0;
    int year=0;
    int week=0;
    private SharedViewModel viewModel;
    List<List<String>> result;
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_YEAR = "year";
    private static final String KEY_TERM = "term";
    private static final String KEY_WEEK = "week";
    String ids;
    public ClassFragment() {
        // Required empty public constructor
    }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        Spinner yearSpinner = view.findViewById(R.id.class_year_spinner);
        Spinner termSpinner = view.findViewById(R.id.class_term_spinner);
        Spinner weekSpinner = view.findViewById(R.id.class_week_spinner);
        TableLayout tableLayout = view.findViewById(R.id.class_tablelayout);
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        String savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        String savedWeek = sharedPreferences.getString(KEY_WEEK, "0");
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));
        weekSpinner.setSelection(getIndex(weekSpinner, savedWeek));
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        ids="";
        class_user = viewModel.getUser();
        // Inflate the layout for this fragment

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                saveSelection(KEY_YEAR, parent.getItemAtPosition(position).toString());
                //参数处理不公开
                // 在这里启动异步任务来处理网络操作
                new ClassFragment.ClassAsyncTask().execute();
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
                saveSelection(KEY_TERM, parent.getItemAtPosition(position).toString());
                //参数处理不公开
                // 在这里启动异步任务来处理网络操作
                new ClassFragment.ClassAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                saveSelection(KEY_WEEK, parent.getItemAtPosition(position).toString());
                //参数处理不公开
                // 在这里启动异步任务来处理网络操作
                new ClassFragment.ClassAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        return view;
    }
    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.toString();
    }
    private class ClassAsyncTask extends AsyncTask<Integer, Void, List<List<String>>> {

        public ClassAsyncTask() {

        }
        @Override
        protected List<List<String>> doInBackground(Integer... params) {
            try {
                class_user.loginIn();
                //参数处理不公开
                result = class_user.getTd_class(class_user.get_class(String.valueOf(0),String.valueOf(0),""));
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }
        private String insertNewlines(String input) {
            StringBuilder output = new StringBuilder(input);
            int len = input.length();
            for (int i = 6; i < len; i += 7) {
                output.insert(i, "\n");
            }
            return output.toString();
        }
        private String insertCourselines(String input) {
            StringBuilder output = new StringBuilder(input);
            int len = input.length();
            for (int i = 5; i < len; i += 6) {
                output.insert(i, "\n");
            }
            return output.substring(0, Math.min(output.length(), 20));
        }

        @Override
        protected void onPostExecute(List<List<String>> result) {
            if (result != null && result.size() > 0) {
                if (getView() != null) {
                    HashMap<Integer, List<Course>> courseContainer = new HashMap<>();
                    // 初始化容器，包括1到12节
                    for (int i = 1; i <= 12; i++) {
                        courseContainer.put(i, new ArrayList<>());
                    }
                    for (List<String> courseInfo : result) {
                        // 解析课程的基本信息
                        String courseName = courseInfo.get(0);
                        String courseLocation = courseInfo.get(1);

                        // 循环处理每对上课周和节课数
                        for (int i = 2; i < courseInfo.size(); i += 2) {
                            int Week = Integer.parseInt(courseInfo.get(i));
                            int Section = Integer.parseInt(courseInfo.get(i + 1))+1;
                            // 创建 Course 对象并添加到数组中
                            Course course = new Course(courseName, courseLocation, Week, Section);
                            courseContainer.get(Section).add(course);
                        }
                    }

                    TableLayout tableLayout = getView().findViewById(R.id.class_tablelayout);
                    // 其他代码...
                    // 清除表格中的旧数据
                    tableLayout.removeAllViews();
                    TableRow headerRow = new TableRow(getContext());
                    // 添加空内容
                    TextView emptyTextView = new TextView(getContext());
                    emptyTextView.setText("");
                    headerRow.addView(emptyTextView);

                    // 添加星期一至星期五
                    for (int i = 1; i <= 5; i++) {
                        TextView dayTextView = new TextView(getContext());
                        dayTextView.setText("星期" + i);
                        headerRow.addView(dayTextView);
                    }
                    tableLayout.addView(headerRow);
                    for (int i = 1; i <= 12; i++) {
                        List<Course> courseList = courseContainer.get(i);
                        TableRow row = new TableRow(getContext());
                        TextView sectionTextView = new TextView(getContext());
                        sectionTextView.setText("第" + i + "节");
                        row.addView(sectionTextView);
                        List<String> list = new ArrayList<>(Arrays.asList("", "", "", "", ""));
                        for (Course course : courseList){
                            int week = course.getStartWeek();
                            String CourseName=course.getCourseName();
                            String CourseLocation=course.getCourseLocation();

                            list.set(week, insertCourselines(CourseName)+"\n"+insertNewlines(CourseLocation));
                        }
                        for (String item : list) {
                            TextView sectionView = new TextView(getContext());
                            sectionView.setText(item);
                            sectionView.setTextSize(12); // 设置字号为12号
                            sectionView.setMinHeight(200);
                            row.addView(sectionView);
                        }
                        tableLayout.addView(row);
                    }
                } else {
                    // 处理getView()为空的情况
                }
            } else {
                // 处理result为空或大小为0的情况
            }
        }
    }
}