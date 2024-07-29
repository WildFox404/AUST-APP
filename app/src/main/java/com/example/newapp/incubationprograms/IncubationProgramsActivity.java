package com.example.newapp.incubationprograms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;

public class IncubationProgramsActivity extends AppCompatActivity {

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
                Intent intent =new Intent(IncubationProgramsActivity.this, IncubationProgramsChildActivity.class);
                intent.putExtra("incubation_data", "incubation1");
                startActivity(intent);
                Log.d("incubation", "onClick: ");
            }
        });
        incubation2_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(IncubationProgramsActivity.this, IncubationProgramsChildActivity.class);
                intent.putExtra("incubation_data", "incubation2");
                startActivity(intent);
                Log.d("incubation", "onClick: ");
            }
        });
    }

}
