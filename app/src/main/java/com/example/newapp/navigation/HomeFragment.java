package com.example.newapp.navigation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.*;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.example.newapp.*;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.User;
import com.example.newapp.utils.DateUtils;
import com.example.newapp.utils.DeviceDataUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private Boolean image_switch = true;
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;
    private static final String KEY_YEAR = "class_year";
    private static final String KEY_TERM = "class_term";
    private static final String KEY_WEEK = "class_week";
    private User user;
    private DeviceDataUtils deviceDataUtils = DeviceDataUtils.getInstance();
    private JsonArray class_results;
    int term=0;
    int year=0;
    int week=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    String savedWeek;
    private GridLayout gridLayout;
    private String startDate;
    private String endDate;
    private View view;
    private int gridLayoutWidth;
    private void saveSelection(String key, String value) {
        Log.d("HomeActivity", "saveSelection: "+key+":"+value);
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

        view = inflater.inflate(R.layout.home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        gridLayout = view.findViewById(R.id.gridlayout);
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

        Spinner yearSpinner = view.findViewById(R.id.classYearSpinner);
        Spinner termSpinner = view.findViewById(R.id.classTermSpinner);
        Spinner weekSpinner = view.findViewById(R.id.classWeekSpinner);

        // 从 SharedPreferences 中加载上次存储的数据
        savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        savedTerm = sharedPreferences.getString(KEY_TERM, "0");
        savedWeek = sharedPreferences.getString(KEY_WEEK, "0");

        user=User.getInstance();

        setScrollViewHeight();

        LinearLayout linearLayoutHead = view.findViewById(R.id.linearLayoutHead);
        TextView textView1 = view.findViewById(R.id.textview1);
        int heightPx = linearLayoutHead.getHeight();

        ImageView more = view.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator rotationAnimator;
                Drawable drawable = more.getDrawable();
                // 设置颜色过滤器
                // 将颜色设置为红色

                if(image_switch){
                    drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    rotationAnimator = ObjectAnimator.ofFloat(view.findViewById(R.id.more), "rotation", 0f, 45f);
                    // 创建并设置动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 1000);
                    translateAnimation.setDuration(300); // 设置动画持续时间为1秒
                    textView1.startAnimation(translateAnimation);
                } else {
                    drawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                    rotationAnimator = ObjectAnimator.ofFloat(view.findViewById(R.id.more), "rotation", 45f, 0f);
                    TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -heightPx);
                    translateAnimation.setDuration(300); // 设置动画持续时间为1秒
                    textView1.startAnimation(translateAnimation);
                }

                image_switch = !image_switch;

                rotationAnimator.setDuration(300);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(rotationAnimator);
                animatorSet.start();
            }
        });

        ArrayAdapter<String> adapter = null;
        if (getContext() != null && user != null && user.getYears() != null) {
            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, user.getYears());
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
                        new ClassAsyncTask().execute();
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
                        // 其他情况的处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    new ClassAsyncTask().execute();
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
                        week = Integer.parseInt(weekNumber)-1;
                    } else {
                        // 未找到匹配的内容处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    new ClassAsyncTask().execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));
        weekSpinner.setSelection(getIndex(weekSpinner, savedWeek));



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
        Pattern pattern = Pattern.compile("第(\\d+)周");
        Matcher matcher = pattern.matcher(savedWeek);
        if (matcher.find()) {
            String weekNumber = matcher.group(1);
            week = Integer.parseInt(weekNumber)-1;
        }
        new ClassAsyncTask().execute();
        return view;
    }

    private void setScrollViewHeight() {
        int linearLayout1 = view.findViewById(R.id.linearLayoutHead).getHeight();
        int linearLayout2 = view.findViewById(R.id.SpinnerLayout).getHeight();
        int linearLayout3 = view.findViewById(R.id.WeekLayout).getHeight();
        int height = deviceDataUtils.getHeight();
        ScrollView scrollView =  view.findViewById(R.id.scrollView);
        ViewGroup.LayoutParams params= scrollView.getLayoutParams();
        params.height = height-linearLayout1-linearLayout2-linearLayout3;
        scrollView.setLayoutParams(params);
    }

    private class ClassAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {
                JsonObject Semester_Date = myDBHelper.getJsonDataBySemesterId(String.valueOf(year + term));
                JsonArray weekArray = Semester_Date.get("weekArray").getAsJsonArray();
                if (week < weekArray.size()) {
                    JsonElement element = weekArray.get(week);
                    if (!element.isJsonNull() && element.isJsonObject()) {
                        JsonObject weekObject = element.getAsJsonObject();
                        startDate = weekObject.get("startDate").getAsString();
                        endDate = weekObject.get("endDate").getAsString();
                        return user.get_class(String.valueOf(year + term), startDate, endDate);
                    } else {
                        // 创建一个空的JsonArray
                        JsonArray emptyArray = new JsonArray();
                        return emptyArray;
                        // 处理索引超出范围的情况，可以选择抛出异常或者返回默认值
                    }
                } else {
                    // 创建一个空的JsonArray
                    JsonArray emptyArray = new JsonArray();
                    return emptyArray;
                    // 处理索引超出范围的情况，可以选择抛出异常或者返回默认值
                }
            } catch (Exception e) {
                Log.e("课表获取", "处理结果时出现异常: " + e.getMessage());
            }
            // 创建一个空的JsonArray
            JsonArray emptyArray = new JsonArray();
            return emptyArray;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            try {
                if (result != null) {
                    // Assuming class_results is a field in your class to store the JSON data
                    class_results = result;
                    Log.d("课表获取", "课表获取成功");
                    UpdateCourseTable();
                } else {
                    // Handle the case where result is null, if needed
                    Log.e("课表获取", "返回结果为空");
                    // Possibly throw an exception or handle accordingly
                }
            } catch (Exception e) {
                // Handle any exception that might occur during the execution of onPostExecute
                Log.e("课表获取", "处理结果时出现异常: " + e.getMessage());
                // Optionally, you can re-throw the exception if it should propagate further
                // throw e;
            }
        }
    }
    private void UpdateCourseTable(){
        String[] weekDates =DateUtils.getDatesForWeek(startDate);
        if(weekDates.length==7){
            TextView week1 = view.findViewById(R.id.week1);
            TextView week2 = view.findViewById(R.id.week2);
            TextView week3 = view.findViewById(R.id.week3);
            TextView week4 = view.findViewById(R.id.week4);
            TextView week5 = view.findViewById(R.id.week5);
            TextView week6 = view.findViewById(R.id.week6);
            TextView week7 = view.findViewById(R.id.week7);
            week1.setText(weekDates[0].substring(5));
            week2.setText(weekDates[1].substring(5));
            week3.setText(weekDates[2].substring(5));
            week4.setText(weekDates[3].substring(5));
            week5.setText(weekDates[4].substring(5));
            week6.setText(weekDates[5].substring(5));
            week7.setText(weekDates[6].substring(5));
        }

        String yearString = String.valueOf(year);
        String termString = String.valueOf(term);
        String weekString = String.valueOf(week);
        Log.d("查询参数", "学年"+yearString+"学期"+termString+"周"+weekString);

        List<Integer> datesWithoutData = new ArrayList<>();
        datesWithoutData.add(1);
        datesWithoutData.add(2);
        datesWithoutData.add(3);
        datesWithoutData.add(4);
        datesWithoutData.add(5);
        datesWithoutData.add(6);
        datesWithoutData.add(7);
        // 清空之前的内容
        gridLayout.removeAllViews();

        int class_number =0;
        for (JsonElement class_result : class_results) {
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
            TextView textView = roundedColorTextView.createTextViewWithRoundedColorBackground(getContext(), "Your Text Here");
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
            gridLayout.addView(textView);
            class_number++;
        }

        TextView empty_class_text =view.findViewById(R.id.empty_class_text);
        ImageView empty_class_image =view.findViewById(R.id.empty_class_image);
        if(class_number>0){
            empty_class_text.setText("");
            empty_class_image.setImageDrawable(new ColorDrawable(Color.TRANSPARENT)); // 设置为透明
        }else{
            int[] emptyImages = {R.drawable.empty_class1, R.drawable.empty_class5,
                    R.drawable.empty_class11, R.drawable.empty_class12,
                    R.drawable.empty_class14};
            Random rand = new Random();
            int randomIndex = rand.nextInt(emptyImages.length);

            empty_class_text.setText("这周没课捏AWA");
            empty_class_image.setImageResource(emptyImages[randomIndex]);
        }


        Log.d("更新课表", "不存在数据的星期数: "+datesWithoutData.toString());
        for (Integer missingDate : datesWithoutData) {
            TextView emptyTextView = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(0,1,1f); // 设置空TextView所在行，默认为0
            params.columnSpec = GridLayout.spec(missingDate - 1); // 设置空TextView所在列

            params.width = gridLayoutWidth/7; // 设置宽度
            params.height = 360; // 设置高度

            emptyTextView.setLayoutParams(params);

            gridLayout.addView(emptyTextView);
        }
    }
}
