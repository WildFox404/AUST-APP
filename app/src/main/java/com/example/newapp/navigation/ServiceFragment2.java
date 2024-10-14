package com.example.newapp.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.campuscalender.CampusCalendarActivity;
import com.example.newapp.chargequery.ChargeQueryActivity;
import com.example.newapp.emptyclassrooms.EmptyBuildingsActivity;
import com.example.newapp.grade.GradeViewerActivity;
import com.example.newapp.incubationprograms.IncubationProgramsActivity;
import com.example.newapp.studydetails.PlanCompletionActivity;
import com.example.newapp.test.TestViewerActivity;
import com.example.newapp.utils.NetworkUtils;
import com.example.newapp.utils.ToastUtils;
import com.example.newapp.webview.WebViewActivity;
import com.example.newapp.wificonnect.WifiConnectActivity;

public class ServiceFragment2 extends Fragment {
    private SharedPreferences sharedPreferences;
    private PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        View view = inflater.inflate(R.layout.service2, container, false);

        ConstraintLayout rootView = view.findViewById(R.id.rootView);
        ImageView pangguai=view.findViewById(R.id.pangguai);
        ImageView smart_building=view.findViewById(R.id.smart_building);
        ImageView second_class=view.findViewById(R.id.second_class);
        ImageView youth_learning=view.findViewById(R.id.youth_learning);
        ImageView educational_system = view.findViewById(R.id.educational_system);
        ImageView year_payment = view.findViewById(R.id.year_payment);
        ImageView charge_query = view.findViewById(R.id.charge_query);
        ImageView school_map = view.findViewById(R.id.school_map);
        ImageView student_system = view.findViewById(R.id.student_system);
        ImageView wificonnect = view.findViewById(R.id.wificonnect);

        // 初始化弹出窗口
        popupWindow = new PopupWindow();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        wificonnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WifiConnectActivity.class);
                startActivity(intent);
            }
        });

        student_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://xsgl.aust.edu.cn/student//wap/main/welcome");
                startActivity(intent);
            }
        });
        school_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置弹出窗口的内容视图
                View contentView = inflater.inflate(R.layout.popup_image, null);
                popupWindow.setContentView(contentView);
                // 在弹出窗口中显示图片
                ImageView imageView = contentView.findViewById(R.id.image_view);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.school_map));

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
        charge_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChargeQueryActivity.class);
                startActivity(intent);
            }
        });
        year_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://cwsf.aust.edu.cn/appjf/#/?t=1724565615711");
                startActivity(intent);
            }
        });
        youth_learning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://dxx.ahyouth.org.cn/");
                startActivity(intent);
            }
        });
        second_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","https://win.9xueqi.com/");
                startActivity(intent);
            }
        });
        smart_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","https://m.zhtj.youth.cn/zhtj/");
                startActivity(intent);
            }
        });
        pangguai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("胖乖绿色版"); // 设置对话框标题
                builder.setMessage("链接1:速度慢,下载直接安装(推荐)\n" +
                        "链接2:速度快,但是需要修改后缀名\n" +
                        "链接3:速度慢,下载直接安装"); // 设置对话框内容

                // 添加右下角的三个按钮（积极按钮）
                builder.setPositiveButton("链接3", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 定义要访问的网址
                        String url = "https://github.com/ForsakenDelusion/AUST-CS-Plan/raw/main/res/11.45.14.@AWAvenue.apk";
                        // 创建Intent对象，Intent.ACTION_VIEW为指定动作，即跳转到浏览器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        // 设置Intent的数据（网址）
                        intent.setData(Uri.parse(url));
                        // 启动Intent，跳转到浏览器
                        startActivity(intent);
                        // 点击按钮1后的逻辑操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });

                builder.setNeutralButton("链接1", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 定义要访问的网址
                        String url = "http://121.41.128.172:3000/pangguai.apk";
                        // 创建Intent对象，Intent.ACTION_VIEW为指定动作，即跳转到浏览器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        // 设置Intent的数据（网址）
                        intent.setData(Uri.parse(url));
                        // 启动Intent，跳转到浏览器
                        startActivity(intent);
                        // 点击按钮2后的逻辑操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });

                builder.setNegativeButton("链接2", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 定义要访问的网址
                        String url = "https://gooodclass.oss-cn-hangzhou.aliyuncs.com/11.45.14.%40AWAvenue%20%281%29.apk.1";

                        // 创建Intent对象，Intent.ACTION_VIEW为指定动作，即跳转到浏览器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        // 设置Intent的数据（网址）
                        intent.setData(Uri.parse(url));
                        // 启动Intent，跳转到浏览器
                        startActivity(intent);
                        // 点击按钮1后的逻辑操作
                        dialog.dismiss(); // 关闭对话框
                        // 点击按钮3后的逻辑操作
                        dialog.dismiss(); // 关闭对话框
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
        return view;

    }
}