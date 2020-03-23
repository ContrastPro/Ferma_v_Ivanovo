package com.fermaivanovo.MyFarm.StartActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fermaivanovo.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView welcomeIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NO TITLE
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.a_welcome);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        welcomeIV = findViewById(R.id.welcomeIV);
        checkForConnection();
    }

    private void checkForConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            //Start Animation
            welcomeIV.setVisibility(View.VISIBLE);
            Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_animation);
            welcomeIV.startAnimation(welcomeAnimation);
            startMainActivity();
        } else {
            welcomeIV.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
            builder.setTitle("Нет доступа к Интернету");
            builder.setMessage("Проверте подключение к Wi-Fi или сотовой сети и повторите попытку");
            builder.setIcon(R.drawable.ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Повторить попытку", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    checkForConnection();
                }
            });
            builder.create().show();
        }
    }

    private void startMainActivity() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        }, 2000);
    }
}
