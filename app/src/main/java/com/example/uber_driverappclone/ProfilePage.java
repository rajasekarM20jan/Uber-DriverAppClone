package com.example.uber_driverappclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class ProfilePage extends AppCompatActivity {
    TextView nameText;
    FirebaseFirestore driverDetails;
    ImageView profilePic;
    ActivityResultLauncher<Intent> dpGetter;
    SharedPreferences sp;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");
        nameText=findViewById(R.id.driverName);
        profilePic=findViewById(R.id.profilePic);

        driverDetails=FirebaseFirestore.getInstance();
        driverDetails.collection("drivers").document(mobile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("qwertyuiop : "+task.getResult());
                    DocumentSnapshot doc= task.getResult();

                    System.out.println("qwertyuiop : "+doc.get("name"));
                    String name= (String) doc.get("name");
                    nameText.setText(name);

                    String image=(String) doc.get("profileImage");

                    if(image.equals("")||image.equals("null")){ }
                    else{
                        Uri imgUri=Uri.parse(image);
                        profilePic.setImageURI(imgUri);
                    }

                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dpGetter.launch(i);
                System.out.println("executed00");
            }
        });
        System.out.println("executed01");
        dpGetter=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                System.out.println("executed02");
                System.out.println("qwertyuiop1234 : "+result.getResultCode());
                if(result.getResultCode()==-1){
                    Bundle myBundle=result.getData().getExtras();
                    Uri imageUri;
                    Bitmap imgBitmap=(Bitmap) myBundle.get("data");
                    WeakReference<Bitmap> wr=new WeakReference<>(Bitmap.createScaledBitmap(imgBitmap,
                                    imgBitmap.getWidth(),imgBitmap.getHeight(),false)
                            .copy(Bitmap.Config.RGB_565,true));
                    Bitmap am=wr.get();
                    imageUri=saveImage(am,ProfilePage.this);
                    System.out.println("imageUri"+imageUri);
                    String uriImage=String.valueOf(imageUri);
                    System.out.println("mobile"+mobile);
                    System.out.println("imageUri"+uriImage);

                    driverDetails.collection("drivers").document(mobile).update("profileImage",uriImage);

                    driverDetails.collection("drivers").document(mobile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot doc= task.getResult();
                                String image=(String) doc.get("profileImage");
                                if(image.equals("")||image.equals("null")){ }
                                else{
                                    Uri imgUri=Uri.parse(image);
                                    profilePic.setImageURI(imgUri);
                                }
                            }
                        }
                    });
                }



            }
        });

        System.out.println("qwertyuiop : "+driverDetails.collection("drivers").document(mobile).get());

    }

    private Uri saveImage(Bitmap am, Context context) {
        Uri uri=null;
        File images=new File(context.getCacheDir(),"images");
        try{
            System.out.println("qwertyuiop : "+uri);
            images.mkdirs();
            System.out.println("qwertyuiop2 : "+uri);
            File file=new File(images,"IMG"+System.currentTimeMillis()+".jpg");
            System.out.println("qwertyuiop3 : "+uri);
            FileOutputStream fos=new FileOutputStream(file);
            System.out.println("qwertyuiop4 : "+uri);
            am.compress(Bitmap.CompressFormat.JPEG,100,fos);
            System.out.println("qwertyuiop5 : "+uri);
            fos.flush();
            fos.close();

            uri= FileProvider.getUriForFile(context.getApplicationContext(),
                    "com.example.uber_driverappclone"+".provider",file);
            System.out.println("qwertyuiop66 : "+uri);
            System.out.println("imageUri@ : "+uri);

        }catch (Exception e){
            System.out.println("qwerty"+e.getMessage());
        }

        return uri;
    }
}