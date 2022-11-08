package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoard extends AppCompatActivity {
    Button menuOpener,goOnline;
    NavigationView myNav;
    FirebaseFirestore driverData;
    SharedPreferences sp;
    String mobile;
    FusedLocationProviderClient flClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        myNav=findViewById(R.id.myNav);
        menuOpener=findViewById(R.id.menuOpener);
        goOnline=findViewById(R.id.goOnline);
        flClient= LocationServices.getFusedLocationProviderClient(DashBoard.this);
        driverData=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        System.out.println("qqwertyytuiop  : "+mobile);

        menuOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myNav.setVisibility(View.VISIBLE);
                menuOpener.setVisibility(View.INVISIBLE);
            }
        });

        myNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeButton:{
                        Toast.makeText(DashBoard.this, "Home Page", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.profileButton:{
                        Intent profile=new Intent(DashBoard.this,ProfilePage.class);
                        startActivity(profile);
                        break;
                    }
                    case R.id.earningsButton:{
                        /*Intent earnings=new Intent(DashBoard.this,EarningsPage.class);
                        startActivity(earnings);*/
                        Toast.makeText(DashBoard.this, "Earnings Page", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.settingsButton:{
                        Toast.makeText(DashBoard.this, "Settings Page", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return true;
            }
        });

        goOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert=new AlertDialog.Builder(DashBoard.this);
                alert.setMessage("Are You Sure About going Online?");
                alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        driverData.collection("drivers").document(mobile).update("loginStatus","Online");
                        Toast.makeText(DashBoard.this, "You're Online", Toast.LENGTH_SHORT).show();

                    }
                });
                alert.show();

            }
        });

        updateMap();


    }

    private void updateMap() {
        if(ContextCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            flClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Geocoder geocoder=new Geocoder(DashBoard.this, Locale.getDefault());
                    if(location!=null){
                        try {
                            List<Address> address=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                            String longitude =Double.toString(address.get(0).getLongitude());
                            String latitude=Double.toString(address.get(0).getLatitude());
                            System.out.println("MyLocation"+longitude+","+latitude);
                            driverData.collection("drivers").document(mobile).update("latitude",latitude);
                            driverData.collection("drivers").document(mobile).update("longitude",longitude);
                            getTimer();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(DashBoard.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(DashBoard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                ActivityCompat.requestPermissions(DashBoard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    private void getTimer() {
        Handler handler=new Handler();
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                updateMap();
            }
        },5000);
    }


    @Override
    public void onBackPressed() {
        if(myNav.getVisibility()==View.VISIBLE){
            myNav.setVisibility(View.INVISIBLE);
            menuOpener.setVisibility(View.VISIBLE);
        }else{
            AlertDialog.Builder exit=new AlertDialog.Builder(DashBoard.this);
            exit.setMessage("Are you Sure about to exit the application");
            exit.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            exit.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });
            exit.show();
        }
    }
}