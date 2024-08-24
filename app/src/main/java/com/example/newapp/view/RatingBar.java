package com.example.newapp.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RatingBar extends LinearLayout {

    private int maxStars = 5;
    private float rating = 0;

    public RatingBar(Context context) {
        super(context);
        init();
    }

    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        for (int i = 0; i < maxStars; i++) {
            ImageView star = new ImageView(getContext());
            star.setImageResource(android.R.drawable.btn_star_big_off);
            star.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            star.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRating(indexOfChild(v) + 1);
                }
            });
            addView(star);
        }
    }

    public void setRating(float rating) {
        this.rating = rating;
        for (int i = 0; i < getChildCount(); i++) {
            ImageView star = (ImageView) getChildAt(i);
            if (i < rating) {
                star.setImageResource(android.R.drawable.btn_star_big_on);
                star.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off);
                star.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public float getRating() {
        return rating;
    }
}
