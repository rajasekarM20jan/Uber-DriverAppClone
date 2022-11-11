package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
        /*sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        System.out.println("my Mobile"+mobile);*/

        mobile="+916369208301";

        driver.collection("drivers").document(mobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                driverLoc=new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString())
                        ,Double.parseDouble(documentSnapshot.get("longitude").toString()));
                System.out.println("My Mobile Location: "+driverLoc);
                ride.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        HashMap A= (HashMap) documentSnapshot.get("pickUp");
                        pickLoc=new LatLng(Double.parseDouble(A.get("latitude").toString())
                                ,Double.parseDouble(A.get("longitude").toString()));
                        HashMap B= (HashMap) documentSnapshot.get("drop");
                        dropLoc=new LatLng(Double.parseDouble(B.get("latitude").toString())
                                ,Double.parseDouble(B.get("longitude").toString()));

                        getMap();

                    }
                });

            }
        });






    }

    private void getMap() {
        map=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInRide);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                driverOpt=new MarkerOptions().position(driverLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
                pickOpt=new MarkerOptions().position(pickLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
                dropOpt=new MarkerOptions().position(dropLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));

                googleMap.addMarker(driverOpt);
                googleMap.addMarker(pickOpt);
                googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(pickLoc));
                LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(pickLoc).build();
                Point pt=new Point();
                getWindowManager().getDefaultDisplay().getSize(pt);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));

            }
        });

    }
}