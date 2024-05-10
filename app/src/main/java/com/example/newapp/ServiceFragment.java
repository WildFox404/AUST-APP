package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.*;

public class ServiceFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;

    private TextView gradeProcess;
    private SharedViewModel viewModel;
    private int completedTasks=0;
    private User user;
    List<Map<String, String>> result;
    private HomeFragment homeFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象
        View view = inflater.inflate(R.layout.service, container, false);
        ImageView courseRevise = view.findViewById(R.id.courseRevise);
        ImageView getGradeImageView = view.findViewById(R.id.getGrade);
        ImageView gradeView= view.findViewById(R.id.gradeview);
        ImageView gpaView= view.findViewById(R.id.gpaview);
        ImageView testView =view.findViewById(R.id.testview);
        ConstraintLayout gradeWait=view.findViewById(R.id.gradeWait);
        gradeProcess = view.findViewById(R.id.gradeProcess);
        homeFragment = new HomeFragment();

        user=viewModel.getUser();
        courseRevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDBHelper.deleteAllDataByUsername(savedUsername);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.addToBackStack(null); // 可选，添加到返回栈
                fragmentTransaction.commit();
            }
        });
        gradeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), GradeViewerActivity.class);
                startActivity(intent);
            }
        });
        testView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),TestViewerActivity.class);
                startActivity(intent);
            }
        });
        gpaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), GPAViewerActivity.class);
                startActivity(intent);
            }
        });
        getGradeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int findYear=0;
                int findTerm=0;
                int totalTasks=8;
                gradeWait.setVisibility(View.VISIBLE);
                int year = Integer.parseInt(user.getUsername().substring(0, 4));
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
                        new Thread(new GradeThread(findTerm, findYear,term,y)).start();
                    }
                }
                new Thread(new TestThread()).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (completedTasks < totalTasks) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gradeProcess.setText(completedTasks + "/" + totalTasks);
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
                                gradeWait.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }).start();
            }
        });
        return view;

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
            new GradeAsyncTask(findTerm, findYear,storeTerm,storeYear).execute();
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
                result = user.getTd(user.get_grade(String.valueOf(Term + Year)));
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null && !result.isEmpty() && getView() != null) {
                myDBHelper.insertGradeData(user.getUsername(), storeYear.toString(), storeTerm.toString(), result);
                completedTasks++;
            }
        }
    }

    class TestThread implements Runnable {

        public TestThread() {

        }

        @Override
        public void run() {
            // 在此处执行需要在后台线程中进行的任务
            // 例如调用异步任务中的代码
            new TestAsyncTask().execute();
        }
    }

    private class TestAsyncTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
        List<Map<String, String>> test_result;
        public TestAsyncTask() {

        }
        @Override
        protected List<Map<String, String>> doInBackground(Void... params) {
            try {
                user.loginIn();
                test_result = user.getTd_test(user.get_test());
                Log.d("UserCreation", test_result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return test_result;
        }
        @Override
        protected void onPostExecute(List<Map<String, String>> test_result) {
            if (result != null && !result.isEmpty() && getView() != null) {
                myDBHelper.insertTestData(user.getUsername(),test_result);
                completedTasks++;
            }
        }
    }
}