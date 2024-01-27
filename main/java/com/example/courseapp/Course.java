package com.example.courseapp;

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

    // 添加 getter 和 setter 方法
    public int getStartSection() {
        return this.Section;
    }

    public int getStartWeek() {
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
