package com.example.newapp.incubationprograms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.emptyclassrooms.EmptyBuildingsActivity;
import com.example.newapp.emptyclassrooms.EmptyClassroomsActivity;
import com.example.newapp.utils.NetworkUtils;
import com.example.newapp.utils.ToastUtils;

public class IncubationProgramsActivity extends AppCompatActivity {
    private Context context=this;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incubationprogramsview);

        ImageView exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout incubation1_linearlayout =findViewById(R.id.incubation1);
        LinearLayout incubation2_linearlayout =findViewById(R.id.incubation2);
        incubation1_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkConnected(context)){
                    //网络已链接
                    Intent intent =new Intent(IncubationProgramsActivity.this, IncubationProgramsChildActivity.class);
                    intent.putExtra("incubation_data", "incubation1");
                    startActivity(intent);
                    Log.d("incubation", "onClick: ");
                }else{
                    ToastUtils.showToastShort(context,"网络去开小差去了:)");
                }
            }
        });
        incubation2_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkConnected(context)){
                    //网络已链接
                    Intent intent =new Intent(IncubationProgramsActivity.this, IncubationProgramsChildActivity.class);
                    intent.putExtra("incubation_data", "incubation2");
                    startActivity(intent);
                    Log.d("incubation", "onClick: ");
                }else{
                    ToastUtils.showToastShort(context,"网络去开小差去了:)");
                }
            }
        });
    }

}
