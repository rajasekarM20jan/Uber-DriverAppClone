package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.Locale;

public class RidePage extends AppCompatActivity {
    SupportMapFragment map;
    String rideID;
    Button navigateButton,reachedPickup,startRide,endRide;
    SharedPreferences sp;
    FirebaseFirestore driver,ride;
    String mobile;
    LatLng driverLoc,pickLoc,dropLoc;
    MarkerOptions driverOpt,pickOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_page);
        SharedPreferences myRide=getSharedPreferences("myRide",MODE_PRIVATE);
        rideID=myRide.getString("rideID","noRides");
        navigateButton=findViewById(R.id.navigateButton);
        reachedPickup=findViewById(R.id.reachedPickup);
        startRide=findViewById(R.id.startRide);
        endRide=findViewById(R.id.endRide);
        driver=FirebaseFirestore.getInstance();
        ride=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        System.out.println("my Mobile"+mobile);


        /*mobile="+916369208301";*/



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

                        getMap(driverLoc,pickLoc);




                        reachedPickup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Location startPoint=new Location("LocA");
                                startPoint.setLatitude(driverLoc.latitude);
                                startPoint.setLongitude(driverLoc.longitude);
                                Location endPoint=new Location("LocB");
                                endPoint.setLatitude(pickLoc.latitude);
                                endPoint.setLongitude(pickLoc.longitude);

                                int distance=(int) startPoint.distanceTo(endPoint);

                                if(distance<=50){
                                    ride.collection("rides").document(rideID)
                                            .update("rideStatus","2");
                                    reachedPickup.setVisibility(View.INVISIBLE);
                                    startRide.setVisibility(View.VISIBLE);
                                    getMap(driverLoc,pickLoc);
                                }else{
                                    Toast.makeText(RidePage.this
                                            , "You Must reach the pick up Location First."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        startRide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder alert=new AlertDialog.Builder(RidePage.this);
                                alert.setMessage("Please Confirm that customer is onBoarded or Not.");
                                alert.setCancelable(false);
                                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ride.collection("rides").document(rideID)
                                                .update("rideStatus","3");
                                        getMap(driverLoc,dropLoc);
                                        startRide.setVisibility(View.INVISIBLE);
                                        endRide.setVisibility(View.VISIBLE);

                                    }
                                });
                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(RidePage.this, "Do not click on Start Ride before the Rider Arrives", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        endRide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Location startPoint=new Location("LocA");
                                startPoint.setLatitude(driverLoc.latitude);
                                startPoint.setLongitude(driverLoc.longitude);
                                Location endPoint=new Location("LocB");
                                endPoint.setLatitude(dropLoc.latitude);
                                endPoint.setLongitude(dropLoc.longitude);

                                int distance=(int) startPoint.distanceTo(endPoint);

                                if(distance<=50){
                                    ride.collection("rides").document(rideID)
                                            .update("rideStatus","4");
                                    getMap(driverLoc,dropLoc);
                                    AlertDialog.Builder alert=new AlertDialog.Builder(RidePage.this);
                                    alert.setMessage("Please Confirm that you have received payment status from the customer.");
                                    alert.setCancelable(false);
                                    alert.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(RidePage.this, "Thank You! For the Confirmation", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(RidePage.this
                                            , "You Must reach the Drop up Location First."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }
        });






    }

    private void getMap(LatLng locA,LatLng locB) {
        map=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInRide);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                driverOpt=new MarkerOptions().position(locA)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
                pickOpt=new MarkerOptions().position(locB)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));


                googleMap.addMarker(driverOpt);
                googleMap.addMarker(pickOpt);
                googleMap.addPolyline(new PolylineOptions().add(locA).add(locB));
                LatLngBounds bounds=new LatLngBounds.Builder().include(locA).include(locB).build();
                Point pt=new Point();
                getWindowManager().getDefaultDisplay().getSize(pt);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));

                navigateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String mapsUri="http://maps.google.com/maps?saddr="+locA.latitude+","+locA.longitude+"&daddr="
                                +locB.latitude+","+locB.longitude;
                        Intent intent = new Intent( Intent.ACTION_VIEW,
                                Uri.parse(mapsUri));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });

            }
        });




    }
}