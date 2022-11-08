package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

public class DashBoard extends AppCompatActivity {
    Button menuOpener,goOnline;
    NavigationView myNav;
    FirebaseFirestore driverData;
    SharedPreferences sp;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        myNav=findViewById(R.id.myNav);
        menuOpener=findViewById(R.id.menuOpener);
        goOnline=findViewById(R.id.goOnline);
        driverData=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");

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
                        Toast.makeText(DashBoard.this, "Profile Page", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.earningsButton:{
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