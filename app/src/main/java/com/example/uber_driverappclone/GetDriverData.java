package com.example.uber_driverappclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class GetDriverData extends AppCompatActivity {

    Button button,submitLicense,carType;
    SharedPreferences sp;
    String mobile;
    LinearLayout linear123;
    RadioGroup radioCarType;
    RadioButton intercity,xlIntercity;
    FirebaseFirestore driverData;
    ActivityResultLauncher<Intent> activityLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_driver_data);
        driverData=FirebaseFirestore.getInstance();
        button=findViewById(R.id.button234);
        carType=findViewById(R.id.carType);
        linear123=findViewById(R.id.linear123);
        radioCarType=findViewById(R.id.radioCarType);
        intercity=findViewById(R.id.intercity);
        xlIntercity=findViewById(R.id.xlIntercity);
        submitLicense=findViewById(R.id.submitLicense);
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityLaunch.launch(i);
            }
        });

        activityLaunch=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Bundle myBundle=result.getData().getExtras();
                Uri imageUri;
                Bitmap imgBitmap=(Bitmap) myBundle.get("data");
                WeakReference<Bitmap> wr=new WeakReference<>(Bitmap.createScaledBitmap(imgBitmap,
                        imgBitmap.getWidth(),imgBitmap.getHeight(),false)
                        .copy(Bitmap.Config.RGB_565,true));
                Bitmap am=wr.get();
                imageUri=saveImage(am,GetDriverData.this);
                System.out.println("imageUri : "+imageUri);
                String uriImage=String.valueOf(imageUri);
                Map<String,Object> driver=new HashMap();
                driver.put("licenseImage",uriImage);
                System.out.println("mobile"+mobile);
                driverData.collection("drivers")
                        .document(mobile).update("licenseImage",uriImage);
                Toast.makeText(GetDriverData.this, "Your License is added to Our Database", Toast.LENGTH_SHORT).show();
                button.setClickable(false);
                carType.setVisibility(View.VISIBLE);

            }
        });

        carType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button.setVisibility(View.INVISIBLE);
                carType.setVisibility(View.INVISIBLE);
                linear123.setVisibility(View.VISIBLE);

            }
        });

        radioCarType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               switch(i){
                   case R.id.intercity:{
                       driverData.collection("drivers")
                               .document(mobile).update("carType","intercity");
                       Toast.makeText(GetDriverData.this, "Type Of Car you Selected is HatchBack", Toast.LENGTH_SHORT).show();
                       button.setClickable(false);
                       carType.setClickable(false);
                       button.setVisibility(View.VISIBLE);
                       carType.setVisibility(View.VISIBLE);
                       linear123.setVisibility(View.INVISIBLE);
                       submitLicense.setVisibility(View.VISIBLE);
                       break;
                   }
                   case R.id.xlIntercity:{
                       driverData.collection("drivers")
                               .document(mobile).update("carType","xlIntercity");
                       Toast.makeText(GetDriverData.this, "Type Of Car you Selected is SEDAN or SUV", Toast.LENGTH_SHORT).show();
                       button.setClickable(false);
                       carType.setClickable(false);
                       button.setVisibility(View.VISIBLE);
                       carType.setVisibility(View.VISIBLE);
                       linear123.setVisibility(View.INVISIBLE);
                       submitLicense.setVisibility(View.VISIBLE);
                       break;
                   }
               }
            }
        });

        submitLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getToDashBoard();
            }
        });

    }

    private void getToDashBoard() {
        Intent i=new Intent(GetDriverData.this,DashBoard.class);
        startActivity(i);
    }

    private Uri saveImage(Bitmap imgBitmap, Context context) {
        Uri uri=null;
        File images=new File(context.getCacheDir(),"images");
        try{
            images.mkdirs();
            File file=new File(images,"IMG"+System.currentTimeMillis()+".jpg");
            FileOutputStream fos=new FileOutputStream(file);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
            uri= FileProvider.getUriForFile(context.getApplicationContext(),
                    "com.example.uber_driverappclone"+".provider",file);
            System.out.println("imageUri@ : "+uri);

        }catch (Exception e){
            e.printStackTrace();
        }

        return uri;
    }
}