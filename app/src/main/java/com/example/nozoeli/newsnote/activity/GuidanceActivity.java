package com.example.nozoeli.newsnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.nozoeli.newsnote.R;

/**
 * Created by nozoeli on 16-4-13.
 */
public class GuidanceActivity extends Activity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acitivity_guidance);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goToMain = new Intent(GuidanceActivity.this, MainActivity.class);
                startActivity(goToMain);
                finish();
            }
        }, 2000);



    }
}
