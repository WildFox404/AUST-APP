package com.example.newapp.emptyclassrooms;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.User;
import com.example.newapp.utils.DateUtils;
import com.example.newapp.utils.DeviceDataUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;

public class EmptyClassroomsActivity extends AppCompatActivity {
    private User user =User.getInstance();
    private JsonArray classrooms_results;
    private String building_id;
    private String date_param;
    private LinearLayout classroom_linearLayout;
    private Map<String,Integer> six_days_data;
    private DeviceDataUtils deviceDataUtils = DeviceDataUtils.getInstance();
    private SharedPreferences sharedPreferences;
    private LinearLayout[] linearLayouts = new LinearLayout[6];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptyclassroomsview);
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        for (int i = 0; i < linearLayouts.length; i++) {
            linearLayouts[i] = new LinearLayout(this); // 用合适的 context 实例化
        }

        building_id = getIntent().getStringExtra("building_id");
        date_param= DateUtils.getCurrentDate();
        six_days_data=DateUtils.getNextSixDaysAndWeekday(date_param);
        UpdateClassroomTableHeader(linearLayouts[0]);
    }


    private class ClassroomAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {
                return user.get_empty_classrooms_byId(building_id,date_param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                classrooms_results = result;
                Log.d("考试安排获取", "考试安排获取成功");
                UpdateClassroomTableContent();
            }
        }
    }


    private void UpdateClassroomTableHeader(LinearLayout linearlayout_v) {
        //分离父视图
        for (int i = 0; i < linearLayouts.length; i++) {
            LinearLayout parentView = (LinearLayout) linearLayouts[i].getParent();
            if (parentView != null) {
                parentView.removeView(linearLayouts[i]);
            }
        }

        // 创建一个GradientDrawable对象
        GradientDrawable border = new GradientDrawable();
        // 设置边框颜色和宽度
        border.setStroke(5, Color.BLUE);
        // 设置圆角半径
        border.setCornerRadius(10);

        linearlayout_v.setBackground(border);

        Paint paint = new Paint();
        paint.setTextSize(15);
        String text = "2024-1-1";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        classroom_linearLayout=findViewById(R.id.classroom_linearLayout);
        classroom_linearLayout.removeAllViews();
        LinearLayout header_linearlayout =new LinearLayout(this);

        header_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
        header_linearlayout.removeAllViews();

        int[] weeksImages = {R.drawable.week1, R.drawable.week2, R.drawable.week3,
                R.drawable.week4,R.drawable.week5,R.drawable.week6,R.drawable.week7,};

        float marginDp = 1; // 10dp 的距离
        float marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
        int margin_width=Math.round(marginPx);

        int width= deviceDataUtils.getWidth()-deviceDataUtils.getMargin_1dp()*10;

        Log.d("EmptyClassroomsActivity", "UpdateClassroomTableHeader: "+String.valueOf(width));

        int image_width=Math.round((width-(width/9)-(margin_width*14))/6);

        LinearLayout.LayoutParams imageviewlayoutParams = new LinearLayout.LayoutParams(image_width, image_width*3/5);
        LinearLayout.LayoutParams header_element_linearlayout_Params = new LinearLayout.LayoutParams(image_width,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(image_width, bounds.height()+bounds.width());

        List<String> keys = new ArrayList<>(six_days_data.keySet());
        Collections.sort(keys);
        int linearlayout_index=0;
        for (String key : keys){
            LinearLayout header_element_linearlayout = linearLayouts[linearlayout_index];
            header_element_linearlayout.removeAllViews();

            linearlayout_index++;
            header_element_linearlayout.setOrientation(LinearLayout.VERTICAL);

            int value;
            if (six_days_data.containsKey(key)) {
                value = six_days_data.get(key);
                // 对存在key的情况进行处理
            } else {
                // 对不存在key的情况进行处理
                value = 1;
            }

            // 创建一个新的 ImageView 对象
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(weeksImages[value-1]);
            // 设置 ImageView 的布局参数
            imageView.setLayoutParams(imageviewlayoutParams);
            System.out.println("Key: " + key + ", Value: " + value);

            TextView WeekColumn = new TextView(this);
            WeekColumn.setText(key.substring(6) != null ? key.substring(6) : "");
            WeekColumn.setTextSize(15); // 设置字号为12号
            WeekColumn.setMinHeight(200);
            WeekColumn.setLayoutParams(textLayoutParams);
            WeekColumn.setGravity(Gravity.CENTER);

            header_element_linearlayout_Params.setMargins(1,0,1,0);
            header_element_linearlayout.addView(imageView);
            header_element_linearlayout.addView(WeekColumn);
            header_element_linearlayout.setLayoutParams(header_element_linearlayout_Params);
            header_element_linearlayout.setTag(key);
            // 将GradientDrawable对象设置为LinearLayout的背景
            header_element_linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 隐藏所有其他LinearLayout的边框
                    for (int j = 0; j < linearLayouts.length; j++) {
                        if (linearLayouts[j] != v) {
                            linearLayouts[j].setBackground(null);
                        }else{
                            v.setBackground(border);
                            date_param = (String) v.getTag();
                            UpdateClassroomTableHeader(linearLayouts[j]);
                        }
                    }
                }
            });
            header_linearlayout.addView(header_element_linearlayout);
        }
        LinearLayout.LayoutParams calenderimageviewlayoutParams = new LinearLayout.LayoutParams(width/9, width/9);
        // 设置垂直居中
        calenderimageviewlayoutParams.gravity = Gravity.CENTER_VERTICAL;
        calenderimageviewlayoutParams.setMargins(2,0, 0,0);
        // 创建一个新的 ImageView 对象
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.calender);
        // 设置 ImageView 的布局参数
        imageView.setLayoutParams(calenderimageviewlayoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            int savedYear;
            int savedMonth;
            int savedDayOfMonth;
            @Override
            public void onClick(View view) {
                // 获取当前日期
                Calendar calendar = Calendar.getInstance();

                String savedDate = sharedPreferences.getString("EmptyClassroomsActivity_selectedDate", "");
                // Split the saved date into year, month, and day

                if (isValidDateFormat(savedDate)) {
                    // The savedDate is in the correct format
                    String[] parts = savedDate.split("-");
                    savedYear = Integer.parseInt(parts[0]);
                    savedMonth = Integer.parseInt(parts[1]) - 1; // Subtract 1 as months are zero-based
                    savedDayOfMonth = Integer.parseInt(parts[2]);
                } else {
                    // The savedDate is not in the correct format
                    savedYear = calendar.get(Calendar.YEAR);
                    savedMonth = calendar.get(Calendar.MONTH);
                    savedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                }
                // 创建一个DatePickerDialog用于让用户选择日期
                DatePickerDialog datePickerDialog = new DatePickerDialog(EmptyClassroomsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // 在这里处理用户选择的日期，可以将其传递给其他方法或保存到变量中

                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth); // 拼接所选的年月日
                        Log.d("dateSelect", "用户选择了日期: "+selectedDate);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("EmptyClassroomsActivity_selectedDate", selectedDate);
                        editor.apply();

                        date_param=selectedDate;
                        six_days_data=DateUtils.getNextSixDaysAndWeekday(selectedDate);
                        // 如果需要将所选日期返回给前一个Activity，则可以使用startActivityForResult()方法
//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("selectedDate", selectedDate);
//                        setResult(Activity.RESULT_OK, resultIntent);
                        UpdateClassroomTableHeader(linearLayouts[0]);
                    }
                }, savedYear, savedMonth, savedDayOfMonth);
                datePickerDialog.show();
            }
        });
        header_linearlayout.addView(imageView);
        classroom_linearLayout.addView(header_linearlayout);

        // 创建一个新的 View 对象作为分割线
        View divider = new View(this);
        // 设置分割线的宽度和高度
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
        // 设置分割线的颜色
        divider.setBackgroundColor(Color.BLUE); // 请替换成你自己定义的颜色
        classroom_linearLayout.addView(divider);
        new ClassroomAsyncTask().execute();
    }

    private void UpdateClassroomTableContent() {
        int width=(deviceDataUtils.getWidth()-deviceDataUtils.getMargin_1dp()*24)/12;
        LinearLayout.LayoutParams imageviewlayoutParams = new LinearLayout.LayoutParams(width, width);
        ScrollView classrooms_content_scrollview =new ScrollView(this);
        LinearLayout classrooms_content_scrollview_child =new LinearLayout(this);
        classrooms_content_scrollview_child.setOrientation(LinearLayout.VERTICAL);
        for (JsonElement classrooms_result : classrooms_results) {
            JsonObject classrooms_result_object = classrooms_result.getAsJsonObject();
            JsonArray rooms = classrooms_result_object.getAsJsonArray("rooms");
            for (JsonElement room : rooms) {
                LinearLayout room_linearlayout =new LinearLayout(this);
                room_linearlayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView room_text=new TextView(this);
                JsonObject room_object = room.getAsJsonObject();
                //教室名称
                String room_name = room_object.get("room_name").getAsString();
                //教室是否为空数据
                String occupy_units = room_object.get("occupy_units").getAsString();
                //教室类型
                String room_type = room_object.get("room_type").getAsString();
                //楼层
                String floor = room_object.get("floor").getAsString();
                //容量
                String room_capacity = room_object.get("room_capacity").getAsString();
                //校区
                String campus_name = room_object.get("campus_name").getAsString();
                //所在建筑
                String building_name = room_object.get("building_name").getAsString();
                room_text.setText(room_name+"("+room_type+")("+campus_name+","+building_name+"容量"+room_capacity+"楼层"+floor+")");
                room_text.setTextSize(11);
                room_linearlayout.addView(room_text);
                classrooms_content_scrollview_child.addView(room_linearlayout);

                LinearLayout room_status_linearlayout =new LinearLayout(this);
                room_status_linearlayout.setOrientation(LinearLayout.HORIZONTAL);

                Log.d("教室空闲数据", occupy_units);
                for (int i = 0; i < occupy_units.length(); i++) {
                    String c = occupy_units.substring(i, i + 1);
                    ImageView room_status_image=new ImageView(this);
                    if(c.equals("1")){
                        //有课
                        room_status_image.setImageResource(R.drawable.work);
                    }else if(c.equals("0")){
                        //无课
                        room_status_image.setImageResource(R.drawable.sleep);
                    }else{
                        room_status_image.setImageResource(R.drawable.work);
                        //报错
                        Log.w("EmptyClassroomsActivity", "空教室数据出错");
                    }
                    room_status_image.setLayoutParams(imageviewlayoutParams);

                    // 创建一个新的TextView来显示数字
                    TextView textView = new TextView(this);
                    textView.setText(String.valueOf(i+1)); // 设置要显示的数字
                    textView.setTextColor(getResources().getColor(R.color.bule_white)); // 设置文本颜色
                    textView.setTextSize(12); // 设置文本大小
                    textView.setLayoutParams(imageviewlayoutParams);
                    // 创建一个FrameLayout来包含ImageView和TextView
                    FrameLayout frameLayout = new FrameLayout(this);

                    // 将ImageView添加到FrameLayout中
                    frameLayout.addView(room_status_image);

                    // 设置TextView的位置，这里假设您想把数字放在图片的右上角
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER; // 设置TextView在右上角
                    textView.setLayoutParams(layoutParams);

                    // 将TextView添加到FrameLayout中
                    frameLayout.addView(textView);

                    room_status_linearlayout.addView(frameLayout);
                }
                // 创建一个新的 View 对象作为分割线
                View divider = new View(this);
                // 设置分割线的宽度和高度
                divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
                // 设置分割线的颜色
                divider.setBackgroundColor(Color.BLUE); // 请替换成你自己定义的颜色

                classrooms_content_scrollview_child.addView(room_status_linearlayout);
                classrooms_content_scrollview_child.addView(divider);
            }
        }
        classrooms_content_scrollview.addView(classrooms_content_scrollview_child);

        classroom_linearLayout.addView(classrooms_content_scrollview);
    }

    public boolean isValidDateFormat(String date) {
        String regex = "\\d{4}-\\d{2}-\\d{2}";

        return date.matches(regex);
    }
}
