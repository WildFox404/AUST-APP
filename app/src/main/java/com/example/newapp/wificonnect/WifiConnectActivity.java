package com.example.newapp.wificonnect;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.*;
import android.net.wifi.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.newapp.R;
import com.example.newapp.emptyclassrooms.EmptyBuildingsActivity;
import com.example.newapp.entries.WifiUser;
import com.example.newapp.webview.SchoolWifiWebActivity;
import com.example.newapp.webview.WebViewActivity;
import com.google.gson.JsonArray;
import org.w3c.dom.Text;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class WifiConnectActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private  static  final int REQUEST_STATE_PERMISSION =2;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private static final String TARGET_SSID = "AUST_Student";
    private LinearLayout logLayout;
    private TextView liantong;
    private TextView yidong;
    private TextView dianxin;
    private EditText editTextAccount;
    private EditText editTextPassword;
    private Integer seletView = 1;
    private static final String KEY_WIFI_SELECT = "wifi_yunying";
    private static final String KEY_WIFI_ACCOUNT = "wifi_account";
    private static final String KEY_WIFI_PASSWORD = "wifi_password";
    private static final String KEY_WIFI_COOKIE = "wifi_cookie";
    private String current_account;
    private String current_password;
    private Boolean botton_press=false;
    private String  yunying;
    private String cookie;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wificonnectview);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        String savedAccount = sharedPreferences.getString(KEY_WIFI_ACCOUNT, "");
        String savedPassword = sharedPreferences.getString(KEY_WIFI_PASSWORD, "");
        cookie = sharedPreferences.getString(KEY_WIFI_COOKIE,"no6j61pikctu8m5hjc9ms5mrr5");
        seletView = sharedPreferences.getInt(KEY_WIFI_SELECT, 1);

        editTextAccount = findViewById(R.id.editTextAccount);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextAccount.setText(savedAccount);
        editTextPassword.setText(savedPassword);

        logLayout=findViewById(R.id.log_layout);

        liantong=findViewById(R.id.liantong);
        liantong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yunying="@unicom";
                selectTextView(liantong);
                saveSelection(KEY_WIFI_SELECT,1);
            }
        });
        yidong=findViewById(R.id.yidong);
        yidong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yunying="@cmcc";
                selectTextView(yidong);
                saveSelection(KEY_WIFI_SELECT,2);
            }
        });
        dianxin=findViewById(R.id.dianxin);
        dianxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yunying="@aust";
                selectTextView(dianxin);
                saveSelection(KEY_WIFI_SELECT,3);
            }
        });

        if(seletView==1){
            selectTextView(liantong);
            yunying="@unicom";
        }else if(seletView == 2){
            selectTextView(yidong);
            yunying="@cmcc";
        }else{
            selectTextView(dianxin);
            yunying="@aust";
        }

        ImageView exitButton =findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button wifi_connect = findViewById(R.id.wifi_connect);
        wifi_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_account = String.valueOf(editTextAccount.getText());
                current_password = String.valueOf(editTextPassword.getText());
                if(!current_account.equals("")&&!current_password.equals("")) {
                    Log.d("wifi_connect", current_account);
                    Log.d("wifi_connect", current_account);
                    Log.d("wifi_connect", yunying);
                    saveSelection(KEY_WIFI_SELECT,seletView);
                    saveSelection(KEY_WIFI_COOKIE,cookie);
                    saveSelection(KEY_WIFI_ACCOUNT,current_account);
                    saveSelection(KEY_WIFI_PASSWORD,current_password);
                    wifi_connect();
                    botton_press=true;
                }else{
                    toastShow("请输入账号密码");
                }
            }
        });

        Button wifi_open = findViewById(R.id.wifi_open);
        wifi_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_account = String.valueOf(editTextAccount.getText());
                current_password = String.valueOf(editTextPassword.getText());
                if(!current_account.equals("")&&!current_password.equals("")) {
                    saveSelection(KEY_WIFI_SELECT,seletView);
                    saveSelection(KEY_WIFI_COOKIE,cookie);
                    saveSelection(KEY_WIFI_ACCOUNT,current_account);
                    saveSelection(KEY_WIFI_PASSWORD,current_password);
                    Log.d("wifi_connect", current_account);
                    Log.d("wifi_connect", current_account);
                    Log.d("wifi_connect", yunying);
                    Intent intent = new Intent(WifiConnectActivity.this, SchoolWifiWebActivity.class);
                    intent.putExtra("cookie",cookie);
                    intent.putExtra("account",current_account);
                    intent.putExtra("password",current_password);
                    intent.putExtra("yunying",yunying);
                    startActivity(intent);
                }else{
                    toastShow("请输入账号密码");
                }
            }
        });
        // Define and register the BroadcastReceiver
        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("WifiConnectActivity", "receiver");
                String action = intent.getAction();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    Log.d("WifiConnectActivity", "SCAN_RESULTS_AVAILABLE_ACTION");
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    boolean found = false;

                    for (ScanResult result : scanResults) {
                        Log.d("WifiConnectActivity", result.SSID);
                        if (result.SSID.contains(TARGET_SSID)) {
                            found = true;
                            logShow("找到了校园网");
                            connectByP2P(TARGET_SSID);
                            break;
                        }
                    }

                    if (!found) {
                        logShow("没找到校园网");
                    }
                }

                if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    Log.d("WifiConnectActivity", "NETWORK_STATE_CHANGED_ACTION");
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                    if (info != null&&botton_press) { // 确保 info 不为 null
                        if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                            logShow("连接已断开");
                        } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            logShow("已连接到网络:" + wifiInfo.getSSID());
                        } else {
                            // 根据 NetworkInfo.DetailedState 的不同状态显示消息
                            NetworkInfo.DetailedState state = info.getDetailedState();
                            if (state == NetworkInfo.DetailedState.CONNECTING) {
                                logShow("连接中...");
                            } else if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
                                logShow("正在验证身份信息...");
                            } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                                logShow("正在获取IP地址...");
                            } else if (state == NetworkInfo.DetailedState.FAILED) {
                                logShow("连接失败");
                            }
                        }
                    }
                }
            }
        };

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_STATE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWifiScan();
            } else {
                toastShow("Location permission is required to access Wi-Fi scan results.");
            }
        }
    }

    private void wifi_connect() {
        if (wifiManager.isWifiEnabled()) {
            startWifiScan();
        } else {
            // Prompt the user to enable Wi-Fi
            toastShow("请手动开启WIFI");
            // Optionally, prompt the user to enable Wi-Fi
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        }
    }

    private void startWifiScan() {
        if (wifiManager != null) {
            wifiManager.startScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void toastShow(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    private void logShow(String content){
        TextView textView = new TextView(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(content);
                // 更新UI的代码
                logLayout.addView(textView);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectByP2P(String ssid) {
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .build();

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // do success processing here..
                logShow("wifi链接成功");
                requestLogin();
            }

            @Override
            public void onUnavailable() {
                // do failure processing here..
                logShow("wifi链接失败");
            }
        };

        if(botton_press){
            connectivityManager.requestNetwork(request, networkCallback);
            botton_press=false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectBySug(String ssid) {
        WifiNetworkSuggestion suggestion = new WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                .build();

        List<WifiNetworkSuggestion> suggestionsList = Collections.singletonList(suggestion);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int status = wifiManager.addNetworkSuggestions(suggestionsList);
        Log.d("WifiConnectActivity", String.valueOf(status));

        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // Handle error
        }

        IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION.equals(intent.getAction())) {
                    return;
                }
                // Handle the broadcast
            }
        };

        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void requestLogin(){

        Intent intent = new Intent(WifiConnectActivity.this, SchoolWifiWebActivity.class);
        intent.putExtra("cookie",cookie);
        intent.putExtra("account",current_account);
        intent.putExtra("password",current_password);
        intent.putExtra("yunying",yunying);
        startActivity(intent);
    }

    private void selectTextView(TextView selectView){
        if(selectView.equals(liantong)){
            liantong.setTextColor(getResources().getColor(R.color.icon_blue));
            saveSelection(KEY_WIFI_SELECT, 1);
            seletView = 1;
        }else{
            liantong.setTextColor(getResources().getColor(R.color.black));
        }

        if(selectView.equals(yidong)){
            yidong.setTextColor(getResources().getColor(R.color.icon_blue));
            saveSelection(KEY_WIFI_SELECT, 2);
            seletView = 2;
        }else{
            yidong.setTextColor(getResources().getColor(R.color.black));
        }

        if(selectView.equals(dianxin)){
            dianxin.setTextColor(getResources().getColor(R.color.icon_blue));
            saveSelection(KEY_WIFI_SELECT, 3);
            seletView = 3;
        }else{
            dianxin.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void saveSelection(String key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    private void saveSelection(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
