package com.example.newapp;

import com.google.gson.JsonArray;

public class Item {
    public Item(String group_course_type_name, String group_credits_completed, String group_credits_required, String group_num_completed, String group_num_required,String plan_course_audit_results) {

        this.group_course_type_name = group_course_type_name;
        this.group_credits_completed = group_credits_completed;
        this.group_credits_required = group_credits_required;
        this.group_num_completed = group_num_completed;
        this.group_num_required = group_num_required;
        this.plan_course_audit_results=plan_course_audit_results;
    }

    private String group_course_type_name ;   //学分
    private String group_credits_completed ;   //绩点
    private String group_credits_required ;    //分数
    private String group_num_completed ;  //成绩性质
    private String group_num_required;
    private String plan_course_audit_results;

    public String getGroup_course_type_name() {
        return group_course_type_name;
    }

    public void setGroup_course_type_name(String group_course_type_name) {
        this.group_course_type_name = group_course_type_name;
    }

    public String getGroup_credits_completed() {
        return group_credits_completed;
    }

    public void setGroup_credits_completed(String group_credits_completed) {
        this.group_credits_completed = group_credits_completed;
    }

    public String getGroup_credits_required() {
        return group_credits_required;
    }

    public void setGroup_credits_required(String group_credits_required) {
        this.group_credits_required = group_credits_required;
    }

    public String getGroup_num_completed() {
        return group_num_completed;
    }

    public void setGroup_num_completed(String group_num_completed) {
        this.group_num_completed = group_num_completed;
    }

    public String getGroup_num_required() {
        return group_num_required;
    }

    public void setGroup_num_required(String group_num_required) {
        this.group_num_required = group_num_required;
    }


    public String getPlan_course_audit_results() {
        return plan_course_audit_results;
    }

    public void setPlan_course_audit_results(String plan_course_audit_results) {
        this.plan_course_audit_results = plan_course_audit_results;
    }
}
