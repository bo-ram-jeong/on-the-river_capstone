package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MyPage extends Activity {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView user_email;
    static TextView user_nickname;
    static TextView user_height;
    static TextView user_weight;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        user_email = findViewById(R.id.user_email);
        user_nickname = findViewById(R.id.user_nickname);
        user_height = findViewById(R.id.user_height);
        user_weight = findViewById(R.id.user_weight);

        ConstraintLayout btn_myactivitypage = findViewById(R.id.btn_myactivitypage);
        ConstraintLayout btn_mybadgepage = findViewById(R.id.btn_mybadgepage);
        ConstraintLayout btn_arrankingpage = findViewById(R.id.btn_arrankingpage);
        ConstraintLayout btn_settingpage = findViewById(R.id.btn_settingpage);

        btn_myactivitypage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_myactivitypage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_myactivitypage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(MyPage.this, MyActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });
        btn_mybadgepage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_mybadgepage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_mybadgepage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(MyPage.this, MyBadge.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });
        btn_arrankingpage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_arrankingpage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_arrankingpage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(MyPage.this, ARRanking.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });
        btn_settingpage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_settingpage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_settingpage.setBackground(ContextCompat.getDrawable(MyPage.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(MyPage.this, Setting.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

        String autoId = UserPreferenceData.getString(MyPage.this, "autoID");
        DocumentReference productRef = db.collection("users").document(autoId);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        String email = String.valueOf(map.get("email"));
                        String nickname = String.valueOf(map.get("nickname"));
                        String height = String.valueOf(map.get("height"));
                        String weight = String.valueOf(map.get("weight"));
                        user_email.setText(email);
                        user_nickname.setText(nickname);
                        user_height.setText(height + "cm");
                        user_weight.setText(weight + "kg");
                    } else {
                        Log.d(TAG, "Error", task.getException());
                    }

                } else {

                }
            }
        });
    }//onCreate

    public void onBackPressed() { //뒤로가기 내장버튼 애니메이션 추가
        Intent intent = new Intent(MyPage.this, MapActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(R.anim.horizon_exit2, R.anim.horizon_enter2);
        finish();
    }//onBackPressed

}//activity
