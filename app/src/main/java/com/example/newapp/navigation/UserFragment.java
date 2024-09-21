package com.example.newapp.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.newapp.*;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.Api;
import com.example.newapp.entries.SharedViewModel;
import com.example.newapp.entries.User;

import java.util.List;
import java.util.Random;

public class UserFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MyDBHelper myDBHelper;
    private SharedViewModel viewModel;
    private User user=User.getInstance();
    private Api api =new Api();
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        myDBHelper = new MyDBHelper(getContext()); // 实例化 MyDBHelper 对象

        view = inflater.inflate(R.layout.user, container, false);
        LinearLayout loginOut= view.findViewById(R.id.login_out);
        LinearLayout about_our= view.findViewById(R.id.about_our);
        TextView user_name=view.findViewById(R.id.user_name);
        TextView depart_name=view.findViewById(R.id.depart_name);

        ImageView camping_image =view.findViewById(R.id.camping_image);
        int[] campingImages = {R.drawable.camping1, R.drawable.camping4, R.drawable.camping5, R.drawable.camping6, R.drawable.camping7,
                R.drawable.camping8, R.drawable.camping10, R.drawable.camping11,
                R.drawable.camping12};
        Random rand = new Random();
        int randomIndex = rand.nextInt(campingImages.length);
        camping_image.setImageResource(campingImages[randomIndex]);

        ImageView usericon=view.findViewById(R.id.usericon);
        int[] usericonImages = {R.drawable.useravataricon1, R.drawable.useravataricon3, R.drawable.useravataricon4, R.drawable.useravataricon5, R.drawable.useravataricon6,
                R.drawable.useravataricon7, R.drawable.useravataricon8, R.drawable.useravataricon9, R.drawable.useravataricon10,
                R.drawable.useravataricon11,R.drawable.useravataricon12,R.drawable.useravataricon13,R.drawable.useravataricon14,
                R.drawable.useravataricon15,R.drawable.useravataricon16};
        randomIndex = rand.nextInt(usericonImages.length);
        usericon.setImageResource(usericonImages[randomIndex]);

        user_name.setText(user.getUser_name());
        depart_name.setText(user.getDepart_name());
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取SharedPreferences的编辑器
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // 删除ids属性
                editor.remove("username");
                editor.remove("password");
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        about_our.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("关于我们"); // 设置对话框标题

                // 创建两个不同的TextView
                TextView qqTextView = new TextView(getContext());
                qqTextView.setTextColor(Color.BLACK);
                qqTextView.setTextSize(16);
                qqTextView.setPadding(20, 20, 20, 20);

                TextView githubTextView = new TextView(getContext());
                githubTextView.setTextColor(Color.BLUE);
                githubTextView.setTextSize(14);
                githubTextView.setPadding(20, 20, 20, 20);

                SpannableString qqTextContent = new SpannableString("QQ群:\n956026820");
                qqTextContent.setSpan(new UnderlineSpan(), 0, qqTextContent.length(), 0);
                SpannableString githubTextContent = new SpannableString("GitHub:\nhttps://github.com/WildFox404/AUST-APP");
                githubTextContent.setSpan(new UnderlineSpan(), 0, githubTextContent.length(), 0);

                qqTextView.setText(qqTextContent);
                qqTextView.setTextColor(Color.BLUE);
                githubTextView.setText(githubTextContent);
                githubTextView.setTextColor(Color.BLUE);

                qqTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = "tPPUYS7eP54EcUs6qdDLt4m-Ufr_hzVb"; // 获取TextView中的QQ号
                        // 构造跳转到QQ群页面的Intent
                        Intent intent = new Intent();
                        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
                        // 启动Intent，跳转到QQ群页面
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            // 未安装手Q或安装的版本不支持
                        }
                    }
                });
                githubTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String githubLink = "https://github.com/WildFox404/AUST-APP";  // 替换成你要跳转的 GitHub 链接

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setComponent(null);
                        intent.setSelector(null);

                        PackageManager packageManager = getContext().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;

                        if (isIntentSafe) {
                            // 有应用可以处理该 Intent，直接启动
                            startActivity(intent);
                        } else {
                            // 没有应用可以处理该 Intent，提示用户打开浏览器
                            Toast.makeText(getContext(), "没有找到GitHub应用，将在浏览器中打开", Toast.LENGTH_SHORT).show();
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
                            startActivity(intent);
                        }
                    }
                });
                // 将这两个TextView添加到AlertDialog的内容中
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(qqTextView);
                layout.addView(githubTextView);

                builder.setView(layout);

                // 添加右下角的三个按钮（积极按钮）
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击按钮1后的逻辑操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });

                // 创建自定义的背景Drawable
                Drawable drawable = getResources().getDrawable(R.drawable.selectbutton5);
                // 创建并显示对话框
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(drawable);
                dialog.show();
            }
        });
        new SentenceAsyncTask().execute();

        return view;
    }

    private class SentenceAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return api.get_sentence();
            } catch (Exception e) {
                Log.e("SentenceAsyncTask", "处理结果时出现异常: " + e.getMessage());
                // 创建一个空的JsonArray
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Assuming grade_result is a field in your class to store the JSON data
                Log.d("SentenceAsyncTask", "SentenceAsyncTask获取成功");
                InitSentence(result);
            }
        }
    }

    private void InitSentence(String result) {
        TextView sentence_textview =view.findViewById(R.id.sentence);
        sentence_textview.setText(result);
    }
}
