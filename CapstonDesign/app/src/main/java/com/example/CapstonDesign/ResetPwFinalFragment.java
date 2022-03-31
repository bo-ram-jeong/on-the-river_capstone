package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ResetPwFinalFragment extends Fragment {
    Button btn_change_pw;
    ImageFilterView btn_back_grey;
    EditText et_pw1, et_pw2;
    TextView tv_pw_alert;
    Boolean pswChecked = false, pswChecked2 = false;
    String emailForReset ="";
    String autoIDForReset = "";
    String pwdString = "" ;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ResetPwFinalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null) {
            emailForReset = getArguments().getString("emailForReset");
            // 전달한 key 값
            autoIDForReset = getArguments().getString("autoIDForReset");
            // 전달한 key 값
        }

        View view = inflater.inflate(R.layout.pw_update, container, false);
        btn_back_grey = view.findViewById(R.id.btn_back_grey);
        btn_change_pw = view.findViewById(R.id.btn_change_pw);
        et_pw1 = view.findViewById(R.id.et_pw1);
        et_pw2 = view.findViewById(R.id.et_pw2);
        tv_pw_alert = view.findViewById(R.id.tv_pw_alert);

        btn_back_grey.setVisibility(View.INVISIBLE);

        btn_change_pw.setEnabled(false);
        btn_change_pw.setBackgroundResource(R.drawable.radiusgray);
        btn_change_pw.setOnClickListener(change_PwButtonClickListener);

        et_pw1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9]).{8,12}$", s))
                {
                    tv_pw_alert.setText("8~12자리 영문 숫자가 아닙니다.");
                    tv_pw_alert.setVisibility(View.VISIBLE);
                    et_pw2.setEnabled(false);
                    pswChecked = false;
                }else {
                    tv_pw_alert.setVisibility(View.INVISIBLE);
                    //정규표현식 통과
                    pwdString = et_pw1.getText().toString();
                    et_pw2.setEnabled(true);
                    pswChecked = true;

                    if(pswChecked && pswChecked2){
                        btn_change_pw.setEnabled(true);
                        //sign.setBackgroundColor(Color.parseColor("#3498DB"));
                        btn_change_pw.setBackgroundResource(R.drawable.radius);
                    }else{
                        btn_change_pw.setEnabled(false);
                        btn_change_pw.setBackgroundResource(R.drawable.radiusgray);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                pwdString = et_pw1.getText().toString();
            }

        });

        et_pw2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!pwdString.equals(s.toString()))
                {
                    tv_pw_alert.setText("비밀번호가 일치하지 않습니다.");
                    tv_pw_alert.setVisibility(View.VISIBLE);
                    pswChecked2 = false;
                }else
                    pswChecked2 = true;
                tv_pw_alert.setVisibility(View.INVISIBLE);

                //아이디랑 비밀번호 정규표현식 통과, 비밀번호 확인 일치 시 "변경 버튼 오픈"
                if(pswChecked && pswChecked2){
                    btn_change_pw.setEnabled(true);
                    //sign.setBackgroundColor(Color.parseColor("#3498DB"));
                    btn_change_pw.setBackgroundResource(R.drawable.radius);
                }else{
                    tv_pw_alert.setText("비밀번호가 일치하지 않습니다.");
                    tv_pw_alert.setVisibility(View.VISIBLE);
                    btn_change_pw.setEnabled(false);
                    btn_change_pw.setBackgroundResource(R.drawable.radiusgray);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    View.OnClickListener change_PwButtonClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
           //데이터 변경돼야 함
            rewriteUserPsw(pwdString, autoIDForReset);
        }
    };

    public void rewriteUserPsw(String pwdString, String autoIDForReset){
        //Users - Badge

        //Users - Records
        Map<String, Object> data = new HashMap<>();
        data.put("password", pwdString);

        db.collection("users")
                .document(autoIDForReset)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("password rewrite", "password rewrite : successfully written!");
                        Toast.makeText(getActivity(), "변경된 비밀번호로 접속해주세요", Toast.LENGTH_LONG);
                        LoginActivity fragment = new LoginActivity();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("password rewrite", "password rewrite : failed", e);
                        Toast.makeText(getActivity(), "비밀번호 변경 오류", Toast.LENGTH_LONG);
                    }
                });



    }
}

