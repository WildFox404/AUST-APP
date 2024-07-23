package com.example.newapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.*;


// 创建一个继承自Service的类
public class MyService extends Service {
    private String savedIds;
    private User user;
    private TextView courseProcess;
    private LinearLayout courseWait;
    private List<Map<String, String>> grade_result;
    List<List<String>> result;
    private MyDBHelper myDBHelper;
    int completedTasks=0;
    @Override
    public void onCreate() {
        super.onCreate();

        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象，传入Service的上下文

        // 获取布局文件中的元素
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home, null);

        // 找到布局文件中的特定元素
        courseWait =view.findViewById(R.id.courseWait);
        courseProcess = view.findViewById(R.id.courseProcess);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在这里执行你想要在后台运行的操作，比如网络请求、定时任务等
        // 注意：这里的操作会在主线程中执行，如果需要执行耗时操作，建议使用子线程或者异步任务
        Bundle bundle = intent.getBundleExtra("bundle");
        savedIds = bundle.getString("savedIds");
        String savedUsername=bundle.getString("savedUsername");
        String savedPassword=bundle.getString("savedPassword");
        user=new User(savedUsername,savedPassword);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int year = Integer.parseInt(user.getUsername().substring(0, 4));
                int findYear = 0;
                int findTerm = 0;
                int totalTasks = 4 * 2 * 21; // 总的异步任务数量

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

                findYear = 0;
                findTerm = 0;
                year = Integer.parseInt(user.getUsername().substring(0, 4));
//                try {
//                    user.loginIn();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

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
                        new Thread(new GradeThread(findTerm, findYear, term, y)).start();
                    }
                }
                int completedTasks = 0;
                while (completedTasks < totalTasks) {
                    try {
                        Thread.sleep(1000);
                        completedTasks++; // Simulate completion of tasks
                        // 发送广播通知Activity更新UI
                        Intent progressIntent = new Intent("progress_update");
                        progressIntent.putExtra("completed_tasks", completedTasks);
                        LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(progressIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 发送广播通知Activity隐藏等待视图
                Intent hideIntent = new Intent("hide_wait_view");
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(hideIntent);
            }
        }).start();

        stopSelf(); // Stop the service when tasks are completed

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 如果你的Service需要与Activity或其他组件进行交互，可以在这里返回一个IBinder对象
        return null;
    }

    @Override
    public void onDestroy() {
        // 在Service销毁时执行一些清理操作
    }


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
                user.login();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //result = user.getTd_class(user.get_class(String.valueOf(findTerm + findYear),String.valueOf(findWeek),String.valueOf(savedIds)));
                Log.d("UserCreation", result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<List<String>> result) {
            if (result != null && !result.isEmpty()) {
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
                user.login();
                try {
                    Thread.sleep(500); // 500毫秒 = 0.5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //grade_result = user.getTd(user.get_grade(String.valueOf(Term + Year)));
                Log.d("UserCreation", grade_result.toString());
            } catch (IOException e) {
                e.printStackTrace(); // 处理异常
            }
            return grade_result;
        }
        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null && !result.isEmpty()) {
                myDBHelper.insertGradeData(user.getUsername(), storeYear.toString(), storeTerm.toString(), result);
                completedTasks++;
            }
        }
    }
}