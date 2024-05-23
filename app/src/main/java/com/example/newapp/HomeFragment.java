package com.example.newapp;

import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

        TextView qqTextView =view.findViewById(R.id.qqlink);
        TextView githubTextView=view.findViewById(R.id.githublink);
        qqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = "tPPUYS7eP54EcUs6qdDLt4m-Ufr_hzVb"; // 获取TextView中的QQ号
                // 构造跳转到QQ群页面的Intent
                Intent intent = new Intent();
                intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
                // 启动Intent，跳转到QQ群页面
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // 未安装手Q或安装的版本不支持
                }
            }
        });
        githubTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String githubLink = "https://github.com/";  // 替换成你要跳转的 GitHub 链接

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                intent.setSelector(null);

                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    // 有应用可以处理该 Intent，直接启动
                    startActivity(intent);
                } else {
                    // 没有应用可以处理该 Intent，提示用户打开浏览器
                    Toast.makeText(getContext(), "没有找到GitHub应用，将在浏览器中打开", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
                    startActivity(intent);
                }
            }
        });

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
        if (myDBHelper.getCourseCountByUsername(user.getUsername().toString()) == 0&&courseWait.getVisibility() == View.INVISIBLE) {
            Log.d("数据获取", "开始获取数据");
            courseWait.setVisibility(View.VISIBLE);
            Intent serviceIntent = new Intent(getActivity(), MyService.class);
            Bundle bundle = new Bundle();
            bundle.putString("savedIds", savedIds);
            bundle.putString("savedUsername",savedUsername);
            bundle.putString("savedPassword",savedPassword);
//            bundle.putInt("param2", param2Value);
            // 将更多的参数放入bundle中
            serviceIntent.putExtra("bundle", bundle);
            getActivity().startService(serviceIntent);
            UpdateCourseTable();
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
    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        int totalTasks=176;
        @Override
        public void onReceive(Context context, Intent intent) {
            int completedTasks = intent.getIntExtra("completed_tasks", 0);
            // 更新UI
            courseProcess.setText(completedTasks + "/" + totalTasks);
        }
    };

    private BroadcastReceiver hideWaitViewReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 隐藏等待视图
            courseWait.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册广播接收器
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(progressReceiver, new IntentFilter("progress_update"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(hideWaitViewReceiver, new IntentFilter("hide_wait_view"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(progressReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(hideWaitViewReceiver);
    }
}
