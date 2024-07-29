package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class EmptyBuildingsActivity extends AppCompatActivity {
    private User user = User.getInstance();
    private JsonArray building_results;
    private DeviceDataUtils deviceDataUtils = DeviceDataUtils.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptybuildingsview);
        ImageView exitButton = findViewById(R.id.exitButton);
        new BuildingAsyncTask().execute();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private class BuildingAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {
                return user.get_empty_classrooms();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                building_results = result;
                Log.d("考试安排获取", "考试安排获取成功");
                UpdateBuildingTable();
            }
        }
    }

    private void UpdateBuildingTable() {
        int[] buildingImages = {R.drawable.building1, R.drawable.building2, R.drawable.building3,
                R.drawable.building4, R.drawable.building5, R.drawable.building6, R.drawable.building7,
                R.drawable.building8, R.drawable.building9, R.drawable.building10, R.drawable.building11,
                R.drawable.building12, R.drawable.building13, R.drawable.building14};

        Paint paint = new Paint();
        paint.setTextSize(20);
        String text = "大创中心";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        float marginDp = 6; // 10dp 的距离
        float marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
        int margin_width = Math.round(marginPx);

        LinearLayout building_tableLayout = findViewById(R.id.building_linearLayout);
        int width = building_tableLayout.getWidth();

        int image_width = Math.round((width - margin_width * 8) / 4);

        building_tableLayout.setOrientation(TableLayout.VERTICAL);
        int elementsInRow = 0;
        int imageId = 0;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300, 1);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(image_width, bounds.height() + bounds.width());
        // 设置 ImageView 的布局参数
        LinearLayout.LayoutParams imageviewlayoutParams = new LinearLayout.LayoutParams(image_width, image_width);

        // 创建布局参数并设置margin
        LinearLayout.LayoutParams element_layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int element_marginInDp = 6; // 设置6dp的margin
        // 将dp单位转换为像素值
        float element_marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, element_marginInDp, getResources().getDisplayMetrics());

        element_layoutParams.setMargins((int) element_marginPx, 0, (int) element_marginPx, 0);

        LinearLayout currentlinearLayout = new LinearLayout(this);
        currentlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        currentlinearLayout.setLayoutParams(layoutParams);
//        currentlinearLayout.setBackgroundColor(Color.RED);
        for (JsonElement building_result : building_results) {
            JsonObject building_result_object = building_result.getAsJsonObject();
            String name = building_result_object.get("name").getAsString();
            JsonArray buildings = building_result_object.getAsJsonArray("buildings");
            TextView headCategory = new TextView(this);
            LinearLayout textlinearLayout = new LinearLayout(this);
            headCategory.setText(name);
            headCategory.setTextSize(20); // 设置字号为12号
            headCategory.setTextColor(Color.parseColor("#344CAA"));
            headCategory.setMinHeight(100);
            textlinearLayout.addView(headCategory);
            building_tableLayout.addView(textlinearLayout);
            for (JsonElement building : buildings) {
                JsonObject building_object = building.getAsJsonObject();
                String building_name = building_object.get("name").getAsString();
                String building_id = building_object.get("id").getAsString();

                LinearLayout elementslinearLayout = new LinearLayout(this);
                elementslinearLayout.setOrientation(LinearLayout.VERTICAL);
                elementslinearLayout.setLayoutParams(element_layoutParams);
                elementslinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EmptyBuildingsActivity.this, EmptyClassroomsActivity.class);

                        // 将 int 类型的数据添加到 Intent 中
                        intent.putExtra("building_id", building_id);

                        // 启动 EmptyClassroomsActivity，并传递Intent
                        startActivity(intent);
                    }
                });
                TextView BuildingColumn = new TextView(this);
                BuildingColumn.setText(building_name != null ? building_name : "");
                BuildingColumn.setTextSize(12); // 设置字号为12号
                BuildingColumn.setMinHeight(200);
                BuildingColumn.setLayoutParams(textLayoutParams);
//                BuildingColumn.setBackgroundColor(Color.GREEN);
                BuildingColumn.setGravity(Gravity.CENTER);

                // 创建一个新的 ImageView 对象
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(buildingImages[imageId % buildingImages.length]); // 替换成你想要显示的图片资源ID
                imageId++;
                // 设置 ImageView 的布局参数
                imageView.setLayoutParams(imageviewlayoutParams);
//                imageView.setBackgroundColor(Color.BLUE);

                // 将BuildingColumn 添加到当前行
                // 将 ImageView 添加到 LinearLayout 中
                elementslinearLayout.addView(imageView);
                elementslinearLayout.addView(BuildingColumn);
                currentlinearLayout.addView(elementslinearLayout);
                elementsInRow++;

                if (elementsInRow % 4 == 0) {
                    // 当前行已经有3个元素，将其添加到tableLayout中并创建新的行
                    building_tableLayout.addView(currentlinearLayout);
                    currentlinearLayout = new LinearLayout(this);
                    currentlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    currentlinearLayout.setBackgroundColor(Color.RED);
                }
            }
            // 检查最后一行是否少于3个元素,如果少于3个元素则需要单独处理
            if (elementsInRow % 4 != 0) {
                building_tableLayout.addView(currentlinearLayout);
                currentlinearLayout = new LinearLayout(this);
                currentlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            }
        }

    }
}
