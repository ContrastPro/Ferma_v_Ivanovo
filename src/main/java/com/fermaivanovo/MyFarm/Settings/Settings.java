package com.fermaivanovo.MyFarm.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fermaivanovo.BuildConfig;
import com.fermaivanovo.MyFarm.UserProfile.SignInUp;
import com.fermaivanovo.MyFarm.UserProfile.UserProfile;
import com.fermaivanovo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;


public class Settings extends AppCompatActivity {

    private TextView versionNameTV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_settings);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Настройки");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        displayCurrentVersionName();
    }

    private void displayCurrentVersionName() {
        versionNameTV = findViewById(R.id.versionNameTV);
        String versionName = BuildConfig.VERSION_NAME;
        versionNameTV.setText(versionName);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_contactUs:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {"info@dostavkavodyodessa.com.ua"};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Отзывы/Предложения");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Выберите почтовый клиент"));
                break;
            case R.id.bt_About:

                break;
            case R.id.bt_Admin:
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), SignInUp.class));
                    finish();
                }
                break;
        }
    }
}
