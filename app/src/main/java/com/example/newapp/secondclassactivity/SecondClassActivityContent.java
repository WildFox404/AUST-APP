package com.example.newapp.secondclassactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.utils.DateUtils;
import com.example.newapp.view.RatingBar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SecondClassActivityContent extends AppCompatActivity {
    private long lastTime = System.currentTimeMillis()-120;
    private JsonObject activity_content_jsonobject;
    private JsonObject activity_comment_jsonobject;
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private Integer pageNum = 1;
    private Integer selected = 1;
    private LinearLayout layout_content;
    private String id;
    private String[] array = {"1", "0", "0", "0", "0"};
    private String comment_selected = "";
    private Boolean commentUIInit=false;
    private ArrayList<TextView> textview_list= new ArrayList<>();
    private LinearLayout comment_layout;
    private PopupWindow popupWindow;
    private LinearLayout rootView;
    private ImageView write_comment_button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassactivitycontent);

        id = getIntent().getStringExtra("id");

        // 初始化弹出窗口
        popupWindow = new PopupWindow();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        layout_content = findViewById(R.id.layout_content);
        rootView =findViewById(R.id.root);

        comment_layout = new LinearLayout(this);
        comment_layout.setOrientation(LinearLayout.VERTICAL);

        write_comment_button = findViewById(R.id.write_comment_button);
        write_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("评论的评论", "发起评论: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondClassActivityContent.this);

                builder.setTitle("输入评论");

                // 设置对话框视图
                final LinearLayout linearLayout = new LinearLayout(SecondClassActivityContent.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //星星图
                final RatingBar ratingBar = new RatingBar(SecondClassActivityContent.this);
                ratingBar.setRating(4.5f); // 设置评分为4.5
                linearLayout.addView(ratingBar);
                //选项
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                        LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
                );
                layoutParams.setMargins(5, 0, 5, 5); // 设置左、上、右、下的 margin 值
                LinearLayout select_linearLayout = new LinearLayout(SecondClassActivityContent.this);
                select_linearLayout.setLayoutParams(layoutParams);
                select_linearLayout.setOrientation(LinearLayout.HORIZONTAL); // 设置排列方式为水平

                SelectedlinearLayoutAddTextView(select_linearLayout,"1","整体评价");
                SelectedlinearLayoutAddTextView(select_linearLayout,"2","组织规划");
                SelectedlinearLayoutAddTextView(select_linearLayout,"3","活动气氛");
                linearLayout.addView(select_linearLayout);

                select_linearLayout = new LinearLayout(SecondClassActivityContent.this);
                select_linearLayout.setLayoutParams(layoutParams);
                select_linearLayout.setOrientation(LinearLayout.HORIZONTAL); // 设置排列方式为水平

                SelectedlinearLayoutAddTextView(select_linearLayout,"4","内容质量");
                SelectedlinearLayoutAddTextView(select_linearLayout,"5","实用性");
                SelectedlinearLayoutAddTextView(select_linearLayout,"","");
                linearLayout.addView(select_linearLayout);
                //输入框
                final EditText input = new EditText(SecondClassActivityContent.this);
                // 创建布局参数对象，并设置margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(40, 20, 40, 20); // left, top, right, bottom
                // 将布局参数应用到 EditText
                input.setLayoutParams(params);
                linearLayout.addView(input);

                builder.setView(linearLayout);

                // 设置确认按钮操作
                builder.setPositiveButton("确认发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String appraiseTag = array[0];
                        // 遍历数组并修改元素
                        for (int i = 1; i < array.length; i++) {
                            if(array[i].equals("0")){
                                //未选择
                            }else {
                                appraiseTag+=","+array[i];
                            }
                        }
                        if(appraiseTag.equals("0")){
                            //啥也没选
                            toastShow("必须选一个选项");
                        }else {
                            String comment = input.getText().toString();
                            // 在这里处理用户输入的评论内容
                            new CommentAddAsyncTask(comment,id,String.valueOf(ratingBar.getRating())).execute();
                        }

                    }
                });

                // 设置取消按钮操作
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 创建自定义的背景Drawable
                Drawable drawable = getResources().getDrawable(R.drawable.selectbutton5);
                // 创建并显示对话框
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(drawable);
                dialog.show();
            }
        });

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView info_textview = findViewById(R.id.info_textview);
        TextView introduction_textview = findViewById(R.id.introduction_textview);
        TextView comment_textview = findViewById(R.id.comment_textview);
        info_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.equals(1)){
                    //已经选择了,啥也不做
                }else{
                    //没有选择,执行代码
                    write_comment_button.setVisibility(View.INVISIBLE);
                    commentUIInit=false;
                    selected=1;
                    info_textview.setTextColor(getResources().getColor(R.color.icon_blue));
                    introduction_textview.setTextColor(getResources().getColor(R.color.black));
                    comment_textview.setTextColor(getResources().getColor(R.color.black));
                    layout_content.removeAllViews();
                    updateInfoUI();
                }
            }
        });
        introduction_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.equals(2)){
                    //已经选择了,啥也不做
                }else{
                    //没有选择,执行代码
                    write_comment_button.setVisibility(View.INVISIBLE);
                    commentUIInit=false;
                    selected=2;
                    introduction_textview.setTextColor(getResources().getColor(R.color.icon_blue));
                    info_textview.setTextColor(getResources().getColor(R.color.black));
                    comment_textview.setTextColor(getResources().getColor(R.color.black));
                    layout_content.removeAllViews();
                    updateIntroductionUI();
                }
            }
        });
        comment_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.equals(3)){
                    //已经选择了,啥也不做
                }else{
                    //没有选择,执行代码
                    write_comment_button.setVisibility(View.VISIBLE);
                    pageNum=1;
                    selected=3;
                    introduction_textview.setTextColor(getResources().getColor(R.color.black));
                    info_textview.setTextColor(getResources().getColor(R.color.black));
                    comment_textview.setTextColor(getResources().getColor(R.color.icon_blue));
                    layout_content.removeAllViews();
                    comment_layout.removeAllViews();
                    new ActivityContentCommentAsyncTask().execute();
                }
            }
        });

        new ActivityContentAsyncTask(id).execute();
    }
    private class CommentAddAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String content;
        private String parentId;
        private String starScore;
        private String appraiseTag;
        private CommentAddAsyncTask(String content,String parentId,String starScore){
            this.content = content;
            this.parentId = parentId;
            this.starScore=starScore;
            String appraiseTag = array[0];
            // 遍历数组并修改元素
            for (int i = 1; i < array.length; i++) {
                if(array[i].equals("0")){
                    //未选择
                }else {
                    appraiseTag+=","+array[i];
                }
            }
            this.appraiseTag = appraiseTag;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.postAddActivityComment(content,id,parentId,starScore,appraiseTag);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Assuming grade_result is a field in your class to store the JSON data
                toastShow("发送成功");
                pageNum = 1;
                comment_layout.removeAllViews();
                new ActivityContentCommentAsyncTask().execute();
            }else{
                toastShow("发送失败");
            }
        }
    }

    private class CommentDeleteAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String contentId;
        private CommentDeleteAsyncTask(String contentId){
            this.contentId = contentId;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.postDeleteActivityComment(contentId);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Assuming grade_result is a field in your class to store the JSON data
                toastShow("删除成功");
                pageNum = 1;
                comment_layout.removeAllViews();
                new ActivityContentCommentAsyncTask().execute();
            }else{
                toastShow("删除失败");
            }
        }
    }

    private class ActivityContentAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        private String id;

        private ActivityContentAsyncTask(String id){
            this.id=id;
        }
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return secondClassUser.getActivityContent(id);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                activity_content_jsonobject = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateUI();
                updateInfoUI();
            }
        }
    }

    private class ActivityContentCommentAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                return secondClassUser.getActivityContentComment(pageNum,id,comment_selected);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                activity_comment_jsonobject = result;
                Log.d("考试安排获取", "考试安排获取成功");
                if(commentUIInit){
                    //初始化完成了
                    updateCommentContentUI();
                }else{
                    updateCommentUI();
                }
            }
        }
    }

    private void updateUI() {
        TextView activity_name = findViewById(R.id.activity_name);
        TextView hours = findViewById(R.id.hours);
        TextView activity_type = findViewById(R.id.activity_type);
        TextView status = findViewById(R.id.status);
        TextView organizationName = findViewById(R.id.organizationName);
        TextView enroll = findViewById(R.id.enroll);
        TextView enroll_count = findViewById(R.id.enroll_count);
        TextView enroll_time = findViewById(R.id.enroll_time);
        TextView activity_time = findViewById(R.id.activity_time);
        TextView address = findViewById(R.id.address);
        TextView enroll_textview = findViewById(R.id.enroll_textview);
        ImageView image = findViewById(R.id.image);

        String name = activity_content_jsonobject.get("name").getAsString();
        String classifyName = activity_content_jsonobject.get("classifyName").getAsString();
        String organizationName_result = activity_content_jsonobject.get("organizationName").getAsString();
        String grantHours = activity_content_jsonobject.get("grantHours").getAsString();
        String hours_result = activity_content_jsonobject.get("hours").getAsString();
        String enrollEndTime = activity_content_jsonobject.get("enrollEndTime").getAsString();
        String enrollStartTime = activity_content_jsonobject.get("enrollStartTime").getAsString();
        String endTime = activity_content_jsonobject.get("endTime").getAsString();
        String startTime = activity_content_jsonobject.get("startTime").getAsString();
        String address_result = activity_content_jsonobject.get("address").getAsString();
        String logo = activity_content_jsonobject.get("logo").getAsString();
        String peopleLimit = activity_content_jsonobject.get("peopleLimit").getAsString();
        String joinMemberCount = activity_content_jsonobject.get("joinMemberCount").getAsString();

        activity_name.setText(name);
        hours.setText(hours_result+"学时");
        activity_type.setText(classifyName);
        organizationName.setText(organizationName_result);
        enroll_time.setText(DateUtils.convertTimestampToDate(enrollStartTime)+"-"+DateUtils.convertTimestampToDate(enrollEndTime));
        activity_time.setText(DateUtils.convertTimestampToDate(startTime)+"-"+DateUtils.convertTimestampToDate(endTime));
        address.setText(address_result);
        enroll_count.setText(joinMemberCount+"/"+peopleLimit);
        //是否可报名
        enroll_textview.setBackground(getDrawable(R.drawable.button_background1));
        enroll_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShow("功能没做呢,加群催更");
            }
        });
        if(DateUtils.isPastDeadline(endTime)) {
            enroll.setText("已结束");
            enroll_textview.setText("活动结束了");
            enroll.setTextColor(getResources().getColor(R.color.fire_red));
        }else if((Integer.parseInt(peopleLimit) > Integer.parseInt(joinMemberCount)) && DateUtils.isCurrentTimeInRange(enrollStartTime, enrollEndTime)){
            enroll.setText("可报名");
            enroll.setTextColor(getResources().getColor(R.color.green));
            enroll_textview.setText("报名");
            enroll_textview.setBackground(getDrawable(R.drawable.button_background));
        }else if(DateUtils.isCurrentTimeInRange(startTime, endTime)){
            enroll.setText("进行中");
            enroll_textview.setText("活动进行中");
            enroll.setTextColor(getResources().getColor(R.color.icon_blue));
        }else {
            enroll.setText("已满员");
            enroll_textview.setText("已满员");
            enroll.setTextColor(getResources().getColor(R.color.yellow));
        }
        //判断是否结束
        if(DateUtils.isPastDeadline(endTime)){
            //已经结束
            status.setText("已完结:"+grantHours+"学时");
            status.setTextColor(getResources().getColor(R.color.yellow));
        }else{
            //未结束
            status.setText("进行中");
            status.setTextColor(getResources().getColor(R.color.green));
        }

        //活动图像
        Picasso.get().load(logo).into(image);
    }


    private void updateInfoUI() {
        //附件
        JsonArray attachment = activity_content_jsonobject.get("attachment").getAsJsonArray();
        //管理员电话
        String contact = activity_content_jsonobject.get("contact").getAsString();
        //不知道是啥
        String courseName = activity_content_jsonobject.get("courseName").getAsString();
        //是否提交作业 0->不需要 1->需要
        String isPostHomework = activity_content_jsonobject.get("isPostHomework").getAsString();
        //承办单位
        JsonArray collegeList = activity_content_jsonobject.get("collegeList").getAsJsonArray();
        StringBuilder collegeConcatenated = new StringBuilder();
        for (JsonElement collegeResult : collegeList) {
            try {
                JsonObject collegeObject = collegeResult.getAsJsonObject();
                String name = collegeObject.get("name").getAsString();
                collegeConcatenated.append(name).append(" ");
            }catch (Exception e){
                Log.d("活动详情", "发生错误: "+e.getMessage());
            }
        }
        String collegeConcatenatedNames = collegeConcatenated.toString();
        //报名限制班级单位
        JsonArray organizationList = activity_content_jsonobject.get("organizationList").getAsJsonArray();
        StringBuilder organizationConcatenated = new StringBuilder();
        for (JsonElement organizationResult : organizationList) {
            try {
                JsonObject organizationObject = organizationResult.getAsJsonObject();
                String name = organizationObject.get("name").getAsString();
                organizationConcatenated.append(name).append(" ");
            }catch (Exception e){
                Log.d("活动详情", "发生错误: "+e.getMessage());
            }
        }
        String organizationConcatenatedNames = organizationConcatenated.toString();
        //管理员名称
        JsonObject manager = activity_content_jsonobject.get("manager").getAsJsonObject();
        String name = manager.get("name").getAsString();
        //诚信值限制 honestLimit以上才能参加
        String honestLimit = manager.get("honestLimit") != null && !manager.get("honestLimit").isJsonNull() ? manager.get("honestLimit").getAsString() : "";
        //活动类型
        String classifyName = activity_content_jsonobject.get("classifyName").getAsString();

        layoutAddTextView(collegeConcatenatedNames,"承办单位");
        layoutAddTextView(classifyName,"活动类型");
        layoutAddTextView("暂无","指导老师");
        layoutAddTextView(name+contact,"活动管理员");
        if(honestLimit.equals("")){
            layoutAddTextView(organizationConcatenatedNames,"报名限制");
        }else{
            layoutAddTextView("诚信值"+honestLimit+contact+"以上\t"+organizationConcatenatedNames,"报名限制");
        }
        if(isPostHomework.equals("0")){
            layoutAddTextView("否","是否提交作业");
        }else{
            layoutAddTextView("是","是否提交作业");
        }
        layoutAddTextView("","附件");

        LinearLayout image_linearlayout = new LinearLayout(this);
        image_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater inflater = getLayoutInflater();
        //附件设置
        for (JsonElement attachmentResult : attachment) {
            try {
                ImageView imageView = new ImageView(this);
                imageView.setMaxHeight(60);
                imageView.setMaxWidth(40);
                JsonObject attachmentObject = attachmentResult.getAsJsonObject();
                String image_url = attachmentObject.get("url").getAsString();
                // 设置第一个 URL 到第一个 ImageView
                Picasso.get().load(image_url).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 设置弹出窗口的内容视图
                        View contentView = inflater.inflate(R.layout.popup_image, null);
                        popupWindow.setContentView(contentView);
                        // 在弹出窗口中显示图片
                        ImageView imageView = contentView.findViewById(R.id.image_view);
                        // 使用你的URL加载图片，例如使用Glide库
                        Picasso.get().load(image_url).into(imageView);
                        //点击退出
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 关闭弹出窗口
                                popupWindow.dismiss();
                            }
                        });
                        ConstraintLayout root =contentView.findViewById(R.id.root);
                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 关闭弹出窗口
                                popupWindow.dismiss();
                            }
                        });
                        // 显示弹出窗口
                        popupWindow.showAtLocation(rootView , Gravity.CENTER, 0, 0);
                    }
                });
                image_linearlayout.addView(imageView);
            }catch (Exception e){

            }
        }
        layout_content.addView(image_linearlayout);
    }

    private void layoutAddTextView(String content,String title) {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );
        linearLayout.setLayoutParams(layoutParams);
        layoutParams.setMargins(5, 0, 5, 5); // 设置左、上、右、下的 margin 值
        linearLayout.setOrientation(LinearLayout.HORIZONTAL); // 设置排列方式为水平

        LinearLayout.LayoutParams textViewTitleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // 设置宽度为match_parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );

        TextView contentTextView = new TextView(this);
        contentTextView.setLayoutParams(layoutParams);
        contentTextView.setText(content);
        contentTextView.setGravity(Gravity.RIGHT);
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(textViewTitleLayoutParams);
        titleTextView.setText(title);
        titleTextView.setTextColor(getResources().getColor(R.color.grey));
        linearLayout.addView(titleTextView);
        linearLayout.addView(contentTextView);
        layout_content.addView(linearLayout);
    }

    private void updateIntroductionUI() {
        String introduce = activity_content_jsonobject.get("introduce").getAsString();

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                ViewGroup.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );
        layoutParams.setMargins(10, 0, 10, 10); // 设置左、上、右、下的 margin 值

        TextView textView = new TextView(this);
        textView.setText(introduce);
        textView.setTextSize(20);

        textView.setLayoutParams(layoutParams);
        layout_content.addView(textView);
    }

    private void updateCommentUI() {
        commentUIInit=true;
        textview_list.clear();

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setRating(4.5f); // 设置评分为4.5
        layout_content.addView(ratingBar); // 将RatingBar添加到父布局中显示

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );
        layoutParams.setMargins(5, 0, 5, 5); // 设置左、上、右、下的 margin 值

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL); // 设置排列方式为水平

        LinearLayoutAddTextView(linearLayout,"1","整体评价");
        LinearLayoutAddTextView(linearLayout,"2","组织规划");
        LinearLayoutAddTextView(linearLayout,"3","活动气氛");
        layout_content.addView(linearLayout);

        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL); // 设置排列方式为水平

        LinearLayoutAddTextView(linearLayout,"4","内容质量");
        LinearLayoutAddTextView(linearLayout,"5","实用性");
        LinearLayoutAddTextView(linearLayout,"","");
        layout_content.addView(linearLayout);
        layout_content.addView(comment_layout);
        updateCommentContentUI();
    }

    private void LinearLayoutAddTextView(LinearLayout linearLayout,String selected,String content_selected){
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );
        textViewLayoutParams.weight = 1;
        textViewLayoutParams.setMargins(0,3,0,3);
        TextView contentTextView = new TextView(this);

        contentTextView.setGravity(Gravity.CENTER);
        contentTextView.setLayoutParams(textViewLayoutParams);
        contentTextView.setText(content_selected);
        if(!content_selected.equals("")){
            contentTextView.setBackground(getDrawable(R.drawable.selectbutton3));
            contentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!comment_selected.equals(selected)){
                        comment_selected=selected;
                        comment_layout.removeAllViews();
                        pageNum=1;
                        textview_list_select(contentTextView);
                        new ActivityContentCommentAsyncTask().execute();
                    }
                }
            });
        }
        textview_list.add(contentTextView);
        linearLayout.addView(contentTextView);
    }

    private void SelectedlinearLayoutAddTextView(LinearLayout linearLayout,String selected,String content_selected){
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 设置宽度为match_parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // 设置高度为wrap_content
        );
        textViewLayoutParams.weight = 1;
        textViewLayoutParams.setMargins(0,3,0,3);
        TextView contentTextView = new TextView(this);

        contentTextView.setGravity(Gravity.CENTER);
        contentTextView.setLayoutParams(textViewLayoutParams);
        contentTextView.setText(content_selected);
        Integer select;
        if(!selected.equals("")){
            select = Integer.parseInt(selected);
        }else {
            select = 0;
        }

        if(!content_selected.equals("")){
            contentTextView.setBackground(getDrawable(R.drawable.selectbutton3));
            if(!select.equals(0)){
                contentTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(array[select-1].equals("0")){
                            //未被选择,即将选择
                            array[select-1]=String.valueOf(select);
                            contentTextView.setTextColor(getResources().getColor(R.color.icon_blue));
                        }else{
                            //已经选择了,即将取消
                            array[select-1]="0";
                            contentTextView.setTextColor(getResources().getColor(R.color.grey));
                        }
                    }
                });
            }
        }
        if(!select.equals(0)) {
            if (array[select - 1].equals("0")) {
                contentTextView.setTextColor(getResources().getColor(R.color.grey));
            } else {
                contentTextView.setTextColor(getResources().getColor(R.color.icon_blue));
            }
        }
        textview_list.add(contentTextView);
        linearLayout.addView(contentTextView);
    }
    private void textview_list_select(TextView selectTextView){
        for(TextView textView:textview_list){
            if(textView.equals(selectTextView)){
                textView.setTextColor(getResources().getColor(R.color.icon_blue));
            }else{
                textView.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
    private void updateCommentContentUI() {
        Log.d("活动评论内容", "updateCommentContentUI启动 ");
        Integer total = activity_comment_jsonobject.get("total").getAsInt();
        Integer pageSize = activity_comment_jsonobject.get("pageSize").getAsInt();
        JsonArray list_results = activity_comment_jsonobject.get("list").getAsJsonArray();

        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();
        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String userAvatar = list_object.get("userAvatar") != null && !list_object.get("userAvatar").isJsonNull() ? list_object.get("userAvatar").getAsString() : "";
                String ctime = list_object.get("ctime").getAsString();
                String userName = list_object.get("userName").getAsString();
                String starScore = list_object.get("starScore").getAsString();
                String userId = list_object.get("userId").getAsString();
                String content = list_object.get("content").getAsString();
                String contentId = list_object.get("contentId").getAsString();

                View comment_view =new View(this);
                comment_view = inflater.inflate(R.layout.secondclassschoolcommentcomment, null);
                TextView user_name =comment_view.findViewById(R.id.user_name);
                TextView user_depart =comment_view.findViewById(R.id.user_depart);
                TextView comment_time =comment_view.findViewById(R.id.comment_time);
                TextView comment_content =comment_view.findViewById(R.id.comment_content);
                ImageView user_avatar =comment_view.findViewById(R.id.user_avatar);
                ImageView delete_comment_button =comment_view.findViewById(R.id.delete_comment_button);
                //删除评论
                if(userId.equals(secondClassUser.getId())){
                    //说明是自己的评论
                    delete_comment_button.setImageDrawable(getResources().getDrawable(R.drawable.delete_icon1));
                    delete_comment_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("评论的评论", "发起评论: ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(SecondClassActivityContent.this);

                            builder.setTitle("确定要删除评论吗");


                            // 设置对话框视图
                            final TextView input = new TextView(SecondClassActivityContent.this);
                            input.setText("内容:\t"+content);
                            // 创建布局参数对象，并设置margin
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(40, 20, 40, 20); // left, top, right, bottom
                            // 将布局参数应用到 EditText
                            input.setLayoutParams(params);

                            builder.setView(input);

                            // 设置确认按钮操作
                            builder.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 在这里处理用户输入的评论内容
                                    new CommentDeleteAsyncTask(contentId).execute();
                                    // 例如，可以将评论内容显示在一个 TextView 中
                                }
                            });

                            // 设置取消按钮操作
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // 创建自定义的背景Drawable
                            Drawable drawable = getResources().getDrawable(R.drawable.selectbutton5);
                            // 创建并显示对话框
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(drawable);
                            dialog.show();
                        }
                    });
                }
                //跳转用户信息
                user_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassActivityContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",userId);
                        startActivity(intent);
                    }
                });
                user_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassActivityContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",userId);
                        startActivity(intent);
                    }
                });
                //用户名
                user_name.setText(userName);
                //组织,学院,专业
                user_depart.setText("评分:"+starScore);
                //发帖时间
                comment_time.setText(DateUtils.convertTimestampToDate(ctime));
                //评论内容
                comment_content.setText(content);
                //用户头像
                if (!userAvatar.equals("")) {
                    Picasso.get().load(userAvatar).into(user_avatar);
                }

                comment_layout.addView(comment_view);
            }catch (Exception e){

            }
        }
        ScrollView scrollView = findViewById(R.id.scrollview);
        Log.d("活动评论", "list_results.size(): "+String.valueOf(list_results.size()));
        Log.d("活动评论", "pageSize: "+String.valueOf(pageSize));
        if(list_results.size()<pageSize){
            Log.d("活动评论", "评论到底了: ");
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                }
            });
        }else {
            Log.d("活动评论", "还有评论要加载: ");
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int height = scrollView.getChildAt(0).getHeight();
                    int scrollViewHeight = scrollView.getHeight();
                    int diff = height - scrollViewHeight;
                    Log.d("活动评论", "判断加载时间: ");
                    if (System.currentTimeMillis() - lastTime < 100){
                        Log.d("活动评论", "时间条件不合格: ");
                        return;
                    } else {
                        Log.d("活动评论", "判断是否到底部: ");
                        lastTime = System.currentTimeMillis();
                        // rest of your code
                        if (scrollY+30 >= diff) {
                            // ScrollView滑动到底部
                            // 执行相应的操作
                            pageNum++;
                            Log.d("活动评论", "获取更多数据: ");
                            new ActivityContentCommentAsyncTask().execute();
                        } else {
                            // ScrollView未滑动到底部
                        }
                    }
                }
            });
        }
    }

    private void toastShow(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
