package com.example.newapp;

public class Course {
    private String courseName;
    private String courseLocation;
    private int Week;
    private int Section;

    public Course(String courseName, String courseLocation, int Week, int Section) {
        this.courseName = courseName;
        this.courseLocation = courseLocation;
        this.Week = Week;
        this.Section = Section;

    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public void setWeek(int week) {
        Week = week;
    }

    public void setSection(int section) {
        Section = section;
    }

    // 添加 getter 和 setter 方法
    public int getSection() {
        return this.Section;
    }

    public int getWeek() {
        return this.Week;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseLocation() {
        return this.courseLocation;
    }
    // 其他操作方法
}