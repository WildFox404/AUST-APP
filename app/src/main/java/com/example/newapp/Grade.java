package com.example.newapp;


public class Grade {
    public Grade(String course_type_name, String credits_completed, String credits_required, String num_completed, String num_required, String relation_text) {
        this.course_type_name = course_type_name;
        this.credits_completed = credits_completed;
        this.credits_required = credits_required;
        this.num_completed = num_completed;
        this.num_required = num_required;
        this.relation_text = relation_text;
    }

    private String course_type_name ;   //学年
    private String credits_completed ;    //学期
    private String credits_required ;   //课程代码
    private String num_completed ;   //课程名字
    private String num_required;
    private String relation_text ;  //课程性质

    public String getCourse_type_name() {
        return course_type_name;
    }

    public void setCourse_type_name(String course_type_name) {
        this.course_type_name = course_type_name;
    }

    public String getCredits_completed() {
        return credits_completed;
    }

    public void setCredits_completed(String credits_completed) {
        this.credits_completed = credits_completed;
    }

    public String getCredits_required() {
        return credits_required;
    }

    public void setCredits_required(String credits_required) {
        this.credits_required = credits_required;
    }

    public String getNum_completed() {
        return num_completed;
    }

    public void setNum_completed(String num_completed) {
        this.num_completed = num_completed;
    }

    public String getNum_required() {
        return num_required;
    }

    public void setNum_required(String num_required) {
        this.num_required = num_required;
    }

    public String getRelation_text() {
        return relation_text;
    }

    public void setRelation_text(String relation_text) {
        this.relation_text = relation_text;
    }
}

