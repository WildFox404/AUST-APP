package com.example.newapp.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.newapp.*;
import com.example.newapp.campuscalender.CampusCalendarActivity;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.emptyclassrooms.EmptyBuildingsActivity;
import com.example.newapp.entries.SharedViewModel;
import com.example.newapp.entries.User;
import com.example.newapp.incubationprograms.IncubationProgramsActivity;
import com.example.newapp.studydetails.PlanCompletionActivity;
import com.example.newapp.test.TestViewerActivity;
import com.example.newapp.grade.GradeViewerActivity;
import com.example.newapp.utils.NetworkUtils;
import com.example.newapp.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.*;

public class ServiceFragment extends Fragment {
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        View view = inflater.inflate(R.layout.service, container, false);
        ImageView gradeView= view.findViewById(R.id.gradeview);
        ImageView testView =view.findViewById(R.id.testview);
        ImageView getEmptyRooms = view.findViewById(R.id.getEmptyRooms);
        ImageView campus_calender =view.findViewById(R.id.campus_calender);
        ImageView plan_complete =view.findViewById(R.id.planComplete);
        ImageView incubation_programs=view.findViewById(R.id.incubationPrograms);
        ImageView pangguai=view.findViewById(R.id.pangguai);
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

                // 创建并显示对话框
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        incubation_programs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                if(NetworkUtils.isNetworkConnected(getContext())){
                    //网络已链接
                    Intent intent = new Intent(getActivity(), IncubationProgramsActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtils.showToastShort(getContext(),"网络去开小差去了:)");
                }
            }
        });
        plan_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                if(NetworkUtils.isNetworkConnected(getContext())){
                    //网络已链接
                    Intent intent = new Intent(getActivity(), PlanCompletionActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtils.showToastShort(getContext(),"网络去开小差去了:)");
                }
            }
        });
        campus_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                if(NetworkUtils.isNetworkConnected(getContext())){
                    //网络已链接
                    Intent intent = new Intent(getActivity(), CampusCalendarActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtils.showToastShort(getContext(),"网络去开小差去了:)");
                }
            }
        });
        gradeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), GradeViewerActivity.class);
                startActivity(intent);
            }
        });
        testView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(), TestViewerActivity.class);
                startActivity(intent);
            }
        });

        getEmptyRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                if(NetworkUtils.isNetworkConnected(getContext())){
                    //网络已链接
                    Intent intent = new Intent(getActivity(), EmptyBuildingsActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtils.showToastShort(getContext(),"网络去开小差去了:)");
                }
            }
        });
        return view;

    }
}