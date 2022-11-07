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

    Button button,submitLicense;
    SharedPreferences sp;
    String mobile;
    FirebaseFirestore driverData;
    ActivityResultLauncher<Intent> activityLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_driver_data);
        driverData=FirebaseFirestore.getInstance();
        button=findViewById(R.id.button234);
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
                submitLicense.setVisibility(View.VISIBLE);

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
        /*Intent i=new Intent(GetDriverDetails.this,DashBoard.class);
        startActivity(i);*/
        Toast.makeText(this, "Leads to dash board", Toast.LENGTH_SHORT).show();
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