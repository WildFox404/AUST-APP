package com.example.newapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RoundedColorTextView {

    private Set<Integer> colorSet = new HashSet<>();
    private Random random = new Random();

    public TextView createTextViewWithRoundedColorBackground(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(10);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextColor(Color.WHITE);

        // 设置圆角背景
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8); // 设置圆角半径
        shape.setColor(getRandomColor()); // 设置随机颜色
        textView.setBackground(shape);

        return textView;
    }

    private int getRandomColor() {
        int color;
        do {
            int red = random.nextInt(60) + 160;   // 160 到 255 之间的值
            int green = random.nextInt(60) + 160; // 160 到 255 之间的值
            int blue = random.nextInt(60) + 160;  // 160 到 255 之间的值
            color = Color.rgb(red, green, blue);
        } while (colorSet.contains(color));
        colorSet.add(color);
        return color;
    }
}