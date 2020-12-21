package com.samet.sauwallpaper;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(ActivitySplash.this,HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },3000); // Sleep 3 second


        }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("Orientation", "LANDSCAPE MODE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("Orientation", "PORTRAIT MODE");
        }
    }
}