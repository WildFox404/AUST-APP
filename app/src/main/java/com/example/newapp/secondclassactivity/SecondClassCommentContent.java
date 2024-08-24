package com.example.newapp.secondclassactivity;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.secondclass.SecondClassSchoolFragment;
import com.example.newapp.secondclass.SecondClassTodoFragment;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SecondClassCommentContent extends AppCompatActivity {
    private long lastTime = System.currentTimeMillis()-120;
    private LayoutInflater inflater;
    private Integer pageNumComment = 1;
    private Integer pageNumLike = 1;
    private String id;
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private JsonObject CommentContent;
    private JsonObject CommentComment;
    private JsonObject CommentLike;
    private PopupWindow popupWindow;
    private TextView like_textview;
    private TextView comment_textview;
    private ScrollView scrollView;
    private LinearLayout layout_content;
    private Boolean comment_selected=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassschoolcommentcommentview);

        id = getIntent().getStringExtra("id");

        ImageView write_comment_button = findViewById(R.id.write_comment_button);
        write_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("评论的评论", "发起评论: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondClassCommentContent.this);

                builder.setTitle("输入评论");


                // 设置对话框视图
                final EditText input = new EditText(SecondClassCommentContent.this);
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
                builder.setPositiveButton("确认发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = input.getText().toString();
                        // 在这里处理用户输入的评论内容
                        new CommentAddAsyncTask(comment,id,"","").execute();
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

        inflater = getLayoutInflater();
        // 获取ScrollView
        scrollView = findViewById(R.id.commentcommentlayout);
        //内容layout
        layout_content = findViewById(R.id.layout_content);

        // 初始化弹出窗口
        popupWindow = new PopupWindow();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        like_textview =findViewById(R.id.like_textview);
        comment_textview =findViewById(R.id.comment_textview);

        like_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment_selected) {
                    write_comment_button.setVisibility(View.INVISIBLE);
                    comment_selected=false;
                    like_textview.setTextColor(getResources().getColor(R.color.bule, null));
                    comment_textview.setTextColor(getResources().getColor(R.color.black, null));
                    layout_content.removeAllViews();
                    pageNumLike=1;
                    new CommentLikeAsyncTask().execute();
                }
            }
        });

        comment_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment_selected) {
                    write_comment_button.setVisibility(View.VISIBLE);
                    comment_selected=true;
                    comment_textview.setTextColor(getResources().getColor(R.color.bule, null));
                    like_textview.setTextColor(getResources().getColor(R.color.black, null));
                    layout_content.removeAllViews();
                    pageNumComment=1;
                    new CommentCommentAsyncTask().execute();
                }
            }
        });

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new CommentContentAsyncTask().execute();
        new CommentCommentAsyncTask().execute();
    }

    private class CommentAddAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String content;
        private String id;
        private String parentId;
        private String replyCommentUserId;
        private CommentAddAsyncTask(String content,String id,String parentId,String replyCommentUserId){
            this.content = content;
            this.id = id;
            this.parentId = parentId;
            this.replyCommentUserId = replyCommentUserId;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.postAddCommentComment(content,id,parentId,replyCommentUserId);
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
                pageNumComment = 1;
                layout_content.removeAllViews();
                new CommentCommentAsyncTask().execute();
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
                return secondClassUser.postDeleteCommentComment(contentId);
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
                pageNumComment = 1;
                layout_content.removeAllViews();
                new CommentCommentAsyncTask().execute();
            }else{
                toastShow("删除失败");
            }
        }
    }

    private void toastShow(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    private class CommentContentAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getSchoolCommentContent(id);
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
                CommentContent = result;
                Log.d("校友圈评论的评论", "校友圈评论的评论获取成功");
                Log.d("校友圈评论的评论", result.toString());
                updateCommentContentUI();
            }
        }
    }

    private void updateCommentContentUI() {
        Log.d("校友圈评论的评论", "执行开始: ");
//        try {
            String comment_image_url = CommentContent.get("attachUrl") != null && !CommentContent.get("attachUrl").isJsonNull() ? CommentContent.get("attachUrl").getAsString() : "";
            String isLike = CommentContent.get("isLike").getAsString();
            String comment_time_result = CommentContent.get("ctime").getAsString();
            String activity_type_result = CommentContent.get("topicName").getAsString();
            String like_count = CommentContent.get("likeCount").getAsString();
            String comment_content_result = CommentContent.get("content").getAsString();
            String comment_count_result = CommentContent.get("commentCount").getAsString();
            String id = CommentContent.get("id").getAsString();
            JsonObject user_object =CommentContent.get("createUser").getAsJsonObject();
            String college_name = user_object.get("collegeName") != null && !user_object.get("collegeName").isJsonNull() ? user_object.get("collegeName").getAsString() : "";
            String organization_name = user_object.get("organizationName") != null && !user_object.get("organizationName").isJsonNull() ? user_object.get("organizationName").getAsString() : "";
            String name = user_object.get("name").getAsString();
            String avatar_url = user_object.get("avatar") != null && !user_object.get("avatar").isJsonNull() ? user_object.get("avatar").getAsString() : "";
            String major_name = user_object.get("majorName").getAsString();
            String user_id = user_object.get("id").getAsString();

            LinearLayout rootView =findViewById(R.id.root);
            LinearLayout like_button =findViewById(R.id.like_button);
            TextView user_name =findViewById(R.id.user_name);
            TextView user_depart =findViewById(R.id.user_depart);
            TextView comment_time =findViewById(R.id.comment_time);
            TextView comment_content =findViewById(R.id.comment_content);
            TextView activity_type =findViewById(R.id.activity_type);
            TextView comment_like =findViewById(R.id.comment_like);
            ImageView user_avatar =findViewById(R.id.user_avatar);
            ImageView comment_image1 =findViewById(R.id.comment_image1);
            ImageView comment_image2 =findViewById(R.id.comment_image2);
            ImageView comment_image3 =findViewById(R.id.comment_image3);
            ImageView comment_like_icon =findViewById(R.id.comment_like_icon);
            //是否点赞
            if(isLike.equals("1")){
                //点赞了
                comment_like_icon.setImageResource(R.drawable.likeicon2);
                comment_like.setTextColor(getResources().getColor(R.color.icon_blue));
                like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new LikeAsyncTask(id,"0",comment_like_icon,comment_like,like_count,like_button).execute();
                    }
                });
            }else{
                //未点赞
                like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new LikeAsyncTask(id,"1",comment_like_icon,comment_like,like_count,like_button).execute();
                    }
                });
            }
            //跳转用户信息
            user_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }
            });
            user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }
            });
            //用户名
            user_name.setText(name);
            //组织,学院,专业
            user_depart.setText(organization_name+"\t"+college_name+"\t"+major_name);
            //发帖时间
            comment_time.setText(DateUtils.convertTimestampToDate(comment_time_result));
            //评论内容
            comment_content.setText(comment_content_result);
            //活动类型
            activity_type.setText("#"+activity_type_result);
            //点赞数
            comment_like.setText(like_count);
            //用户头像
            if (!avatar_url.equals("")) {
                Picasso.get().load(avatar_url).into(user_avatar);
            }
            //评论图片
            if (!comment_image_url.equals("")){
                String[] urls = comment_image_url.split(",");
                for (int i = 0; i < urls.length; i++) {
                    String url = urls[i];
                    if (i == 0) {
                        // 设置第一个 URL 到第一个 ImageView
                        Picasso.get().load(url).into(comment_image1);
                        comment_image1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 设置弹出窗口的内容视图
                                View contentView = inflater.inflate(R.layout.popup_image, null);
                                popupWindow.setContentView(contentView);
                                // 在弹出窗口中显示图片
                                ImageView imageView = contentView.findViewById(R.id.image_view);
                                // 使用你的URL加载图片，例如使用Glide库
                                Picasso.get().load(url).into(imageView);

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
                    } else if (i == 1) {
                        // 设置第二个 URL 到第二个 ImageView
                        Picasso.get().load(url).into(comment_image2);
                        comment_image2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 设置弹出窗口的内容视图
                                View contentView = inflater.inflate(R.layout.popup_image, null);
                                popupWindow.setContentView(contentView);
                                // 在弹出窗口中显示图片
                                ImageView imageView = contentView.findViewById(R.id.image_view);
                                // 使用你的URL加载图片，例如使用Glide库
                                Picasso.get().load(url).into(imageView);

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
                    } else if (i == 2) {
                        // 设置第三个 URL 到第三个 ImageView
                        Picasso.get().load(url).into(comment_image3);
                        comment_image3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 设置弹出窗口的内容视图
                                View contentView = inflater.inflate(R.layout.popup_image, null);
                                popupWindow.setContentView(contentView);
                                // 在弹出窗口中显示图片
                                ImageView imageView = contentView.findViewById(R.id.image_view);
                                // 使用你的URL加载图片，例如使用Glide库
                                Picasso.get().load(url).into(imageView);

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
                    }
                }
            }
            Log.d("校友圈评论的评论", "创建完毕: "+name);
//        }catch (Exception e) {
//            // 处理ExceptionType1类型的异常
//            Log.w("校友圈评论的评论", "发送错误: "+e.toString());
//        }
        Log.d("校友圈评论的评论", "执行结束: ");
    }

    private class CommentLikeAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                Log.d("评论点赞", "异步开始: ");
                return secondClassUser.getSchoolCommentLike(pageNumLike,id);
            } catch (Exception e) {
                Log.e("评论点赞", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                CommentLike = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateCommentLikeUI();
            }
        }
    }

    private void updateCommentLikeUI() {
        Integer total = CommentLike.get("total").getAsInt();
        Integer pageSize = CommentLike.get("pageSize").getAsInt();
        JsonArray list_results = CommentLike.get("list").getAsJsonArray();

        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String collegeName = list_object.get("collegeName").getAsString();
                String organizationName = list_object.get("organizationName").getAsString();
                String name = list_object.get("name").getAsString();
                String ctime = list_object.get("ctime").getAsString();
                String id = list_object.get("id").getAsString();
                String avatar = list_object.get("avatar") != null && !list_object.get("avatar").isJsonNull() ? list_object.get("avatar").getAsString() : "";

                String majorName = list_object.get("majorName").getAsString();

                View comment_view =new View(this);
                comment_view = inflater.inflate(R.layout.secondclassschoolcommentlike, null);
                TextView user_name =comment_view.findViewById(R.id.user_name);
                TextView user_depart =comment_view.findViewById(R.id.user_depart);
                TextView comment_time =comment_view.findViewById(R.id.comment_time);
                ImageView user_avatar =comment_view.findViewById(R.id.user_avatar);
                //跳转用户信息
                user_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",id);
                        startActivity(intent);
                    }
                });
                user_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",id);
                        startActivity(intent);
                    }
                });
                //用户名
                user_name.setText(name);
                //组织,学院,专业
                user_depart.setText(organizationName+"\t"+collegeName+"\t"+majorName);
                //发帖时间
                comment_time.setText(DateUtils.convertTimestampToDate(ctime));
                //用户头像
                if (!avatar.equals("")) {
                    Picasso.get().load(avatar).into(user_avatar);
                }

                layout_content.addView(comment_view);
            }catch(Exception e){
                Log.w("二课评论点赞列表", "发送错误: "+e.toString());
            }
        }
        if(list_results.size()<pageSize){
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });
        }else {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int height = scrollView.getChildAt(0).getHeight();
                    int scrollViewHeight = scrollView.getHeight();
                    int diff = height - scrollViewHeight;

                    if (System.currentTimeMillis() - lastTime < 100)
                        return;
                    else {
                        lastTime = System.currentTimeMillis();
                        // rest of your code
                        if (scrollY >= diff) {
                            // ScrollView滑动到底部
                            // 执行相应的操作
                            pageNumLike++;
                            layout_content.removeAllViews();
                            Log.d("校友圈", "获取更多数据: ");
                            new CommentLikeAsyncTask().execute();
                        } else {
                            // ScrollView未滑动到底部
                        }
                    }
                }
            });
        }
    }

    private class CommentCommentAsyncTask extends AsyncTask<Void, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {

                return secondClassUser.getSchoolCommentComment(pageNumComment,id);
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
                CommentComment = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateCommentCommentUI();
            }
        }
    }

    private void updateCommentCommentUI() {
        Integer total = CommentComment.get("total").getAsInt();
        Integer pageSize = CommentComment.get("pageSize").getAsInt();
        JsonArray list_results = CommentComment.get("list").getAsJsonArray();

        for (JsonElement list_result : list_results) {
            try {
                JsonObject list_object = list_result.getAsJsonObject();
                String content = list_object.get("content").getAsString();
                String ctime = list_object.get("ctime").getAsString();
                String contentId = list_object.get("contentId").getAsString();
                String dynamicId = list_object.get("dynamicId").getAsString();
                JsonObject fromUser = list_object.get("fromUser").getAsJsonObject();
                String collegeName = fromUser.get("collegeName").getAsString();
                String organizationName = fromUser.get("organizationName").getAsString();
                String name = fromUser.get("name").getAsString();
                String user_id = fromUser.get("id").getAsString();
                String avatar = fromUser.get("avatar") != null && !fromUser.get("avatar").isJsonNull() ? fromUser.get("avatar").getAsString() : "";
                String majorName = fromUser.get("majorName").getAsString();


                View comment_view =new View(this);
                comment_view = inflater.inflate(R.layout.secondclassschoolcommentcomment, null);
                LinearLayout main_layout_child = comment_view.findViewById(R.id.main_layout_child);
                LinearLayout user_info_layout = comment_view.findViewById(R.id.user_info_layout);
                TextView user_name =comment_view.findViewById(R.id.user_name);
                TextView user_depart =comment_view.findViewById(R.id.user_depart);
                TextView comment_time =comment_view.findViewById(R.id.comment_time);
                TextView comment_content =comment_view.findViewById(R.id.comment_content);
                ImageView user_avatar =comment_view.findViewById(R.id.user_avatar);
                //删除评论
                if(user_id.equals(secondClassUser.getId())){
                    //是自己的评论,可以删除
                    ImageView delete_comment_button = comment_view.findViewById(R.id.delete_comment_button);
                    delete_comment_button.setImageResource(R.drawable.delete_icon1);
                    delete_comment_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("评论的评论", "发起评论: ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(SecondClassCommentContent.this);

                            builder.setTitle("确定要删除评论吗");


                            // 设置对话框视图
                            final TextView input = new TextView(SecondClassCommentContent.this);
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
                //设置回复信息
                LinearLayout reply_liearlayout = comment_view.findViewById(R.id.reply_liearlayout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                reply_liearlayout.setLayoutParams(params);
                TextView reply_title =comment_view.findViewById(R.id.reply_title);
                TextView reply_name =comment_view.findViewById(R.id.reply_name);
                if (list_object.has("relatedComment")) {
                    reply_title.setText("回复\t");
                    JsonObject relatedComment = list_object.get("relatedComment").getAsJsonObject();
                    JsonObject replyFromUser = relatedComment.get("fromUser").getAsJsonObject();
                    String replyName = replyFromUser.get("name").getAsString();
                    reply_name.setText(replyName);
                    String replyId = replyFromUser.get("id").getAsString();
                    reply_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                            intent.putExtra("user_id",replyId);
                            startActivity(intent);
                        }
                    });
                }
                //跳转用户信息
                user_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
                user_info_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondClassCommentContent.this, SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
                //用户名
                user_name.setText(name);
                //组织,学院,专业
                user_depart.setText(organizationName+"\t"+collegeName+"\t"+majorName);
                //发帖时间
                comment_time.setText(DateUtils.convertTimestampToDate(ctime));
                //评论内容
                comment_content.setText(content);
                //回复评论
                main_layout_child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("评论的评论", "发起评论: ");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondClassCommentContent.this);

                        builder.setTitle("回复"+name);


                        // 设置对话框视图
                        final EditText input = new EditText(SecondClassCommentContent.this);
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
                        builder.setPositiveButton("确认发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String comment = input.getText().toString();
                                // 在这里处理用户输入的评论内容
                                new CommentAddAsyncTask(comment,dynamicId,contentId,user_id).execute();
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
                //用户头像
                if (!avatar.equals("")) {
                    Picasso.get().load(avatar).into(user_avatar);
                }

                layout_content.addView(comment_view);
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }
        if(total<pageSize){
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });
        }else {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int height = scrollView.getChildAt(0).getHeight();
                    int scrollViewHeight = scrollView.getHeight();
                    int diff = height - scrollViewHeight;

                    if (System.currentTimeMillis() - lastTime < 100)
                        return;
                    else {
                        lastTime = System.currentTimeMillis();
                        // rest of your code
                        if (scrollY >= diff) {
                            // ScrollView滑动到底部
                            // 执行相应的操作
                            pageNumComment++;
                            Log.d("校友圈", "获取更多数据: ");
                            new CommentCommentAsyncTask().execute();
                        } else {
                            // ScrollView未滑动到底部
                        }
                    }
                }
            });
        }
    }

    private class LikeAsyncTask extends AsyncTask<Void, Void, String> {
        private String id;
        private String islike;
        private ImageView comment_like_icon;
        private TextView comment_like;
        private String like_count;
        private LinearLayout like_button;

        private LikeAsyncTask(String id, String islike, ImageView comment_like_icon, TextView comment_like, String like_count, LinearLayout like_button) {
            this.id = id;
            this.islike = islike;
            this.comment_like_icon = comment_like_icon;
            this.comment_like = comment_like;
            this.like_count = like_count;
            this.like_button = like_button;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                return secondClassUser.schoolLikeContent(id, islike);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                if (result.equals(islike)) {
                    Log.d("二课点赞", "请求成功: " + result);
                    //请求成功
                    if (result.equals("1")) {
                        //点赞成功
                        comment_like_icon.setImageResource(R.drawable.likeicon2);
                        comment_like.setText(String.valueOf(Integer.parseInt(like_count) + 1));
                        comment_like.setTextColor(getResources().getColor(R.color.icon_blue));
                        like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new LikeAsyncTask(id, "0", comment_like_icon, comment_like, String.valueOf(Integer.parseInt(like_count) + 1), like_button).execute();
                            }
                        });
                    } else {
                        //取消点赞成功
                        comment_like_icon.setImageResource(R.drawable.likeicon1);
                        comment_like.setText(String.valueOf(Integer.parseInt(like_count) - 1));
                        comment_like.setTextColor(getResources().getColor(R.color.black));
                        like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new LikeAsyncTask(id, "1", comment_like_icon, comment_like, String.valueOf(Integer.parseInt(like_count) - 1), like_button).execute();
                            }
                        });
                    }
                    if (!comment_selected) {
                        layout_content.removeAllViews();
                        pageNumLike=1;
                        new CommentLikeAsyncTask().execute();
                    }
                } else {
                    //请求失败
                }
            }
        }
    }
}
