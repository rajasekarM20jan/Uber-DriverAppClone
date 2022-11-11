package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RidePage extends AppCompatActivity {
    SupportMapFragment map;
    String rideID;
    SharedPreferences sp;
    FirebaseFirestore driver,ride;
    String mobile;
    LatLng driverLoc,pickLoc,dropLoc;
    MarkerOptions driverOpt,pickOpt,dropOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_page);
        Intent i=getIntent();
        rideID=i.getStringExtra("rideID");
        driver=FirebaseFirestore.getInstance();
        ride=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        driver.collection("drivers").document(mobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                driverLoc=new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString())
                        ,Double.parseDouble(documentSnapshot.get("longitude").toString()));
            }
        });










        map=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInRide);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                driverOpt=new MarkerOptions().position(driverLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
            }
        });
    }
}