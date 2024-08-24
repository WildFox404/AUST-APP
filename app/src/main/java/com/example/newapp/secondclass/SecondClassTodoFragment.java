package com.example.newapp.secondclass;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.newapp.R;
import com.example.newapp.entries.SecondClassUser;
import com.example.newapp.incubationprograms.IncubationProgramsActivity;
import com.example.newapp.secondclassactivity.SecondClassPersonalRewardsActivity;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SecondClassTodoFragment extends Fragment {
    private SecondClassUser secondClassUser =SecondClassUser.getInstance();
    private JsonArray message_jsonarray;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.secondclasstodoview, container, false);
        TextView todo = view.findViewById(R.id.todo);
        TextView message = view.findViewById(R.id.message);
        LinearLayout nothing =view.findViewById(R.id.nothing);
        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!todo.isSelected()) {
                    todo.setTextColor(getResources().getColor(R.color.bule, null));
                    message.setTextColor(getResources().getColor(R.color.black, null));
                    nothing.setVisibility(View.VISIBLE);
                    new TodoAsyncTask().execute();
                }
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.isSelected()) {
                    todo.setTextColor(getResources().getColor(R.color.black, null));
                    message.setTextColor(getResources().getColor(R.color.bule, null));
                    nothing.setVisibility(View.INVISIBLE);
                    new MessageAsyncTask().execute();
                }
            }
        });

        new TodoAsyncTask().execute();
        return view;
    }
    private class TodoAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return null;
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                message_jsonarray = result;
                Log.d("考试安排获取", "考试安排获取成功");
            }
            updateTodoUI();
        }
    }

    private void updateTodoUI() {
        LinearLayout todo_content =view.findViewById(R.id.todo_content);
        todo_content.removeAllViews();
    }

    private class MessageAsyncTask extends AsyncTask<Void, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(Void... voids) {
            try {

                return secondClassUser.getTodoMessage();
            } catch (Exception e) {
                Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                message_jsonarray = result;
                Log.d("考试安排获取", "考试安排获取成功");
                updateMessageUI();
            }
        }
    }

    private void updateMessageUI() {


        // 在Fragment中获取LayoutInflater实例
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout todo_content =view.findViewById(R.id.todo_content);
        todo_content.removeAllViews();

        for (JsonElement message_element : message_jsonarray) {
            try {
                JsonObject message_object = message_element.getAsJsonObject();
                JsonObject extras = message_object.get("extras").getAsJsonObject();
                String jumpInfo = extras.get("jumpInfo").getAsString();
                String subTypeName = message_object.get("subTypeName").getAsString();
                String content = message_object.get("content").getAsString();
                String ctime = message_object.get("ctime").getAsString();

                View message_view =new View(getContext());
                message_view = inflater.inflate(R.layout.secondclassmessage, null);
                TextView message_type = message_view.findViewById(R.id.message_type);
                TextView message_time = message_view.findViewById(R.id.message_time);
                TextView message_content = message_view.findViewById(R.id.message_content);

                LinearLayout root = message_view.findViewById(R.id.root);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SecondClassPersonalRewardsActivity.class);
                        intent.putExtra("jumpInfo", jumpInfo);
                        startActivity(intent);
                    }
                });

                message_type.setText(subTypeName);
                message_time.setText(DateUtils.convertTimestampToDate(ctime));
                message_content.setText(content);

                todo_content.addView(message_view);
            }catch (Exception e) {
                // 处理ExceptionType1类型的异常
            }
        }
    }
}
