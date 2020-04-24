package com.spark.live;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private String rtmpUrl = "rtmp://192.168.0.166/live/livedemo";

    // the bitrate in kbps.
    private int vbitrate_kbps = 800;

    // settings storage
    private SharedPreferences sp;
    private static final String TAG = "SimpleLivePublisher";
    private Button btnPublish = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("SrsPublisher", MODE_PRIVATE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // restore data.
        rtmpUrl = sp.getString("FLV_URL", rtmpUrl);
        vbitrate_kbps = sp.getInt("VBITRATE", vbitrate_kbps);
        Log.i(TAG, String.format("initialize flv url to %s, vbitrate=%dkbps", rtmpUrl, vbitrate_kbps));

        // initialize url.
        final EditText efu = (EditText) findViewById(R.id.rtmp_url);
        efu.setText(rtmpUrl);
        efu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String fu = efu.getText().toString();
                if (fu == rtmpUrl || fu.isEmpty()) {
                    return;
                }

                rtmpUrl = fu;
                Log.i(TAG, String.format("flv url changed to %s", rtmpUrl));

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("FLV_URL", rtmpUrl);
                editor.commit();
            }
        });

        final EditText evb = (EditText) findViewById(R.id.vbitrate);
        evb.setText(String.format("%dkbps", vbitrate_kbps));
        evb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int vb = Integer.parseInt(evb.getText().toString().replaceAll("kbps", ""));
                if (vb == vbitrate_kbps) {
                    return;
                }

                vbitrate_kbps = vb;
                Log.i(TAG, String.format("video bitrate changed to %d", vbitrate_kbps));

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("VBITRATE", vbitrate_kbps);
                editor.commit();
            }
        });

        // for camera, @see https://developer.android.com/reference/android/hardware/Camera.html
        btnPublish = (Button) findViewById(R.id.capture);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent publishIntent = new Intent(MainActivity.this, PreviewActivity.class);
                publishIntent.putExtra("url", rtmpUrl);
                publishIntent.putExtra("vBitrate", vbitrate_kbps);
                startActivity(publishIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        final Button btn = (Button) findViewById(R.id.capture);
        btn.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}