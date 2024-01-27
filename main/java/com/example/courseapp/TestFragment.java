package com.example.courseapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestFragment extends Fragment {
    User test_user;
    List<Map<String, String>> result;

    private SharedViewModel viewModel;
    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        test_user = viewModel.getUser();

        new TestFragment.GradeAsyncTask().execute();


        return view;
    }
    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 6; i < len; i += 7) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
    }
    private class GradeAsyncTask extends AsyncTask<Integer, Void, List<Map<String, String>>> {

        public GradeAsyncTask() {

        }
        @Override
        protected List<Map<String, String>> doInBackground(Integer... params) {
            try {
                test_user.loginIn();
                result = test_user.getTd_test(test_user.get_test());
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null && result.size() > 0) {
                if (getView() != null) {
                    TableLayout tableLayout = getView().findViewById(R.id.test_tableLayout);

                    // 清除表格中的旧数据
                    tableLayout.removeAllViews();

                    // 动态创建表头
                    TableRow headerRow = new TableRow(getContext());
                    // 创建表头的每一列
                    // 创建并添加课程类别列
                    TextView courseCategoryHeader = new TextView(getContext());
                    courseCategoryHeader.setText("课程名称");
                    headerRow.addView(courseCategoryHeader);

                    // 创建并添加最终成绩列
                    TextView finalGradeHeader = new TextView(getContext());
                    finalGradeHeader.setText("考试日期");
                    headerRow.addView(finalGradeHeader);

                    // 创建并添加总评成绩列
                    TextView totalScoreHeader = new TextView(getContext());
                    totalScoreHeader.setText("考试安排");
                    headerRow.addView(totalScoreHeader);

                    // 创建并添加课程名称列
                    TextView courseNameHeader = new TextView(getContext());
                    courseNameHeader.setText("考试地点");
                    headerRow.addView(courseNameHeader);

                    // 创建并添加学分列
                    TextView creditHeader = new TextView(getContext());
                    creditHeader.setText("座位");
                    headerRow.addView(creditHeader);

                    // 创建并添加绩点列
                    TextView GPAHeader = new TextView(getContext());
                    GPAHeader.setText("情况");
                    headerRow.addView(GPAHeader);

                    // 将表头行添加到表格中
                    tableLayout.addView(headerRow);
                    int color = ContextCompat.getColor(requireContext(), R.color.white);
                    // 动态创建并填充表格行
                    for (Map<String, String> data : result) {
                        TableRow row = new TableRow(getContext());

                        // 创建并填充每一列
                        TextView courseCategoryColumn = new TextView(getContext());
                        String courseCategory = data.get("课程名称");
                        courseCategoryColumn.setText(courseCategory != null ? insertNewlines(courseCategory) : "");
                        courseCategoryColumn.setTextSize(11); // 设置字号为12号
                        courseCategoryColumn.setMinHeight(200);
                        courseCategoryColumn.setTextColor(color);
                        row.addView(courseCategoryColumn);

                        TextView finalGradeColumn = new TextView(getContext());
                        String finalGrade = data.get("考试日期");
                        finalGradeColumn.setText(finalGrade != null ? insertNewlines(finalGrade) : "");
                        finalGradeColumn.setTextSize(11); // 设置字号为12号
                        finalGradeColumn.setMinHeight(200);
                        finalGradeColumn.setTextColor(color);
                        row.addView(finalGradeColumn);

                        TextView totalScoreColumn = new TextView(getContext());
                        String totalScore = data.get("考试安排");
                        totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
                        totalScoreColumn.setTextSize(11); // 设置字号为12号
                        totalScoreColumn.setMinHeight(200);
                        totalScoreColumn.setTextColor(color);
                        row.addView(totalScoreColumn);

                        TextView courseNameColumn = new TextView(getContext());
                        String courseName = data.get("考试地点");
                        courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
                        courseNameColumn.setTextSize(11); // 设置字号为12号
                        courseNameColumn.setMinHeight(200);
                        courseNameColumn.setTextColor(color);
                        row.addView(courseNameColumn);

                        TextView creditColumn = new TextView(getContext());
                        String credit = data.get("考场座位");
                        creditColumn.setText(credit != null ? insertNewlines(credit) : "");
                        creditColumn.setTextSize(11); // 设置字号为12号
                        creditColumn.setMinHeight(200);
                        creditColumn.setTextColor(color);
                        row.addView(creditColumn);

                        TextView GPAColumn = new TextView(getContext());
                        String GPA = data.get("考试情况");
                        GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
                        GPAColumn.setTextSize(11); // 设置字号为12号
                        GPAColumn.setMinHeight(200);
                        GPAColumn.setTextColor(color);
                        row.addView(GPAColumn);

//                    courseCategoryColumn.setBackgroundResource(R.drawable.cell_border);
//                    finalGradeColumn.setBackgroundResource(R.drawable.cell_border);
//                    totalScoreColumn.setBackgroundResource(R.drawable.cell_border);
//                    courseNameColumn.setBackgroundResource(R.drawable.cell_border);
//                    creditColumn.setBackgroundResource(R.drawable.cell_border);
//                    GPAColumn.setBackgroundResource(R.drawable.cell_border);

                        // 将表格行添加到表格中
                        tableLayout.addView(row);
                    }
                }else{

                }
            } else {
                Log.d("UserCreation", "Result is null or empty");
            }
        }
    }
}
