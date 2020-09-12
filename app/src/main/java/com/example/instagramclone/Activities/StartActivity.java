package com.example.instagramclone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    TextView splashText, splashSubText;
    Button register, login, next;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        splashSubText = findViewById(R.id.splashSubText);
        splashText = findViewById(R.id.splashText);
        register = findViewById(R.id.splashRegister);
        login = findViewById(R.id.splashLogin);
        next = findViewById(R.id.splashnext);
        linearLayout = findViewById(R.id.lin);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashSubText.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                splashText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.xslide_up));
                splashSubText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                linearLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.yslide_to_left));

            }
        }, 1300);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

    }

}
