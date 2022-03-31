package com.example.CapstonDesign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PWUpdate extends Activity {

    Dialog dialog_pw;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pw_update);


        EditText et_pw1 = findViewById(R.id.et_pw1);
        EditText et_pw2 = findViewById(R.id.et_pw2);
        TextView tv_pw_alert = findViewById(R.id.tv_pw_alert);
        Button btn_change_pw = findViewById(R.id.btn_change_pw);
        ImageView btn_back_grey = findViewById(R.id.btn_back_grey);
        btn_back_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog_pw = new Dialog(PWUpdate.this);       // Dialog 초기화
        dialog_pw.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_pw.setContentView(R.layout.dialog_custom_pw);             // xml 레이아웃 파일과 연결

        // 버튼: 커스텀 다이얼로그 띄우기 및 예외처리
        btn_change_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String et1 = et_pw1.getText().toString();
                String et2 = et_pw2.getText().toString();

                //영문,숫자만 입력 가능한 정규식
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
                Matcher matcher1 = pattern.matcher(et1);
                Matcher matcher2 = pattern.matcher(et2);

                if (matcher1.find() && matcher2.find()) {
                    //문자열이 영문,숫자로만 되어있을 때
                    if (et1.getBytes().length <= 12 && et2.getBytes().length <= 12 && et1.getBytes().length >= 8 && et2.getBytes().length >= 8) {
                        if (et1.equals(et2)) {
                            showDialog_pw(et2); // 아래 showDialog_logout 함수 호출
                        } else {
                            tv_pw_alert.setText("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
                            tv_pw_alert.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tv_pw_alert.setVisibility(View.VISIBLE);
                    }
                } else {
                    //문자열이 영문,숫자로만 되어있지않을 때
                    tv_pw_alert.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void showDialog_pw(String et2) {
        dialog_pw.show(); // 다이얼로그 띄우기
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        Button btn_yes = dialog_pw.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db update 비번
                dialog_pw.dismiss();
                Toast.makeText(PWUpdate.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                String autoId=UserPreferenceData.getString(PWUpdate.this,"autoID");
                DocumentReference productRef = db.collection("users").document(autoId);
                Map<String, Object> map = new HashMap<>();
                map.put("password", et2);
                productRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       // Toast.makeText(PWUpdate.this, "db 수정 완료", Toast.LENGTH_SHORT).show();
                    }
                });

                finish();

            }
        });
        dialog_pw.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_pw.dismiss();
            }
        });
    }

}
