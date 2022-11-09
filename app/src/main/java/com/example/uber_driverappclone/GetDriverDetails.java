package com.example.uber_driverappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetDriverDetails extends AppCompatActivity {
    EditText nameEt, lastNameEt,licenseET;
    Button submit;
    FirebaseFirestore userDetails;
    SharedPreferences sp;
    String mobile,longitude,latitude;
    FusedLocationProviderClient flClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_driver_details);
        nameEt=findViewById(R.id.nameET);
        lastNameEt=findViewById(R.id.lastNameET);
        licenseET=findViewById(R.id.licenseET);
        submit=findViewById(R.id.submitDetails);
        userDetails=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        flClient= LocationServices.getFusedLocationProviderClient(GetDriverDetails.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameEt.length()==0){

                }else if(lastNameEt.length()==0){

                }else{
                    System.out.println("qwerty"+nameEt.getText().toString()+"\t"+lastNameEt.getText().toString());
                    String name=nameEt.getText().toString()+" "+lastNameEt.getText().toString();
                    getData(name);
                }
            }
        });
    }
    void getData(String name){


        if(ContextCompat.checkSelfPermission(GetDriverDetails.this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            flClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Geocoder geocoder=new Geocoder(GetDriverDetails.this, Locale.getDefault());
                    if(location!=null){
                        try {
                            List<Address> address=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                            longitude =Double.toString(address.get(0).getLongitude());
                            latitude=Double.toString(address.get(0).getLatitude());
                            System.out.println("MyLocation"+longitude+","+latitude);
                            Map<String,Object> driver=new HashMap();
                            driver.put("name",name);
                            driver.put("mobile",mobile);
                            driver.put("license",String.valueOf(licenseET.getText()));
                            driver.put("longitude",longitude);
                            driver.put("latitude",latitude);
                            driver.put("licenseImage","");
                            driver.put("profileImage","");
                            driver.put("carType","");
                            driver.put("loginStatus","Offline");


                            userDetails.collection("drivers").document(mobile).set(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(GetDriverDetails.this, "Data added", Toast.LENGTH_SHORT).show();
                                    goToGetLicense();
                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });



        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(GetDriverDetails.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(GetDriverDetails.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                ActivityCompat.requestPermissions(GetDriverDetails.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    private void goToGetLicense() {
        Intent i=new Intent(GetDriverDetails.this,GetDriverData.class);
        startActivity(i);
    }
}