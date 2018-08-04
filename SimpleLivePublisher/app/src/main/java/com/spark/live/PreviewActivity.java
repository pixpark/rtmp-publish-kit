package com.spark.live;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spark.live.sdk.engine.ISimpleLiveEngine;
import com.spark.live.sdk.engine.SimpleLivePulisherEngine;

public class PreviewActivity extends Activity implements View.OnClickListener{

    Button btnSwitch;
    ISimpleLiveEngine engine;
    String url;
    long lastTimeFlag = 0;
    boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        SurfaceView preview = (SurfaceView)findViewById(R.id.camera_preview);
        btnSwitch = (Button) findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(this);
        engine = SimpleLivePulisherEngine.getInstance();
        engine.Init(this);
        assert preview != null;
        SurfaceHolder holder = preview.getHolder();
        holder.addCallback(engine);
    }

    @Override
    protected void onResume() {
        if(!isRunning) {
            engine.Start(url);
            isRunning = true;
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSwitch) {
            long curTime = System.currentTimeMillis();
            if (curTime - lastTimeFlag > 5000) {
                lastTimeFlag = curTime;
                engine.SwitchCamera();
            } else {
                Toast.makeText(getApplicationContext(), "点的太欢了，歇会再切换吧", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        engine.Destroy();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
