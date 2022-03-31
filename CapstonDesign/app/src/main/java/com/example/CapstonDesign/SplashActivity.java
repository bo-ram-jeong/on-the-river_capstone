package com.example.CapstonDesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            String dsautoID =UserPreferenceData.getString(SplashActivity.this, "autoID");
            String dsemail =UserPreferenceData.getString(SplashActivity.this, "userEmail");
            String dssns =UserPreferenceData.getString(SplashActivity.this, "userSNSType");
            //userNickname

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d("splash",""+dsautoID);
                if (!dsautoID.equals("")){
                    db.collection("users").document(dsautoID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //gggggggsfasfasfasf
                                            Intent i = new Intent(SplashActivity.this, MapActivity.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    } else {
                                        Log.d("splash Error", "get failed with ", task.getException());
                                    }
                                }
                            });
                }else{
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 1500);

    }
}
