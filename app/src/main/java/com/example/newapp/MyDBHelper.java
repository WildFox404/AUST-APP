package com.example.newapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 3;

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS my_course ("
                + "username VARCHAR(10),"
                + "year VARCHAR(4),"
                + "term VARCHAR(1),"
                + "week VARCHAR(2),"
                + "content TEXT)";
        db.execSQL(createTableQuery);

        // 创建 my_grade 表
        String createGradeTableQuery = "CREATE TABLE IF NOT EXISTS my_grade ("
                + "username VARCHAR(10),"
                + "year VARCHAR(4),"
                + "term VARCHAR(1),"
                + "content TEXT)";
        db.execSQL(createGradeTableQuery);

        String createTestTableQuery = "CREATE TABLE IF NOT EXISTS my_test ("
                + "username VARCHAR(10),"
                + "content TEXT)";
        db.execSQL(createTestTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时的处理逻辑
        // 在数据库升级时执行的操作
        String createTestTableQuery = "CREATE TABLE IF NOT EXISTS my_test ("
                + "username VARCHAR(10),"
                + "content TEXT)";
        db.execSQL(createTestTableQuery);
    }
    public void insertGradeData(String username, String year, String term, List<Map<String, String>> content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        String jsonContent = gson.toJson(content);
        values.put("username", username);
        values.put("year", year);
        values.put("term", term);
        values.put("content", jsonContent);
        db.insert("my_grade", null, values);
        db.close();
    }

    public void insertTestData(String username, List<Map<String, String>> test_result){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        String jsonContent = gson.toJson(test_result);
        values.put("username", username);
        values.put("content", jsonContent);
        db.insert("my_test", null, values);
        db.close();
    }

    public List<Map<String, String>> getTestData(String username){
        List<Map<String, String>> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();

        String[] columns = {"content"};
        String selection = "username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("my_test", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String jsonContent = null;
            int columnIndex = cursor.getColumnIndex("content");
            if (columnIndex != -1) {
                jsonContent = cursor.getString(columnIndex);
                Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
                result = gson.fromJson(jsonContent, type);
            } else {
                // 处理列索引不存在的情况
            }
        }

        cursor.close();
        db.close();

        return result;
    }

    public void insertCourseData(String username, String year, String term, String week, HashMap<Integer, List<Course>> content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        String jsonContent = gson.toJson(content);
        values.put("username", username);
        values.put("year", year);
        values.put("term", term);
        values.put("week", week);
        values.put("content", jsonContent);
        db.insert("my_course", null, values);
        db.close();
    }

    public int getCourseCountByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"username"};
        String selection = "username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("my_course", projection, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    public void deleteAllDataByUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("my_course", "username=?", new String[]{username});
        db.close();
    }

    public HashMap<Integer, List<Course>> getCourseData(String username, String year, String term, String week) {
        HashMap<Integer, List<Course>> result = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();

        String[] columns = {"content"};
        String selection = "username = ? AND year = ? AND term = ? AND week = ?";
        String[] selectionArgs = {username, year, term, week};

        Cursor cursor = db.query("my_course", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String jsonContent = null;
            int columnIndex = cursor.getColumnIndex("content");
            if (columnIndex != -1) {
                jsonContent = cursor.getString(columnIndex);
                // 这里继续处理jsonContent
            } else {
                // 处理列索引不存在的情况
            }
            Type type = new TypeToken<HashMap<Integer, List<Course>>>() {}.getType();
            result = gson.fromJson(jsonContent, type);
        }

        cursor.close();
        db.close();

        return result;
    }

    public List<Map<String, String>> getGradeData(String username, String year, String term) {
        List<Map<String, String>> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();

        String[] columns = {"content"};
        String selection = "username = ? AND year = ? AND term = ?";
        String[] selectionArgs = {username, year, term};

        Cursor cursor = db.query("my_grade", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String jsonContent = null;
            int columnIndex = cursor.getColumnIndex("content");
            if (columnIndex != -1) {
                jsonContent = cursor.getString(columnIndex);
                Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
                result = gson.fromJson(jsonContent, type);
            } else {
                // 处理列索引不存在的情况
            }
        }

        cursor.close();
        db.close();

        return result;
    }
}
