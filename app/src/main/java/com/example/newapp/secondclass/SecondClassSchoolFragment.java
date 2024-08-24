package com.example.newapp.secondclass;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.secondclassactivity.SecondClassActivity;
import com.example.newapp.secondclassactivity.SecondClassCommentContent;
import com.example.newapp.secondclassactivity.SecondClassUserInfoActivity;
import com.example.newapp.test.TestViewerActivity;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class SecondClassSchoolFragment extends Fragment {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private View rootView ;
    private JsonArray comment_list;
    private PopupWindow popupWindow;
    private Integer pageNum = 1;
    private long lastTime = System.currentTimeMillis()-120;
    private String topicId="231";
    private LinearLayout comment_content_linearlayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView  = inflater.inflate(R.layout.secondclassschoolview, container, false);
        comment_content_linearlayout = rootView.findViewById(R.id.school_comment);

        // 初始化弹出窗口
        popupWindow = new PopupWindow();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView write_comment_button = rootView.findViewById(R.id.write_comment_button);
        write_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("评论的评论", "发起评论: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("输入评论");


                // 设置对话框视图
                final EditText input = new EditText(getContext());
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
                        new CommentCreateAsyncTask(comment).execute();
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

        new SchoolCommentAsyncTask().execute();
        return rootView ;
    }
    private class CommentCreateAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String content;
        private CommentCreateAsyncTask(String content){
            this.content = content;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.postCreateComment(content,topicId);
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
                comment_content_linearlayout.removeAllViews();
                new SchoolCommentAsyncTask().execute();
            }else{
                toastShow("发送失败");
            }
        }
    }

    private class CommentDeleteAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String content;
        private CommentDeleteAsyncTask(String content){
            this.content = content;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return secondClassUser.postDeleteComment(content);
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
                comment_content_linearlayout.removeAllViews();
                new SchoolCommentAsyncTask().execute();
            }else{
                toastShow("删除失败");
            }
        }
    }

    private void toastShow(String content){
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    private class SchoolCommentAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return secondClassUser.getSchoolComment(pageNum);
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            JsonArray emptyArray = new JsonArray();
            return emptyArray;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                comment_list = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateUI();
            }
        }
    }

    private void updateUI() {

        // 在Fragment中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();
        for (JsonElement comment_result : comment_list) {
            try {
                JsonObject comment_object = comment_result.getAsJsonObject();
                String comment_image_url = comment_object.get("attachUrl").getAsString();
                String isLike = comment_object.get("isLike").getAsString();
                String comment_time_result = comment_object.get("ctime").getAsString();
                String activity_type_result = comment_object.get("topicName").getAsString();
                String like_count = comment_object.get("likeCount").getAsString();
                String comment_content_result = comment_object.get("content").getAsString();
                String comment_count_result = comment_object.get("commentCount").getAsString();
                String id = comment_object.get("id").getAsString();
                JsonObject user_object =comment_object.get("createUser").getAsJsonObject();
                String college_name = user_object.get("collegeName").getAsString();
                String organization_name = user_object.get("organizationName").getAsString();
                String name = user_object.get("name").getAsString();
                String avatar_url = user_object.get("avatar").getAsString();
                String major_name = user_object.get("majorName").getAsString();
                String user_id = user_object.get("id").getAsString();

                View comment_view =new View(getContext());
                comment_view = inflater.inflate(R.layout.secondclassschoolcomment, null);
                LinearLayout header_layout =comment_view.findViewById(R.id.header_layout);
                LinearLayout content_layout =comment_view.findViewById(R.id.content_layout);
                LinearLayout like_button =comment_view.findViewById(R.id.like_button);
                LinearLayout comment_button =comment_view.findViewById(R.id.comment_button);
                TextView user_name =comment_view.findViewById(R.id.user_name);
                TextView user_depart =comment_view.findViewById(R.id.user_depart);
                TextView comment_time =comment_view.findViewById(R.id.comment_time);
                TextView comment_content =comment_view.findViewById(R.id.comment_content);
                TextView activity_type =comment_view.findViewById(R.id.activity_type);
                TextView comment_like =comment_view.findViewById(R.id.comment_like);
                TextView comment_count =comment_view.findViewById(R.id.comment_count);
                ImageView user_avatar =comment_view.findViewById(R.id.user_avatar);
                ImageView comment_image1 =comment_view.findViewById(R.id.comment_image1);
                ImageView comment_image2 =comment_view.findViewById(R.id.comment_image2);
                ImageView comment_image3 =comment_view.findViewById(R.id.comment_image3);
                ImageView comment_count_icon =comment_view.findViewById(R.id.comment_count_icon);
                ImageView comment_like_icon =comment_view.findViewById(R.id.comment_like_icon);


                //删除评论
                if(user_id.equals(secondClassUser.getId())){
                    //是我自己的评论
                    ImageView delete_comment_button = comment_view.findViewById(R.id.delete_comment_button);
                    delete_comment_button.setImageResource(R.drawable.delete_icon1);
                    delete_comment_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("评论的评论", "发起评论: ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("确定要删除评论吗");


                            // 设置对话框视图
                            final TextView input = new TextView(getContext());
                            input.setText("内容:\t"+comment_content_result);
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
                                    new CommentDeleteAsyncTask(id).execute();
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
                        Intent intent = new Intent(getActivity(), SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
                user_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassUserInfoActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
                //跳转评论的评论区
                header_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassCommentContent.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                });
                comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassCommentContent.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                });
                content_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassCommentContent.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                });
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
                //用户名
                user_name.setText(name);
                //组织,学院,专业
                user_depart.setText(organization_name+"\t"+college_name+"\t"+major_name);
                //发帖时间
                comment_time.setText(DateUtils.convertTimestampToDate(comment_time_result));
                //评论内容
                comment_content.setText(comment_content_result);
                comment_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassCommentContent.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                });
                //活动类型
                activity_type.setText("#"+activity_type_result);
                //点赞数
                comment_like.setText(like_count);
                //评论数
                comment_count.setText(comment_count_result);
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
                comment_content_linearlayout.addView(comment_view);
                Log.d("校友圈", "创建完毕: "+name);


            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            } finally {
                // 可选的finally块，无论是否发生异常都会执行
                continue;
            }
        }
        // 获取ScrollView
        ScrollView scrollView = rootView.findViewById(R.id.school_comment_scrollview);
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
                        pageNum++;
                        Log.d("校友圈", "获取更多数据: ");
                        new SchoolCommentAsyncTask().execute();
                    } else {
                        // ScrollView未滑动到底部
                    }
                }
            }
        });
    }

    private class LikeAsyncTask extends AsyncTask<Void, Void, String> {
        private String id;
        private String islike;
        private ImageView comment_like_icon;
        private TextView comment_like;
        private String like_count;
        private LinearLayout like_button;
        private LikeAsyncTask(String id,String islike,ImageView comment_like_icon,TextView comment_like,String like_count,LinearLayout like_button){
            this.id=id;
            this.islike=islike;
            this.comment_like_icon=comment_like_icon;
            this.comment_like=comment_like;
            this.like_count=like_count;
            this.like_button=like_button;
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {

                return secondClassUser.schoolLikeContent(id,islike);
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
                if(result.equals(islike)){
                    Log.d("二课点赞", "请求成功: "+result);
                    //请求成功
                    if(result.equals("1")){
                        //点赞成功
                        comment_like_icon.setImageResource(R.drawable.likeicon2);
                        comment_like.setText(String.valueOf(Integer.parseInt(like_count)+1));
                        comment_like.setTextColor(getResources().getColor(R.color.icon_blue));
                        like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new LikeAsyncTask(id,"0",comment_like_icon,comment_like,String.valueOf(Integer.parseInt(like_count)+1),like_button).execute();
                            }
                        });
                    }else{
                        //取消点赞成功
                        comment_like_icon.setImageResource(R.drawable.likeicon1);
                        comment_like.setText(String.valueOf(Integer.parseInt(like_count)-1));
                        comment_like.setTextColor(getResources().getColor(R.color.black));
                        like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new LikeAsyncTask(id,"1",comment_like_icon,comment_like,String.valueOf(Integer.parseInt(like_count)-1),like_button).execute();
                            }
                        });
                    }
                }else {
                    //请求失败
                }
            }
        }
    }
}
