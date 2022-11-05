package com.example.uber_driverappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {
    FirebaseAuth phoneAuth;
    FirebaseFirestore userVerify;
    EditText pinEt;
    Button verify;
    String vID;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        pinEt=findViewById(R.id.pinET);
        verify=findViewById(R.id.verify);
        phoneAuth= FirebaseAuth.getInstance();
        Intent intent=getIntent();
        phone=intent.getStringExtra("driver'sPhone");
        sendCode(phone);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinEt.length()!=0){
                    verifyCode(pinEt.getText().toString());
                }
                else{
                    pinEt.setError("Invalid Pin");
                }
            }
        });
    }

    void sendCode(String phoneNumber){
        PhoneAuthOptions opt= PhoneAuthOptions.newBuilder(phoneAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneCallback)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(opt);
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneCallback=
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    vID=s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code=phoneAuthCredential.getSmsCode();
                    if (code!=null){
                        pinEt.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    e.getMessage();
                }
            };
    void verifyCode(String code){
        PhoneAuthCredential cred=PhoneAuthProvider.getCredential(vID,code);
        signIn(cred);
    }
    void signIn(PhoneAuthCredential cred){
        phoneAuth.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
                    SharedPreferences.Editor editor= sp.edit();
                    editor.putString("mobile",phone);
                    editor.commit();
                    checkUser();

                }else{
                    AlertDialog.Builder alert= new AlertDialog.Builder(OTPVerification.this);
                    alert.setMessage("Verification Failed");
                    alert.show();
                }
            }
        });
    }
    void checkUser(){
        userVerify=FirebaseFirestore.getInstance();
        try{
            userVerify.collection("drivers").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        System.out.println("qwertyuiop : " + task.getResult());
                        try {
                            DocumentSnapshot doc = task.getResult();
                            System.out.println("qwertyuiop : " + doc.get("mobile"));
                            String mobile = (String) doc.get("mobile");
                            if (mobile.equals(phone)) {
                                getToDashboard();
                            }
                        }catch(Exception e){
                            sendToDetailsPage();
                        }

                    }else{
                        System.out.println("qwertyuiop : no data found");
                    }
                }
            });



        }catch (Exception e){
            System.out.println("qwertyuiop  : No user found with id"+phone);
        }

    }

    void getToDashboard(){
        /*Intent i=new Intent(OTPVerification.this,DashBoard.class);
        startActivity(i);*/
        Toast.makeText(this, "Leads to Dashboard", Toast.LENGTH_SHORT).show();
    }

    void sendToDetailsPage(){
        Intent i=new Intent(OTPVerification.this,GetDriverDetails.class);
        startActivity(i);
    }
}