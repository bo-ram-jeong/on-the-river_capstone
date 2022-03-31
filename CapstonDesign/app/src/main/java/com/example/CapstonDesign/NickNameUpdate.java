package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NickNameUpdate extends Activity {

    Dialog dialog_nickname;
    TextView tv_nickname_alert;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nickname_update);

        TextView tv_nickname1 = findViewById(R.id.tv_nickname1);
        EditText et_nickname2 = findViewById(R.id.et_nickname2);
        tv_nickname_alert = findViewById(R.id.tv_nickname_alert);
        Button btn_change_nickname = findViewById(R.id.btn_change_nickname);
        ImageView btn_back_grey = findViewById(R.id.btn_back_grey);
        btn_back_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String autoId=UserPreferenceData.getString(NickNameUpdate.this,"autoID");
        DocumentReference productRef = db.collection("users").document(autoId);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Map<String, Object> map = document.getData();
                    String nickname = String.valueOf(map.get("nickname"));
                    tv_nickname1.setText(nickname);
                }
            }
        });

        dialog_nickname = new Dialog(NickNameUpdate.this);       // Dialog 초기화
        dialog_nickname.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_nickname.setContentView(R.layout.dialog_custom_nickname);             // xml 레이아웃 파일과 연결

        btn_change_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tv1 = tv_nickname1.getText().toString();
                String et2 = et_nickname2.getText().toString();

                //한글,영문,숫자만 입력 가능한 정규식
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]*$");
                Matcher matcher2 = pattern.matcher(et2);

                if (et2.equals("")) {
                    tv_nickname_alert.setText("입력되었는지 확인해주세요");
                    tv_nickname_alert.setVisibility(View.VISIBLE);
                }
                if (et2.length() <= 8 && et2.length() >= 2) {
                    if (matcher2.find()) {
                        //문자열이 한글,영문,숫자로만 되어있을 때
                        showDialog_nickname(et2); // 아래 showDialog_logout 함수 호출
                    } else {
                        //문자열이 한글,영문,숫자로만 되어있지않을 때
                        tv_nickname_alert.setVisibility(View.VISIBLE);
                    }
                } else {
                    tv_nickname_alert.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void showDialog_nickname(String et2) {
        dialog_nickname.show(); // 다이얼로그 띄우기
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        Button btn_yes = dialog_nickname.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_nickname.dismiss();
                Toast.makeText(NickNameUpdate.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();

                //db update 닉네임
                String autoId=UserPreferenceData.getString(NickNameUpdate.this,"autoID");
                DocumentReference productRef = db.collection("users").document(autoId);
                Map<String, Object> map = new HashMap<>();
                map.put("nickname", et2);
                String nickname = String.valueOf(map.get("nickname"));
                MyPage.user_nickname.setText(nickname);
                productRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       // Toast.makeText(NickNameUpdate.this, "db 수정 완료", Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
            }
        });
        dialog_nickname.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_nickname.dismiss();
            }
        });
    }

}
