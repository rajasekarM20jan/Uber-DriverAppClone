package com.example.uber_driverappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText phoneEt;
    Button generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneEt=findViewById(R.id.phoneET);
        generate=findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneEt.length()!=10){
                    phoneEt.setError("Invalid Number");
                }else{
                    String phone="+91"+phoneEt.getText().toString();
                    send(phone);
                }
            }
        });

        //making request for location access
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

    }
    void send(String phone){
        Intent i=new Intent(MainActivity.this,OTPVerification.class);
        i.putExtra("driver'sPhone",phone);
        startActivity(i);
    }



}