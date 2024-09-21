package com.example.newapp.chargequery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.entries.ChargeApi;
import com.example.newapp.secondclassactivity.SecondClassActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChargeQueryActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private ChargeApi chargeApi =new ChargeApi();
    private LinearLayout linearLayout;
    private String[] stationids = {"015845550204","015743549231","016228548629","016970549222","017136548868",
            //外国语,A,A,A,A
    "018131550010","018187549554","018642549440","018460549339","018211548999","018478550506","024389553147",
            //B,B,B,B
    "024335550215","024706549366","019548549404","024204549374","024658549316","018125549722","019575548957","026699549676","018132549243",
            //C,C,C,C
    "028195549762","027188549691","026699549676","026417550650",
            //D,D,D,D
    "026366555990","025965556985","027423555816",
            //F,F,F,F
    "028504556119","018125549722","027054553856","022451554387","020546554396","016962554456","015471552392","025002552525","019994553221",
    "029140551694","024698553366","023056553212","017822553702","027521552113","015503554271","017676552859","024658553368","011534554224",
    "018012552118","020068556157","021274553112"
            //杂项
    };
    private  LinearLayout.LayoutParams layoutParams;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chargestationview);

        inflater = getLayoutInflater();
        linearLayout=findViewById(R.id.charge_linearlayout);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 创建一个固定大小的线程池，可以同时执行多个任务
        int corePoolSize = 5; // 线程池中保持的线程数
        int maximumPoolSize = 10; // 线程池中允许的最大线程数
        long keepAliveTime = 0L; // 当线程数大于核心线程数时，多余的空闲线程的存活时间
        TimeUnit unit = TimeUnit.MILLISECONDS; // 时间单位

        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>());

        for (String stationid : stationids) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JsonArray results = chargeApi.getChargeStation(stationid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (results != null) {
                                    updateUI(results);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e("考试获取", "处理结果时出现异常: " + e.getMessage());
                        // 创建一个空的JsonArray
                    }
                }
            });
        }
        // 关闭线程池
        executor.shutdown();
    }

    private void updateUI(JsonArray results){
        View chargestationcontent =new View(ChargeQueryActivity.this);
        chargestationcontent = inflater.inflate(R.layout.chargestationcontent, null);
        LinearLayout charge_layout = chargestationcontent.findViewById(R.id.charge_layout);
        TextView charge_name = chargestationcontent.findViewById(R.id.charge_name);
        String stationname="";

        if (results != null) {
            // Assuming grade_result is a field in your class to store the JSON data
            Log.d("考试安排获取", "考试安排获取成功");
            for (JsonElement result : results) {
                JsonObject jsonObject = result.getAsJsonObject();
                String checkcode = jsonObject.get("checkcode").getAsString();
                if(checkcode.equals("SUCCESS")){
                    String pileid = jsonObject.get("pileid").getAsString();
                    stationname = jsonObject.get("stationname").getAsString();
                    String plugcount = jsonObject.get("plugcount").getAsString();

                    View chargestationcontentchild =new View(ChargeQueryActivity.this);
                    chargestationcontentchild = inflater.inflate(R.layout.chargestationcontentchild, null);

                    TextView charge_id = chargestationcontentchild.findViewById(R.id.charge_id);
                    TextView charge_count = chargestationcontentchild.findViewById(R.id.charge_count);
                    charge_id.setText(pileid);
                    charge_count.setText(plugcount+"个插座可用");
                    charge_layout.addView(chargestationcontentchild);
                }
            }
        }
        if (stationname.equals("")){
            //当前站点不可用
        }else {
            charge_name.setText(stationname);
            linearLayout.addView(chargestationcontent);
            View view =new View(ChargeQueryActivity.this);
            view.setLayoutParams(layoutParams);
            linearLayout.addView(view);
        }
    }
}
