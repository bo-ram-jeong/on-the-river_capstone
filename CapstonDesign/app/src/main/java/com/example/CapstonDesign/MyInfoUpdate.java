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
import android.widget.GridView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyInfoUpdate extends Activity {
    // fab 관련 변수
    private Boolean isFabOpen = false;

    Dialog dialog_info;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_update);

        EditText et_info1 = findViewById(R.id.et_info1);
        EditText et_info2 = findViewById(R.id.et_info2);
        TextView tv_info_alert = findViewById(R.id.tv_info_alert);
        Button btn_change_info = findViewById(R.id.btn_change_info);
        ImageView btn_back_grey = findViewById(R.id.btn_back_grey);
        btn_back_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog_info = new Dialog(MyInfoUpdate.this);       // Dialog 초기화
        dialog_info.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_info.setContentView(R.layout.dialog_custom_info);             // xml 레이아웃 파일과 연결

        // 버튼: 커스텀 다이얼로그 띄우기
        btn_change_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String et1 = et_info1.getText().toString();
                String et2 = et_info2.getText().toString();

                //숫자만 입력 가능한 정규식
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Matcher matcher1 = pattern.matcher(et1);
                Matcher matcher2 = pattern.matcher(et2);

                if (matcher1.find() && matcher2.find()) {
                    //문자열이 숫자로만 되어있을 때
                    if (et1.getBytes().length <= 3 && et2.getBytes().length <= 3 && et1.getBytes().length >= 2 && et2.getBytes().length >= 2) {
                        int et1_int = Integer.parseInt(et_info1.getText().toString());
                        int et2_int = Integer.parseInt(et_info2.getText().toString());
                        if (et1_int > 10 && et2_int > 10) {
                            showDialog_info(et1_int, et2_int); // 아래 showDialog_logout 함수 호출
                        } else {
                            tv_info_alert.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tv_info_alert.setVisibility(View.VISIBLE);
                    }
                } else {
                    //문자열이 숫자로만 되어있지않을 때
                    tv_info_alert.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    private void showDialog_info(int et1_int, int et2_int) {
        dialog_info.show(); // 다이얼로그 띄우기
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        Button btn_yes = dialog_info.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_info.dismiss();
                Toast.makeText(MyInfoUpdate.this, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                //db update 키 몸무게
                String autoId=UserPreferenceData.getString(MyInfoUpdate.this,"autoID");
                DocumentReference productRef = db.collection("users").document(autoId);
                Map<String, Object> map = new HashMap<>();
                map.put("height", et1_int);
                map.put("weight", et2_int);
                String height = String.valueOf(map.get("height"));
                String weight = String.valueOf(map.get("weight"));
                MyPage.user_height.setText(height+"cm");
                MyPage.user_weight.setText(weight+"kg");
                productRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       // Toast.makeText(MyInfoUpdate.this, "db 수정 완료", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
        dialog_info.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_info.dismiss();
            }
        });
    }

}
