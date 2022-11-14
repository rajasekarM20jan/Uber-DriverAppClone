package com.example.uber_driverappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth fauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        fauth= FirebaseAuth.getInstance();

        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fauth.getCurrentUser()!=null){
                    Intent i=new Intent(SplashScreen.this,DashBoard.class);
                    startActivity(i);
                }
                else{
                    Intent i=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
            }
        },3000);



    }
}