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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GradeFragment extends Fragment {
    User grade_user;
    private SharedViewModel viewModel;
    int term=0;
    int year=0;
    List<Map<String, String>> result;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_YEAR = "year";
    private static final String KEY_TERM = "term";
    SharedPreferences sharedPreferences;

    public GradeFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        Spinner yearSpinner = view.findViewById(R.id.grade_year_spinner);
        Spinner termSpinner = view.findViewById(R.id.grade_term_spinner);
        TableLayout tableLayout = view.findViewById(R.id.grade_tableLayout);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        grade_user = viewModel.getUser();
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        String savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        String savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));
        // 在GradeFragment中获取ConfigFragment中的EditText组件



        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                saveSelection(KEY_YEAR, parent.getItemAtPosition(position).toString());
                //参数处理不公开
                // 在这里启动异步任务来处理网络操作
                new GradeAsyncTask().execute();
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
                // 在这里处理用户选择的学期
                //参数处理不公开
                // 在这里启动异步任务来处理网络操作
                new GradeAsyncTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        return view;
    }
    //一定长度字符换行
    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
    }
    private class GradeAsyncTask extends AsyncTask<Integer, Void, List<Map<String, String>>> {

        public GradeAsyncTask() {

        }
        //网络请求
        @Override
        protected List<Map<String, String>> doInBackground(Integer... params) {
            try {
                grade_user.loginIn();
                //传参不公开
                result = grade_user.getTd(grade_user.get_grade(String.valueOf(0)));
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }
        //ui布局
        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null && result.size() > 0) {
                if (getView() != null) {
                    TableLayout tableLayout = getView().findViewById(R.id.grade_tableLayout);
                    // 其他代码...
                    // 清除表格中的旧数据
                    tableLayout.removeAllViews();

                    // 动态创建表头
                    TableRow headerRow = new TableRow(getContext());
                    // 创建表头的每一列
                    // 创建并添加课程类别列
                    TextView courseCategoryHeader = new TextView(getContext());
                    courseCategoryHeader.setText("课程类别");
                    headerRow.addView(courseCategoryHeader);

                    // 创建并添加最终成绩列
                    TextView finalGradeHeader = new TextView(getContext());
                    finalGradeHeader.setText("最终成绩");
                    headerRow.addView(finalGradeHeader);

                    // 创建并添加总评成绩列
                    TextView totalScoreHeader = new TextView(getContext());
                    totalScoreHeader.setText("总评成绩");
                    headerRow.addView(totalScoreHeader);

                    // 创建并添加课程名称列
                    TextView courseNameHeader = new TextView(getContext());
                    courseNameHeader.setText("课程名称");
                    headerRow.addView(courseNameHeader);

                    // 创建并添加学分列
                    TextView creditHeader = new TextView(getContext());
                    creditHeader.setText("学分");
                    headerRow.addView(creditHeader);

                    // 创建并添加绩点列
                    TextView GPAHeader = new TextView(getContext());
                    GPAHeader.setText("绩点");
                    headerRow.addView(GPAHeader);

                    // 将表头行添加到表格中
                    tableLayout.addView(headerRow);

                    // 动态创建并填充表格行
                    for (Map<String, String> data : result) {
                        TableRow row = new TableRow(getContext());

                        // 创建并填充每一列
                        TextView courseCategoryColumn = new TextView(getContext());
                        String courseCategory = data.get("课程类别");
                        courseCategoryColumn.setText(courseCategory != null ? insertNewlines(courseCategory) : "");
                        courseCategoryColumn.setTextSize(12); // 设置字号为12号
                        courseCategoryColumn.setMinHeight(200);
                        row.addView(courseCategoryColumn);

                        TextView finalGradeColumn = new TextView(getContext());
                        String finalGrade = data.get("最终");
                        finalGradeColumn.setText(finalGrade != null ? insertNewlines(finalGrade) : "");
                        finalGradeColumn.setTextSize(12); // 设置字号为12号
                        finalGradeColumn.setMinHeight(200);
                        row.addView(finalGradeColumn);

                        TextView totalScoreColumn = new TextView(getContext());
                        String totalScore = data.get("总评成绩");
                        totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
                        totalScoreColumn.setTextSize(12); // 设置字号为12号
                        totalScoreColumn.setMinHeight(200);
                        row.addView(totalScoreColumn);

                        TextView courseNameColumn = new TextView(getContext());
                        String courseName = data.get("课程名称");
                        courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
                        courseNameColumn.setTextSize(12); // 设置字号为12号
                        courseNameColumn.setMinHeight(200);
                        row.addView(courseNameColumn);

                        TextView creditColumn = new TextView(getContext());
                        String credit = data.get("学分");
                        creditColumn.setText(credit != null ? insertNewlines(credit) : "");
                        creditColumn.setTextSize(12); // 设置字号为12号
                        creditColumn.setMinHeight(200);
                        row.addView(creditColumn);

                        TextView GPAColumn = new TextView(getContext());
                        String GPA = data.get("绩点");
                        GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
                        GPAColumn.setTextSize(12); // 设置字号为12号
                        GPAColumn.setMinHeight(200);
                        row.addView(GPAColumn);

                        // 将表格行添加到表格中
                        tableLayout.addView(row);
                    }
                } else {
                    Log.d("UserCreation", "Result is null or empty");
                }

            } else {
                    // 处理视图为空的情况
            }

        }
    }
}
