package com.example.CapstonDesign;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.user.UserApiClient;

import java.util.Map;
import java.util.regex.Pattern;

public class JoinMembershipFragment extends Fragment implements LoginActivity.OnButtonClickListener {


    private Button sign;
    private EditText edtPw,edtRpw,edtEmail;
    private TextView tvCom,tvComEmail, tvJoin;
    private String pwdString, emailString, snsString, idString="";
    private Boolean emailChecked = false, pswChecked = false, pswChecked2 = false ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public JoinMembershipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_membership, container, false);

        sign = view.findViewById(R.id.sign);
        edtPw = view.findViewById(R.id.edt_Pw);
        edtRpw = view.findViewById(R.id.edt_Rpw);
        edtEmail = view.findViewById(R.id.edt_email);
        tvCom = view.findViewById(R.id.tvCom);
        tvComEmail = view.findViewById(R.id.tvCom_e);
        tvJoin = view.findViewById(R.id.tvJoin);

        sign.setEnabled(false);
        sign.setBackgroundResource(R.drawable.radiusgray);

        if(getArguments() != null) {
            emailString = getArguments().getString("newEmail");
            snsString = getArguments().getString("newSNS");

            if (snsString.equals("kakao")){
                idString = getArguments().getString("newKakaoID");
            }else if (snsString.equals("naver")){
                idString = getArguments().getString("newNaverID");
            }

            if (!emailString.equals("null")) {
                Log.d("emailString is not null", "" + emailString);
                edtEmail.setText(emailString);
                edtEmail.setEnabled(false);
                emailChecked = true;
            }
//            }else{
//                Log.d("emailString is null" , ""+emailString);
//
//                UserApiClient.getInstance().unlink(error ->{
//                    if(error != null){
//                        Log.d("sdsd", "kakao 회원탈퇴 실패");
//                    }else{
//                        Log.d("sdsd", "kakao 회원탈퇴 성공. SDK에서 토큰 삭제 됨");
//                        Toast.makeText(getActivity(), "이메일 허용은 필수사항입니다.", Toast.LENGTH_SHORT).show();
//                        LoginActivity fragment = new LoginActivity();
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.fragment_container, fragment)
//                                .addToBackStack(null)
//                                .commit();
//                    }
//                    return null;
//                });
//            }
        }

                //키보드 올라오면 화면올라가게 하고싶었는데 안됨
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches())
                {
                    tvComEmail.setText("이메일 형식을 지켜주세요.");
                    tvComEmail.setVisibility(View.VISIBLE);
                    //edtRw 비활성화해야댐
                    emailChecked = false;
                }else {
                    tvComEmail.setVisibility(View.INVISIBLE);
                    //정규표현식 통과
                    emailString = edtEmail.getText().toString();
                    emailChecked = true;

                    //SNSType - normal일 시 로그인 중복체크 해야함
                    db.collection("users")
                            .whereEqualTo("email", emailString)
                            .whereEqualTo("snsType", "normal")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("check DB search result" ,document.getId() + " => " + document.getData());
                                            tvComEmail.setText("중복된 아이디 입니다.");
                                            tvComEmail.setVisibility(View.VISIBLE);
                                            emailChecked = false;
                                        }
                                    } else {
                                        Log.d("check Error getting documents: ", String.valueOf(task.getException()));
                                        tvComEmail.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                    if(pswChecked && emailChecked && pswChecked2){
                        sign.setEnabled(true);
                        sign.setBackgroundResource(R.drawable.radius);
                    }else{
                        sign.setEnabled(false);
                        sign.setBackgroundResource(R.drawable.radiusgray);
                    }
                }
                Log.d("emailChecked ", String.valueOf(emailChecked));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailString = edtEmail.getText().toString();
            }

        });
        edtPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9]).{8,12}$", s))
                {
                    tvCom.setVisibility(View.VISIBLE);
                    //edtRw 비활성화해야댐
                    edtRpw.setEnabled(false);
                    pswChecked = false;
                }else {
                    tvCom.setVisibility(View.INVISIBLE);
                    //정규표현식 통과
                    pwdString = edtPw.getText().toString();
                    edtRpw.setEnabled(true);
                    pswChecked = true;

                    if(pswChecked && emailChecked && pswChecked2){
                        sign.setEnabled(true);
                        //sign.setBackgroundColor(Color.parseColor("#3498DB"));
                        sign.setBackgroundResource(R.drawable.radius);
                    }else{
                        sign.setEnabled(false);
                        sign.setBackgroundResource(R.drawable.radiusgray);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                pwdString = edtPw.getText().toString();
            }

        });
        edtRpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!pwdString.equals(s.toString()))
                {
                    tvCom.setText("비밀번호가 일치하지 않습니다.");
                    tvCom.setVisibility(View.VISIBLE);
                    pswChecked2 = false;
                }else
                    pswChecked2 = true;
                    tvCom.setVisibility(View.INVISIBLE);

                //아이디랑 비밀번호 정규표현식 통과, 비밀번호 확인 일치 시 "다음 버튼 오픈"
                if(pswChecked && emailChecked && pswChecked2){
                    sign.setEnabled(true);
                    //sign.setBackgroundColor(Color.parseColor("#3498DB"));
                    sign.setBackgroundResource(R.drawable.radius);
                }else{
                    tvCom.setText("비밀번호가 일치하지 않습니다.");
                    tvCom.setVisibility(View.VISIBLE);
                    sign.setEnabled(false);
                    sign.setBackgroundResource(R.drawable.radiusgray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                Log.d("pswChecked ", String.valueOf(pswChecked));
//                Log.d("emailChecked ", String.valueOf(emailChecked));
//                Log.d("pswChecked2 ", String.valueOf(pswChecked2));
            }
        });
        sign.setOnClickListener(view1 -> onSignClick());
        tvJoin.setOnClickListener(tvJoinOnClick);

        return view;
    }

    View.OnClickListener tvJoinOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            LoginActivity fragment = new LoginActivity();
            getActivity().getSupportFragmentManager().beginTransaction()
                    //.remove(JoinMembershipFragment.this)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
    };

    @Override
    public void onSignClick() {
        Join_Info fragment = new Join_Info();

        // Fragment 생성
        Bundle bundle = new Bundle();
        bundle.putString("newEmail", emailString);
        // Key, Value
        bundle.putString("newPsw", pwdString);
        // Key, Value
        bundle.putString("newSNS", snsString);
        bundle.putString("newID", idString);
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onJoinButtonClick() {}
    @Override
    public void onResetPwClick() {}
}