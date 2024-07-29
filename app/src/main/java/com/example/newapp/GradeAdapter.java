package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;


public class GradeAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Grade> grade_list;
    private ArrayList<ArrayList<Item>> item_list;
    private GradientDrawable border;
    private GradientDrawable child_border;
    public GradeAdapter(Context context, ArrayList<Grade> grade_list,ArrayList<ArrayList<Item>> item_list) {
        this.context = context;
        this.grade_list = grade_list;
        this.item_list = item_list;

        // 创建一个GradientDrawable对象
        border = new GradientDrawable();
        // 设置边框颜色和宽度
        border.setStroke(2, Color.BLACK);
        // 设置圆角半径
        border.setCornerRadius(10);
        //设置背景颜色
        border.setColor(context.getResources().getColor(R.color.bule_white1));

        // 创建一个GradientDrawable对象
        child_border = new GradientDrawable();
        // 设置边框颜色和宽度
        child_border.setStroke(1, Color.BLACK);
        // 设置圆角半径
        child_border.setCornerRadius(10);
        //设置背景颜色
        child_border.setColor(context.getResources().getColor(R.color.bule_white));
    }


    @Override
    //获取分组个数
    public int getGroupCount() {
        return grade_list.size();
    }

    @Override
    //分组中子选项个数为1
    public int getChildrenCount(int groupPosition) {
        return item_list.get(groupPosition).size();
    }

    @Override
    //获取指定分组数据
    public Object getGroup(int groupPosition) {
        return grade_list.get(groupPosition);
    }

    @Override
    //获取指定子选项数据
    public Object getChild(int groupPosition, int childPosition) {
        return item_list.get(groupPosition).get(childPosition);
    }

    @Override
    //获取指定分组的id
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {

        GroupHolder groupHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.studydetailslevel1, null);
            groupHolder=new GroupHolder();
            groupHolder.main_layout =convertView.findViewById(R.id.main_layout);
            groupHolder.main_layout.setBackground(border);
            groupHolder.tv_course_type_name = convertView.findViewById(R.id.course_type_name);
            groupHolder.tv_credits = convertView.findViewById(R.id.credits); // 课程名
            groupHolder.tv_num = convertView.findViewById(R.id.num);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if(Float.parseFloat(grade_list.get(groupPosition).getCredits_completed())<Float.parseFloat(grade_list.get(groupPosition).getCredits_required())){
            groupHolder.tv_credits.setTextColor(Color.RED);
        }else{
            groupHolder.tv_credits.setTextColor(Color.BLUE);
        }
        if(Float.parseFloat(grade_list.get(groupPosition).getNum_completed())<Float.parseFloat(grade_list.get(groupPosition).getNum_required())){
            groupHolder.tv_num.setTextColor(Color.RED);
        }else{
            groupHolder.tv_num.setTextColor(Color.BLUE);
        }
        groupHolder.tv_course_type_name.setText(grade_list.get(groupPosition).getCourse_type_name());
        groupHolder.tv_credits.setText(grade_list.get(groupPosition).getCredits_completed()+"/"+grade_list.get(groupPosition).getCredits_required());
        groupHolder.tv_num.setText(grade_list.get(groupPosition).getNum_completed()+"/"+grade_list.get(groupPosition).getNum_required());
        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.studydetailslevel2, null);
            childHolder = new ChildHolder();
            childHolder.tv_group_course_type_name = convertView.findViewById(R.id.group_course_type_name);
            childHolder.tv_group_credits = convertView.findViewById(R.id.group_credits);
            childHolder.tv_group_num = convertView.findViewById(R.id.group_num);
            childHolder.main_layout =convertView.findViewById(R.id.main_layout);
            childHolder.main_layout.setBackground(child_border);
            childHolder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(context,StudyDetailsCoursesActivity.class);
                    // 将JsonArray转换为字符串
                    String jsonArrayString = item_list.get(groupPosition).get(childPosition).getPlan_course_audit_results();
                    intent.putExtra("jsonArray", jsonArrayString);
                    Log.d("getChildView", jsonArrayString);
                    intent.putExtra("group_course_type_name", item_list.get(groupPosition).get(childPosition).getGroup_course_type_name());
                    context.startActivity(intent);
                }
            });
            convertView.setTag(childHolder);
        }else{
            childHolder = (ChildHolder) convertView.getTag();
        }

        if(Float.parseFloat(item_list.get(groupPosition).get(childPosition).getGroup_credits_completed())<Float.parseFloat(item_list.get(groupPosition).get(childPosition).getGroup_credits_required())){
            childHolder.tv_group_credits.setTextColor(Color.RED);
        }else{
            childHolder.tv_group_credits.setTextColor(Color.BLUE);
        }
        if(Float.parseFloat(item_list.get(groupPosition).get(childPosition).getGroup_num_completed())<Float.parseFloat(item_list.get(groupPosition).get(childPosition).getGroup_num_required())){
            childHolder.tv_group_num.setTextColor(Color.RED);
        }else{
            childHolder.tv_group_num.setTextColor(Color.BLUE);
        }
        childHolder.tv_group_course_type_name.setText(item_list.get(groupPosition).get(childPosition).getGroup_course_type_name());
        childHolder.tv_group_credits.setText(item_list.get(groupPosition).get(childPosition).getGroup_credits_completed()+"/"+item_list.get(groupPosition).get(childPosition).getGroup_credits_required());
        childHolder.tv_group_num.setText(item_list.get(groupPosition).get(childPosition).getGroup_num_completed()+"/"+item_list.get(groupPosition).get(childPosition).getGroup_num_required());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_course_type_name;
        TextView tv_credits;
        TextView tv_num;
        LinearLayout main_layout;
    }

    static class ChildHolder {
        TextView tv_group_course_type_name;
        TextView tv_group_credits;
        TextView tv_group_num;
        LinearLayout main_layout;
    }
}
