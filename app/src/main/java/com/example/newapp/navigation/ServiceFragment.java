package com.example.newapp.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.*;

public class ServiceFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;

    private TextView gradeProcess;
    private SharedViewModel viewModel;
    private int completedTasks=0;
    private User user;
    List<Map<String, String>> result;
    private HomeFragment homeFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象
        View view = inflater.inflate(R.layout.service, container, false);
        ImageView gradeView= view.findViewById(R.id.gradeview);
        ImageView testView =view.findViewById(R.id.testview);
        ImageView getEmptyRooms = view.findViewById(R.id.getEmptyRooms);
        ImageView campus_calender =view.findViewById(R.id.campus_calender);
        ImageView plan_complete =view.findViewById(R.id.planComplete);
        ImageView incubation_programs=view.findViewById(R.id.incubationPrograms);
        homeFragment = new HomeFragment();

        user=viewModel.getUser();
        incubation_programs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), IncubationProgramsActivity.class);
                startActivity(intent);
            }
        });
        plan_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), PlanCompletionActivity.class);
                startActivity(intent);
            }
        });
        campus_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建 Intent 对象,指定从当前 Activity 跳转到 GradeViewActivity
                Intent intent = new Intent(getActivity(), CampusCalendarActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(getActivity(), EmptyBuildingsActivity.class);
                startActivity(intent);
            }
        });
        return view;

    }
}