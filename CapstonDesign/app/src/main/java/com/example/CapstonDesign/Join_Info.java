package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class Join_Info extends Fragment {
    Button btnMan, btnWoman, finalSign;
    EditText edt_Weight, edt_Height, edt_nickname;
    TextView tvnickName_e;
    int mode = 0;
    String emailString, pswString, idString, snsTypeString = "normal" ;  //JoinMemebershipFragment에서 받아온 값들
    String weightString, heightString, nicknameString, gender;
    Boolean nickNameTyped = false, weightTyped = false, heightTyped = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public Join_Info() {
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
            emailString = getArguments().getString("newEmail");
            // 전달한 key 값
            pswString = getArguments().getString("newPsw");
            // 전달한 key 값
            snsTypeString = getArguments().getString("newSNS");
                if (snsTypeString == null){
                    snsTypeString = "normal";
                }else{
                    idString = getArguments().getString("newID");
                }
            }

        View view = inflater.inflate(R.layout.fragment_join__info, container, false);
        edt_nickname = view.findViewById(R.id.edt_nickname);
        btnMan = view.findViewById(R.id.btnMan);
        btnWoman = view.findViewById(R.id.btnWoman);
        finalSign = view.findViewById(R.id.finalSign);
        edt_Height = view.findViewById(R.id.edt_Height);
        edt_Weight = view.findViewById(R.id.edt_Weight);
        tvnickName_e = view.findViewById(R.id.tvnickName_e);

        btnMan.setOnClickListener(btnManButtonClickListener);
        btnWoman.setOnClickListener(btnWomanButtonClickListener);

        finalSign.setOnClickListener(finalSignButtonClickListener);
        finalSign.setEnabled(false);
        finalSign.setBackgroundResource(R.drawable.radiusgray);

        gender = "male";
        btnMan.setBackgroundColor(Color.parseColor("#3498DB"));
        btnMan.setTextColor(Color.parseColor("#ffffff"));
        btnWoman.setBackgroundColor(Color.parseColor("#ffffff"));
        btnWoman.setTextColor(Color.parseColor("#000000"));

        edt_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {
                //정규표현식 필 (2~8자 영문,숫자,한)
                if(!Pattern.matches("^[가-힣a-zA-Z0-9].{2,8}$", c)){
                    //정규표현식 실패
                    tvnickName_e.setVisibility(View.VISIBLE);
                    nickNameTyped = false;
                }else {
                    //정규표현식 통과
                    tvnickName_e.setVisibility(View.INVISIBLE);
                    nickNameTyped = true;
                }

                if(nickNameTyped && weightTyped && heightTyped){
                    finalSign.setEnabled(true);
                    finalSign.setBackgroundResource(R.drawable.radius);
                }else{
                    finalSign.setEnabled(false);
                    finalSign.setBackgroundResource(R.drawable.radiusgray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                nicknameString = edt_nickname.getText().toString();
            }
        });

        edt_Weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                weightString = edt_Weight.getText().toString();
                if (edt_Weight.getText().toString().isEmpty()){
                    weightTyped = false;
                }else{
                    weightTyped = true;
                }

                if(nickNameTyped && weightTyped && heightTyped){
                    finalSign.setEnabled(true);
                    finalSign.setBackgroundResource(R.drawable.radius);
                }else{
                    finalSign.setEnabled(false);
                    finalSign.setBackgroundResource(R.drawable.radiusgray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                weightString = edt_Weight.getText().toString();
                if (edt_Weight.getText().toString().isEmpty()){
                    weightTyped = false;
                }else{
                    weightTyped = true;
                }

                if(nickNameTyped && weightTyped && heightTyped){
                    finalSign.setEnabled(true);
                    finalSign.setBackgroundResource(R.drawable.radius);
                }else{
                    finalSign.setEnabled(false);
                    finalSign.setBackgroundResource(R.drawable.radiusgray);
                }
            }
        });

        edt_Height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                heightString = edt_Height.getText().toString();
                if (edt_Height.getText().toString().isEmpty()) {
                    heightTyped = false;
                }else{
                    heightTyped = true;
                }

                if(nickNameTyped && weightTyped && heightTyped){
                    finalSign.setEnabled(true);
                    finalSign.setBackgroundResource(R.drawable.radius);
                }else{
                    finalSign.setEnabled(false);
                    finalSign.setBackgroundResource(R.drawable.radiusgray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                heightString = edt_Height.getText().toString();
                if (edt_Height.getText().toString().isEmpty()) {
                    heightTyped = false;
                }else{
                    heightTyped = true;
                }

                if(nickNameTyped && weightTyped && heightTyped){
                    finalSign.setEnabled(true);
                    finalSign.setBackgroundResource(R.drawable.radius);
                }else{
                    finalSign.setEnabled(false);
                    finalSign.setBackgroundResource(R.drawable.radiusgray);
                }
            }
        });
        return view;
    }

    View.OnClickListener btnManButtonClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            mode = 1;
            Log.d("gender","male");
            gender = "male";
            btnMan.setBackgroundColor(Color.parseColor("#3498DB"));
            btnMan.setTextColor(Color.parseColor("#ffffff"));
            btnWoman.setBackgroundColor(Color.parseColor("#ffffff"));
            btnWoman.setTextColor(Color.parseColor("#000000"));
        }
    };

    View.OnClickListener btnWomanButtonClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            mode = 2;
            Log.d("gender","female");
            gender = "female";
            btnWoman.setBackgroundColor(Color.parseColor("#3498DB"));
            btnWoman.setTextColor(Color.parseColor("#ffffff"));
            btnMan.setBackgroundColor(Color.parseColor("#ffffff"));
            btnMan.setTextColor(Color.parseColor("#000000"));
        }
    };


    View.OnClickListener finalSignButtonClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
          //여기서 쿼리문이 들어간다.

            Map<String, Object> data = new HashMap<>();
            data.put("email", emailString);
            data.put("password", pswString);
            data.put("nickname", nicknameString);
            data.put("gender", gender);
            data.put("height", heightString);
            data.put("weight", weightString);
            data.put("snsType", snsTypeString);
            if (!snsTypeString.equals("normal")){
                data.put("snsID", idString);
            }


            db.collection("users")
                    //자동ID로 도큐먼트 생성
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            // documentReference.getId() 가 자동아이디값임. 잘 써먹어야함
                            UserPreferenceData.setString(getActivity(), "autoID", documentReference.getId());
                            UserPreferenceData.setString(getActivity(), "userEmail", emailString);
                            UserPreferenceData.setString(getActivity(), "userNickname", nicknameString);
                            UserPreferenceData.setString(getActivity(), "userSNSType", snsTypeString);

                            Log.d("DS input success - email : ", emailString+"");
                            Log.d("DS input success - password : ", pswString+"");
                            Log.d("DS input success - nickname : ", nicknameString+"");
                            Log.d("DS input success - gender : ", gender+"");
                            Log.d("DS input success - height : ", heightString+"");
                            Log.d("DS input success - weight : ", weightString+"");
                            Log.d("DS input success - snsType : ", snsTypeString+"");

                            addUserDocuments(documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            //메인화면으로 진입해야됨
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
            getActivity().finish();

            //전역변수 or Sharedpreferences로 저장 (자동ID, SNStype, email)
        }
    };

    public void addUserDocuments(String documentID){
        //Users - Badge
//        Map<String, Object> data = new HashMap<>();
//        data.put("getDate", "");
//        db.collection("users")
//                .document(documentID)
//                .collection("Badge")
//                .document("Badge01")
//                .set(data);
//        db.collection("users")
//                .document(documentID)
//                .collection("Badge")
//                .document("Badge02")
//                .set(data);

        //Users - Records
//        Map<String, Object> data2 = new HashMap<>();
//        data2.put("date", new Timestamp(new Date()));
//        data2.put("distance", 0);
//        data2.put("kcal", "");
//        data2.put("mode", "");
//        data2.put("route", "");
//        data2.put("runningTime", 0);
//        db.collection("users")
//                .document(documentID)
//                .collection("Records")
//                .add(data2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//            }
//        });

        //Users - TotalRecords
//        Map<String, Object> data4 = new HashMap<>();
//        data4.put("totalDistance", "");
//        db.collection("users")
//                .document(documentID)
//                .collection("TotalRecords")
//                .document("TotalDistance")
//                .set(data4);
//
//        Map<String, Object> data5 = new HashMap<>();
//        data5.put("totalRunningTime", "");
//        db.collection("users")
//                .document(documentID)
//                .collection("TotalRecords")
//                .document("TotalRunningTime")
//                .set(data5);

    }
}