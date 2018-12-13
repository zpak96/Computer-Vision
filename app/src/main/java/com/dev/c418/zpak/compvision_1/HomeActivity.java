package com.dev.c418.zpak.compvision_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Button effect1 = findViewById(R.id.effect1);
        effect1.setOnClickListener(this);
        Button effect2 = findViewById(R.id.effect2);
        effect2.setOnClickListener(this);
        Button effect3 = findViewById(R.id.effect3);
        effect3.setOnClickListener(this);
        Button effect4 = findViewById(R.id.effect4);
        effect4.setOnClickListener(this);
        Button effect5 = findViewById(R.id.effect5);
        effect5.setOnClickListener(this);
        Button effect6 = findViewById(R.id.effect6);
        effect6.setOnClickListener(this);
        Button effect7 = findViewById(R.id.effect7);
        effect7.setOnClickListener(this);
        Button default_camera = findViewById(R.id.default_camera);
        default_camera.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.default_camera:
                callEffect(0);
                break;
            case R.id.effect1:
                callEffect(1);
                break;
            case R.id.effect2:
                callEffect(2);
                break;
            case R.id.effect3:
                callEffect(3);
                break;
            case R.id.effect4:
                callEffect(4);
                break;
            case R.id.effect5:
                callEffect(5);
                break;
            case R.id.effect6:
                callEffect(6);
                break;
            case R.id.effect7:
                callEffect(7);
                break;
        }

    }

    public void callEffect(int val) {
        Intent CAM = new Intent(this, MainActivity_show_camera.class);
        Bundle extras = new Bundle();
        extras.putInt("EFFECT_NUMBER", val);

        CAM.putExtras(extras);
        startActivity(CAM);
    }
}
