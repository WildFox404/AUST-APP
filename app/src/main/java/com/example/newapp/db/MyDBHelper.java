package com.example.newapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.newapp.db.Course;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 6;
    private Gson gson = new Gson();;

    private SQLiteDatabase db;

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS semesterDB (" +
                "SEMESTER VARCHAR(10) PRIMARY KEY," +
                "COLUMN_JSON_DATA TEXT)";
        db.execSQL(createTableQuery);
        Log.d("数据库onCreate", "数据库onCreate成功");

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='semesterDB'", null);
        boolean tableExists = cursor.moveToFirst();
        cursor.close();

        if (tableExists) {
            Log.d("onCreate", "表已成功创建");
        } else {
            Log.w("onCreate", "表创建失败");
        }

        String jsonData="{\n" +
                "            \"weeks\": 20,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-08-26\",\n" +
                "                    \"endDate\": \"2024-09-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-09-02\",\n" +
                "                    \"endDate\": \"2024-09-08\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-09-09\",\n" +
                "                    \"endDate\": \"2024-09-15\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-09-16\",\n" +
                "                    \"endDate\": \"2024-09-22\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-09-23\",\n" +
                "                    \"endDate\": \"2024-09-29\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-09-30\",\n" +
                "                    \"endDate\": \"2024-10-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-10-07\",\n" +
                "                    \"endDate\": \"2024-10-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-10-14\",\n" +
                "                    \"endDate\": \"2024-10-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-10-21\",\n" +
                "                    \"endDate\": \"2024-10-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-10-28\",\n" +
                "                    \"endDate\": \"2024-11-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-11-04\",\n" +
                "                    \"endDate\": \"2024-11-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-11-11\",\n" +
                "                    \"endDate\": \"2024-11-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-11-18\",\n" +
                "                    \"endDate\": \"2024-11-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-11-25\",\n" +
                "                    \"endDate\": \"2024-12-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-12-02\",\n" +
                "                    \"endDate\": \"2024-12-08\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-12-09\",\n" +
                "                    \"endDate\": \"2024-12-15\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-12-16\",\n" +
                "                    \"endDate\": \"2024-12-22\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-12-23\",\n" +
                "                    \"endDate\": \"2024-12-29\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-12-30\",\n" +
                "                    \"endDate\": \"2025-01-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2025-01-06\",\n" +
                "                    \"endDate\": \"2025-01-12\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"260",gson.fromJson(jsonData,JsonObject.class));
        Log.d("数据库onCreate", "数据库onCreate结束");
                jsonData="{\n" +
                "            \"weeks\": 19,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-02-26\",\n" +
                "                    \"endDate\": \"2024-03-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-03-04\",\n" +
                "                    \"endDate\": \"2024-03-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-03-11\",\n" +
                "                    \"endDate\": \"2024-03-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-03-18\",\n" +
                "                    \"endDate\": \"2024-03-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-03-25\",\n" +
                "                    \"endDate\": \"2024-03-31\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-04-01\",\n" +
                "                    \"endDate\": \"2024-04-07\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-04-08\",\n" +
                "                    \"endDate\": \"2024-04-14\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-04-15\",\n" +
                "                    \"endDate\": \"2024-04-21\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-04-22\",\n" +
                "                    \"endDate\": \"2024-04-28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-04-29\",\n" +
                "                    \"endDate\": \"2024-05-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-05-06\",\n" +
                "                    \"endDate\": \"2024-05-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-05-13\",\n" +
                "                    \"endDate\": \"2024-05-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-05-20\",\n" +
                "                    \"endDate\": \"2024-05-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-05-27\",\n" +
                "                    \"endDate\": \"2024-06-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-06-03\",\n" +
                "                    \"endDate\": \"2024-06-09\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-06-10\",\n" +
                "                    \"endDate\": \"2024-06-16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-06-17\",\n" +
                "                    \"endDate\": \"2024-06-23\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-06-24\",\n" +
                "                    \"endDate\": \"2024-06-30\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-07-01\",\n" +
                "                    \"endDate\": \"2024-07-07\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"240",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 21,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-08-28\",\n" +
                "                    \"endDate\": \"2023-09-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-09-04\",\n" +
                "                    \"endDate\": \"2023-09-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-09-11\",\n" +
                "                    \"endDate\": \"2023-09-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-09-18\",\n" +
                "                    \"endDate\": \"2023-09-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-09-25\",\n" +
                "                    \"endDate\": \"2023-10-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-10-02\",\n" +
                "                    \"endDate\": \"2023-10-08\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-10-09\",\n" +
                "                    \"endDate\": \"2023-10-15\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-10-16\",\n" +
                "                    \"endDate\": \"2023-10-22\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-10-23\",\n" +
                "                    \"endDate\": \"2023-10-29\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-10-30\",\n" +
                "                    \"endDate\": \"2023-11-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-11-06\",\n" +
                "                    \"endDate\": \"2023-11-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-11-13\",\n" +
                "                    \"endDate\": \"2023-11-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-11-20\",\n" +
                "                    \"endDate\": \"2023-11-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-11-27\",\n" +
                "                    \"endDate\": \"2023-12-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-12-04\",\n" +
                "                    \"endDate\": \"2023-12-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-12-11\",\n" +
                "                    \"endDate\": \"2023-12-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-12-18\",\n" +
                "                    \"endDate\": \"2023-12-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-12-25\",\n" +
                "                    \"endDate\": \"2023-12-31\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-01-01\",\n" +
                "                    \"endDate\": \"2024-01-07\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-01-08\",\n" +
                "                    \"endDate\": \"2024-01-14\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2024-01-15\",\n" +
                "                    \"endDate\": \"2024-01-21\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"220",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 21,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-02-13\",\n" +
                "                    \"endDate\": \"2023-02-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-02-20\",\n" +
                "                    \"endDate\": \"2023-02-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-02-27\",\n" +
                "                    \"endDate\": \"2023-03-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-03-06\",\n" +
                "                    \"endDate\": \"2023-03-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-03-13\",\n" +
                "                    \"endDate\": \"2023-03-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-03-20\",\n" +
                "                    \"endDate\": \"2023-03-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-03-27\",\n" +
                "                    \"endDate\": \"2023-04-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-04-03\",\n" +
                "                    \"endDate\": \"2023-04-09\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-04-10\",\n" +
                "                    \"endDate\": \"2023-04-16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-04-17\",\n" +
                "                    \"endDate\": \"2023-04-23\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-04-24\",\n" +
                "                    \"endDate\": \"2023-04-30\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-05-01\",\n" +
                "                    \"endDate\": \"2023-05-07\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-05-08\",\n" +
                "                    \"endDate\": \"2023-05-14\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-05-15\",\n" +
                "                    \"endDate\": \"2023-05-21\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-05-22\",\n" +
                "                    \"endDate\": \"2023-05-28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-05-29\",\n" +
                "                    \"endDate\": \"2023-06-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-06-05\",\n" +
                "                    \"endDate\": \"2023-06-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-06-12\",\n" +
                "                    \"endDate\": \"2023-06-18\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-06-19\",\n" +
                "                    \"endDate\": \"2023-06-25\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-06-26\",\n" +
                "                    \"endDate\": \"2023-07-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-07-03\",\n" +
                "                    \"endDate\": \"2023-07-09\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"200",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 19,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-08-29\",\n" +
                "                    \"endDate\": \"2022-09-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-09-05\",\n" +
                "                    \"endDate\": \"2022-09-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-09-12\",\n" +
                "                    \"endDate\": \"2022-09-18\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-09-19\",\n" +
                "                    \"endDate\": \"2022-09-25\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-09-26\",\n" +
                "                    \"endDate\": \"2022-10-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-10-03\",\n" +
                "                    \"endDate\": \"2022-10-09\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-10-10\",\n" +
                "                    \"endDate\": \"2022-10-16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-10-17\",\n" +
                "                    \"endDate\": \"2022-10-23\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-10-24\",\n" +
                "                    \"endDate\": \"2022-10-30\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-10-31\",\n" +
                "                    \"endDate\": \"2022-11-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-11-07\",\n" +
                "                    \"endDate\": \"2022-11-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-11-14\",\n" +
                "                    \"endDate\": \"2022-11-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-11-21\",\n" +
                "                    \"endDate\": \"2022-11-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-11-28\",\n" +
                "                    \"endDate\": \"2022-12-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-12-05\",\n" +
                "                    \"endDate\": \"2022-12-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-12-12\",\n" +
                "                    \"endDate\": \"2022-12-18\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-12-19\",\n" +
                "                    \"endDate\": \"2022-12-25\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-12-26\",\n" +
                "                    \"endDate\": \"2023-01-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2023-01-02\",\n" +
                "                    \"endDate\": \"2023-01-08\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"180",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 20,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-02-21\",\n" +
                "                    \"endDate\": \"2022-02-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-02-28\",\n" +
                "                    \"endDate\": \"2022-03-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-03-07\",\n" +
                "                    \"endDate\": \"2022-03-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-03-14\",\n" +
                "                    \"endDate\": \"2022-03-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-03-21\",\n" +
                "                    \"endDate\": \"2022-03-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-03-28\",\n" +
                "                    \"endDate\": \"2022-04-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-04-04\",\n" +
                "                    \"endDate\": \"2022-04-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-04-11\",\n" +
                "                    \"endDate\": \"2022-04-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-04-18\",\n" +
                "                    \"endDate\": \"2022-04-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-04-25\",\n" +
                "                    \"endDate\": \"2022-05-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-05-02\",\n" +
                "                    \"endDate\": \"2022-05-08\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-05-09\",\n" +
                "                    \"endDate\": \"2022-05-15\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-05-16\",\n" +
                "                    \"endDate\": \"2022-05-22\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-05-23\",\n" +
                "                    \"endDate\": \"2022-05-29\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-05-30\",\n" +
                "                    \"endDate\": \"2022-06-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-06-06\",\n" +
                "                    \"endDate\": \"2022-06-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-06-13\",\n" +
                "                    \"endDate\": \"2022-06-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-06-20\",\n" +
                "                    \"endDate\": \"2022-06-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-06-27\",\n" +
                "                    \"endDate\": \"2022-07-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-07-04\",\n" +
                "                    \"endDate\": \"2022-07-10\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"160",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 20,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-08-30\",\n" +
                "                    \"endDate\": \"2021-09-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-09-06\",\n" +
                "                    \"endDate\": \"2021-09-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-09-13\",\n" +
                "                    \"endDate\": \"2021-09-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-09-20\",\n" +
                "                    \"endDate\": \"2021-09-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-09-27\",\n" +
                "                    \"endDate\": \"2021-10-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-10-04\",\n" +
                "                    \"endDate\": \"2021-10-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-10-11\",\n" +
                "                    \"endDate\": \"2021-10-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-10-18\",\n" +
                "                    \"endDate\": \"2021-10-24\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-10-25\",\n" +
                "                    \"endDate\": \"2021-10-31\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-11-01\",\n" +
                "                    \"endDate\": \"2021-11-07\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-11-08\",\n" +
                "                    \"endDate\": \"2021-11-14\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-11-15\",\n" +
                "                    \"endDate\": \"2021-11-21\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-11-22\",\n" +
                "                    \"endDate\": \"2021-11-28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-11-29\",\n" +
                "                    \"endDate\": \"2021-12-05\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-12-06\",\n" +
                "                    \"endDate\": \"2021-12-12\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-12-13\",\n" +
                "                    \"endDate\": \"2021-12-19\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-12-20\",\n" +
                "                    \"endDate\": \"2021-12-26\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-12-27\",\n" +
                "                    \"endDate\": \"2022-01-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-01-03\",\n" +
                "                    \"endDate\": \"2022-01-09\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2022-01-10\",\n" +
                "                    \"endDate\": \"2022-01-16\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"140",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 19,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-03-08\",\n" +
                "                    \"endDate\": \"2021-03-14\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-03-15\",\n" +
                "                    \"endDate\": \"2021-03-21\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-03-22\",\n" +
                "                    \"endDate\": \"2021-03-28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-03-29\",\n" +
                "                    \"endDate\": \"2021-04-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-04-05\",\n" +
                "                    \"endDate\": \"2021-04-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-04-12\",\n" +
                "                    \"endDate\": \"2021-04-18\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-04-19\",\n" +
                "                    \"endDate\": \"2021-04-25\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-04-26\",\n" +
                "                    \"endDate\": \"2021-05-02\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-05-03\",\n" +
                "                    \"endDate\": \"2021-05-09\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-05-10\",\n" +
                "                    \"endDate\": \"2021-05-16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-05-17\",\n" +
                "                    \"endDate\": \"2021-05-23\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-05-24\",\n" +
                "                    \"endDate\": \"2021-05-30\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-05-31\",\n" +
                "                    \"endDate\": \"2021-06-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-06-07\",\n" +
                "                    \"endDate\": \"2021-06-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-06-14\",\n" +
                "                    \"endDate\": \"2021-06-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-06-21\",\n" +
                "                    \"endDate\": \"2021-06-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-06-28\",\n" +
                "                    \"endDate\": \"2021-07-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-07-05\",\n" +
                "                    \"endDate\": \"2021-07-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-07-12\",\n" +
                "                    \"endDate\": \"2021-07-18\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"120",gson.fromJson(jsonData,JsonObject.class));
        jsonData="{\n" +
                "            \"weeks\": 21,\n" +
                "            \"firstWeekday\": 1,\n" +
                "            \"weekArray\": [\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-08-31\",\n" +
                "                    \"endDate\": \"2020-09-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-09-07\",\n" +
                "                    \"endDate\": \"2020-09-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-09-14\",\n" +
                "                    \"endDate\": \"2020-09-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-09-21\",\n" +
                "                    \"endDate\": \"2020-09-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-09-28\",\n" +
                "                    \"endDate\": \"2020-10-04\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-10-05\",\n" +
                "                    \"endDate\": \"2020-10-11\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-10-12\",\n" +
                "                    \"endDate\": \"2020-10-18\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-10-19\",\n" +
                "                    \"endDate\": \"2020-10-25\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-10-26\",\n" +
                "                    \"endDate\": \"2020-11-01\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-11-02\",\n" +
                "                    \"endDate\": \"2020-11-08\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-11-09\",\n" +
                "                    \"endDate\": \"2020-11-15\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-11-16\",\n" +
                "                    \"endDate\": \"2020-11-22\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-11-23\",\n" +
                "                    \"endDate\": \"2020-11-29\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-11-30\",\n" +
                "                    \"endDate\": \"2020-12-06\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-12-07\",\n" +
                "                    \"endDate\": \"2020-12-13\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-12-14\",\n" +
                "                    \"endDate\": \"2020-12-20\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-12-21\",\n" +
                "                    \"endDate\": \"2020-12-27\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2020-12-28\",\n" +
                "                    \"endDate\": \"2021-01-03\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-01-04\",\n" +
                "                    \"endDate\": \"2021-01-10\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-01-11\",\n" +
                "                    \"endDate\": \"2021-01-17\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"startDate\": \"2021-01-18\",\n" +
                "                    \"endDate\": \"2021-01-24\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        semesterDBInsertData(db,"100",gson.fromJson(jsonData,JsonObject.class));
    }

    // 查询数据
    public boolean isSemesterExists(String semester) {
        SQLiteDatabase db = this.getReadableDatabase(); // 获取可读的数据库对象
        String[] columns = {"SEMESTER"};
        String selection = "SEMESTER=?";
        String[] selectionArgs = {semester};

        Cursor cursor = db.query("semesterDB", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true; // 数据存在
        } else {
            return false; // 数据不存在
        }
    }
    public void semesterDBInsertData(SQLiteDatabase db, String semesterId, JsonObject jsonData) {
        // 插入数据
        ContentValues values = new ContentValues();
        values.put("SEMESTER", semesterId);
        values.put("COLUMN_JSON_DATA", jsonData.toString());

        try {
            long result = db.insertOrThrow("semesterDB", null, values);
            Log.d("semesterDBInsertData", "插入成功:"+semesterId);
        } catch (Exception e) {
            Log.d("semesterDBInsertData", "插入失败:"+semesterId+", 错误信息:"+e.getMessage());
        }
    }

    public JsonObject getJsonDataBySemesterId(String semesterId) {
        SQLiteDatabase db = this.getReadableDatabase(); // 获取可读的数据库对象
        JsonObject jsonData = null;

        // 构建查询语句
        String[] projection = {"COLUMN_JSON_DATA"};
        String selection = "SEMESTER=?";
        String[] selectionArgs = {semesterId};

        Cursor cursor = db.query("semesterDB", projection, selection, selectionArgs, null, null, null); // 执行查询操作


        if (cursor.moveToFirst()) {
            String jsonContent = null;
            int columnIndex = cursor.getColumnIndex("COLUMN_JSON_DATA");
            if (columnIndex != -1) {
                jsonContent = cursor.getString(columnIndex);
                jsonData = gson.fromJson(jsonContent, JsonObject.class);
            } else {
                // 处理列索引不存在的情况
                jsonData = new JsonObject();
            };
        }
        Log.d("getJsonDataBySemesterId", "getJsonDataBySemesterId: "+jsonData);
        cursor.close(); // 关闭游标
        db.close(); // 关闭数据库

        return jsonData;
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
