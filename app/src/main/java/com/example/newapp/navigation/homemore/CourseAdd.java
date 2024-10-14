package com.example.newapp.navigation.homemore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import com.example.newapp.R;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.User;
import com.example.newapp.navigation.HomeFragment;
import com.example.newapp.navigation.RoundedColorTextView;
import com.example.newapp.utils.DateUtils;
import com.example.newapp.utils.ToastUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.*;

public class CourseAdd extends AppCompatActivity {
    private static JsonArray courseJsonArray = new JsonArray();
    private static Set<String> courseUniqueKeys = new HashSet<>();
    private User user = User.getInstance();
    private MyDBHelper myDBHelper;
    private SharedPreferences sharedPreferences;
    Spinner yearSpinner;
    Spinner termSpinner;
    private GridLayout gridLayout;
    private AppCompatEditText course_name;
    private AppCompatEditText course_address;
    private AppCompatEditText course_section;
    private AppCompatEditText course_teacher;
    private AppCompatEditText course_week;
    private AppCompatEditText course_week_num;
    private AppCompatTextView warning;
    private static final String KEY_YEAR = "class_year";
    private static final String KEY_TERM = "class_term";
    int term=0;
    int year=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    private int gridLayoutWidth;
    private String name = "";
    private String weeks = "";
    private String section = "";
    private String teacher = "";
    private String address = "";
    private String week_num = "";
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courseadd);

        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象

        gridLayout = findViewById(R.id.gridlayout);
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听器以避免重复调用
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 获取宽度
                gridLayoutWidth = gridLayout.getWidth();
                // 处理宽度
            }
        });
        ImageView exitButton = findViewById(R.id.exitButton);

        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        savedTerm = sharedPreferences.getString(KEY_TERM, "0");

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView add_check_button = findViewById(R.id.add_check_button);
        add_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(warning.getText().equals("可以添加课程")){
                    if (myDBHelper.insertCourse(user.getUsername(), String.valueOf(year + term), weeks, name, section, address, teacher,week_num)) {
                        // 插入成功
                        ToastUtils.showToastShort(getApplicationContext(), "课程添加成功");
                        // 在第二个 Activity 中设置结果并返回
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        // 插入失败
                        ToastUtils.showToastShort(getApplicationContext(), "课程添加失败");
                    }
                }else{
                    ToastUtils.showToastShort(getApplicationContext(),"要求未满足,禁止添加课程");
                }
            }
        });

        yearSpinner = findViewById(R.id.classYearSpinner);
        termSpinner = findViewById(R.id.classTermSpinner);

        ArrayAdapter<String> adapter = null;
        if (user != null && user.getYears() != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user.getYears());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        if (adapter != null && yearSpinner != null) {
            yearSpinner.setAdapter(adapter);
            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedYear = (String) parent.getItemAtPosition(position);
                    String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                    if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                        saveSelection(KEY_YEAR, selectedYear);
                        String firstFourChars = selectedYear.substring(0, 4); // 获取前四个字符
                        int number = Integer.parseInt(firstFourChars); // 转换成int类型
                        year = (number - 2020) * 40 + 100;
                        //执行
                        TextChange();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 当没有选择任何项时触发此方法
                }
            });
        }

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selectedTerm);

                    if (selectedTerm.equals("第一学期")) {
                        term=0;
                    } else if (selectedTerm.equals("第二学期")) {
                        term=20;
                    } else {
                        term=0;
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    //执行
                    TextChange();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
        warning=findViewById(R.id.warning);

        course_name = findViewById(R.id.course_name);
        course_address = findViewById(R.id.course_address);
        course_section = findViewById(R.id.course_section);
        course_teacher = findViewById(R.id.course_teacher);
        course_week = findViewById(R.id.course_week);
        course_week_num = findViewById(R.id.course_week_num);
        addFocusChangeListener(course_name);
        addFocusChangeListener(course_address);
        addFocusChangeListener(course_section);
        addFocusChangeListener(course_teacher);
        addFocusChangeListener(course_week);
        addFocusChangeListener(course_week_num);
        setupEditTextWithEnterKey(course_name);
        setupEditTextWithEnterKey(course_address);
        setupEditTextWithEnterKey(course_section);
        setupEditTextWithEnterKey(course_teacher);
        setupEditTextWithEnterKey(course_week);
        setupEditTextWithEnterKey(course_week_num);

        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));

        int number;
        try {
            String firstFourChars = savedYear.substring(0, 4); // 获取前四个字符
            number = Integer.parseInt(firstFourChars); // 转换成int类型
        }catch (Exception e){
            Log.d("考试安排", "考试安排初始化出现问题");
            number=user.getCoverage_year();
        }
        year=(number-2020)*40+100;

        if (savedTerm.equals("第一学期")) {
            term = 0;
        } else if (savedTerm.equals("第二学期")) {
            term = 20;
        } else {
            term = 0;
        }
    }

    private void addFocusChangeListener(final AppCompatEditText editText) {
        final String[] previousText = {editText.getText().toString()};

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本内容改变之前的操作
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在文本改动之后执行的操作
                String currentText = s.toString();
                if (!currentText.equals(previousText[0])) {
                    Log.d("CourseAdd", "内容发生改变");
                    TextChange();
                }
                previousText[0] = currentText; // 保存当前内容作为下一次的上一次内容
            }
        });
    }

    private void TextChange() {
        //week匹配成功
        gridLayout.removeAllViews();
        Log.d("CourseAdd", "TextChange: "+String.valueOf(year+term)+course_name.getText()+
                course_week.getText()+course_section.getText()+course_teacher.getText()+course_address.getText()+course_week_num.getText());

        name = String.valueOf(course_name.getText());
        weeks = String.valueOf(course_week.getText());
        section = String.valueOf(course_section.getText());
        teacher = String.valueOf(course_teacher.getText());
        address = String.valueOf(course_address.getText());
        week_num = String.valueOf(course_week_num.getText());
        if(name.length()>0&&weeks.length()>0&&section.length()>0&&week_num.length()>0){
            if (section.length() == 1) {
                int sectionNumber = Integer.parseInt(section);
                if (sectionNumber >= 1 && sectionNumber <= 5) {
                    if (week_num.length() == 1) {
                        int weekNumber = Integer.parseInt(week_num);
                        if (weekNumber >= 1 && weekNumber <= 7) {
                            // section符合要求
                            // 根据中文标点或英文标点分隔week，然后判断每个元素是否是纯数字
                            String[] weekArray = weeks.split("[,，]"); // 使用中文逗号或英文逗号分隔
                            Boolean match = true;
                            for (String day : weekArray) {
                                if (!day.matches("\\d+")) {
                                    Log.d("CourseAdd", "week中的元素必须是纯数字");
                                    match=false;
                                    break;
                                }
                            }
                            if(match){
                                courseJsonArray = new JsonArray();
                                courseUniqueKeys.clear();

                                ClassAsyncThread asyncThread = new ClassAsyncThread(); // 传入初始的 weekNum
                                asyncThread.executeTasks(weekArray);

                            }else{
                                warning.setTextColor(getResources().getColor(R.color.fire_red));
                                warning.setText("week格式不正确");
                            }
                        }else{
                            warning.setTextColor(getResources().getColor(R.color.fire_red));
                            warning.setText("星期必须是1到7之间的数字");
                        }
                    }else{
                        warning.setTextColor(getResources().getColor(R.color.fire_red));
                        warning.setText("星期必须是一位数字");
                    }
                } else {
                    warning.setTextColor(getResources().getColor(R.color.fire_red));
                    warning.setText("节课数必须是1到5之间的数字");
                }
            } else {
                warning.setTextColor(getResources().getColor(R.color.fire_red));
                warning.setText("节课数必须是一位数字");
            }
        }else{
            warning.setTextColor(getResources().getColor(R.color.fire_red));
            warning.setText("必填内容未满足要求");
        }
    }

    public void setupEditTextWithEnterKey(final AppCompatEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // 在这里执行完成输入的操作，比如隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true; // 返回true表示已处理该事件
                }
                return false; // 返回false表示未处理该事件
            }
        });
    }

    public class ClassAsyncThread {
        private List<Future<JsonArray>> futures = new ArrayList<>();
        private ExecutorService executor = Executors.newFixedThreadPool(5); // 创建一个固定大小为5的线程池

        public void executeTasks(String[] weekArray) {
            for (String day : weekArray) {
                int course_week = Integer.parseInt(day) - 1;
                Callable<JsonArray> task = new Callable<JsonArray>() {
                    @Override
                    public JsonArray call() throws Exception {
                        return doInBackground(course_week);
                    }
                };
                Future<JsonArray> future = executor.submit(task);
                futures.add(future);
            }

            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // 等待所有任务完成
                for (Future<JsonArray> future : futures) {
                    JsonArray result = future.get();
                    onPostExecute(result);
                }
                // 所有任务完成后执行接下来的代码
                // 执行后续代码
                Boolean courseContain = false;
                String courseName = "";
                // 遍历 JsonArray 中的元素
                Log.d("CourseAdd", "courseJsonArray"+courseJsonArray.toString());
                for (int i = 0; i < courseJsonArray.size(); i++) {
                    JsonObject jsonObject = courseJsonArray.get(i).getAsJsonObject();

                    String section_data = jsonObject.get("section").getAsString();
                    String week_data = jsonObject.get("week").getAsString();
                    Log.d("CourseAdd", "Section"+section);
                    Log.d("CourseAdd", "week"+weeks);
                    if(section_data.equals(String.valueOf((Integer.parseInt(section)*2)-1))&&week_data.equals(week_num)){
                        courseContain = true;
                        courseName = jsonObject.get("course_name").getAsString();
                    }
                }

                if(courseContain){
                    warning.setTextColor(getResources().getColor(R.color.fire_red));
                    warning.setText("添加课程与{{{"+courseName+"}}}冲突");
                }else{
                    warning.setTextColor(getResources().getColor(R.color.icon_blue));
                    warning.setText("可以添加课程");
                    RoundedColorTextView roundedColorTextView = new RoundedColorTextView();
                    TextView textView = roundedColorTextView.createTextViewWithRoundedColorBackground(getApplicationContext(), "Your Text Here");
                    textView.setText(name + "\n" + address+"\n"+teacher);
                    textView.setTextSize(10);
                    textView.setGravity(Gravity.CENTER);

                    GridLayout.Spec rowSpec = GridLayout.spec((Integer.parseInt(section)*2)-2); // 指定行
                    GridLayout.Spec columnSpec = GridLayout.spec(Integer.parseInt(week_num)-1); // 指定列
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                    params.setGravity(Gravity.FILL);
                    // 设置固定的宽度和高度
                    params.width = gridLayoutWidth/7; // 设置为您希望的宽度，单位是像素或dp
                    params.height = 360; // 设置为您希望的高度，单位是像素或dp
                    textView.setLayoutParams(params);
                    // 将TextView添加到GridLayout中
                    gridLayout.addView(textView);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        private JsonArray doInBackground(int course_week) {
            try {
                JsonObject Semester_Date = myDBHelper.getJsonDataBySemesterId(String.valueOf(year + term));
                JsonArray weekArray = Semester_Date.get("weekArray").getAsJsonArray();

                if (course_week < weekArray.size()) {
                    JsonElement element = weekArray.get(course_week);
                    if (!element.isJsonNull() && element.isJsonObject()) {
                        JsonObject weekObject = element.getAsJsonObject();
                        return user.get_class(String.valueOf(year + term), weekObject.get("startDate").getAsString(), weekObject.get("endDate").getAsString());
                    } else {
                        return new JsonArray();
                    }
                } else {
                    return new JsonArray();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new JsonArray();
            }
        }

        private void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming class_results is a field in your class to store the JSON data
                UpdateCourseTable(result);
            } else {
                // Handle the case where result is null, if needed
            }
        }
    }

    private void UpdateCourseTable(JsonArray result){

        List<Integer> datesWithoutData = new ArrayList<>();
        datesWithoutData.add(1);
        datesWithoutData.add(2);
        datesWithoutData.add(3);
        datesWithoutData.add(4);
        datesWithoutData.add(5);
        datesWithoutData.add(6);
        datesWithoutData.add(7);

        for (JsonElement class_result : result) {
            JsonObject arrange_lesson_object = class_result.getAsJsonObject();
            String course_name=arrange_lesson_object.get("course_name").getAsString();

            String date=arrange_lesson_object.get("date").getAsString();
            if(datesWithoutData.contains(Integer.valueOf(DateUtils.getDayOfWeek(date)))){
                datesWithoutData.remove(Integer.valueOf(DateUtils.getDayOfWeek(date)));
            }
            Integer start_unit=arrange_lesson_object.get("start_unit").getAsInt();

            JsonArray teachers_json=arrange_lesson_object.getAsJsonArray("teachers");
            JsonObject teachers = teachers_json.get(0).getAsJsonObject();
            String teachers_name=teachers.get("name").getAsString();

            JsonArray lesson_arrange_json=arrange_lesson_object.getAsJsonArray("rooms");
            JsonObject rooms = lesson_arrange_json.get(0).getAsJsonObject();
            String rooms_name=rooms.get("name").getAsString();

            // 创建TextView来显示课程名称和地点
            RoundedColorTextView roundedColorTextView = new RoundedColorTextView();
            TextView textView = roundedColorTextView.createTextViewWithRoundedColorBackground(this, "Your Text Here");
            textView.setText(course_name + "\n" + rooms_name+"\n"+teachers_name);
            textView.setTextSize(10);
            textView.setGravity(Gravity.CENTER);
            // 设置TextView在GridLayout中的位置
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(start_unit - 1, 1, 1f);  // 注意，GridLayout的行数从0开始，而节次从1开始，需要做适当的转换
            params.columnSpec = GridLayout.spec(DateUtils.getDayOfWeek(date) - 1,1); // 同样，周数也需要适当的转换
            params.setGravity(Gravity.FILL);
            // 设置固定的宽度和高度
            params.width = gridLayoutWidth/7; // 设置为您希望的宽度，单位是像素或dp
            params.height = 360; // 设置为您希望的高度，单位是像素或dp
            textView.setLayoutParams(params);
            // 将TextView添加到GridLayout中
            addCourse(course_name,String.valueOf(start_unit),String.valueOf(DateUtils.getDayOfWeek(date)));
            gridLayout.addView(textView);
        }

        Log.d("更新课表", "不存在数据的星期数: "+datesWithoutData.toString());
        for (Integer missingDate : datesWithoutData) {
            TextView emptyTextView = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(9,1,1f); // 设置空TextView所在行，默认为0
            params.columnSpec = GridLayout.spec(missingDate - 1); // 设置空TextView所在列

            params.width = gridLayoutWidth/7; // 设置宽度
            params.height = 360; // 设置高度

            emptyTextView.setLayoutParams(params);

            gridLayout.addView(emptyTextView);
        }
    }

    private void saveSelection(String key, String value) {
        Log.d("CourseAdd", "saveSelection: "+key+":"+value);
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

    private static void addCourse(String courseName, String section, String week) {
        JsonObject course = new JsonObject();
        course.addProperty("course_name", courseName);
        course.addProperty("section", section);
        course.addProperty("week", week);

        String key = courseName + section + week;
        if (!courseUniqueKeys.contains(key)) {
            courseUniqueKeys.add(key);
            courseJsonArray.add(course);
        }
    }
}
