package com.example.CapstonDesign;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ResetPwFragment extends Fragment {
    Button fragementbtn_change_pw;
    EditText fragement_et_pw;
    TextView fragement_pw_alert;
    Boolean emailChecked = false;
    String emailString ="";
    String autoID = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ResetPwFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_pw, container, false);
        fragementbtn_change_pw = view.findViewById(R.id.fragementbtn_change_pw) ;
        fragement_et_pw = view.findViewById(R.id.fragement_et_pw);
        fragement_pw_alert = view.findViewById(R.id.fragement_pw_alert);

        fragementbtn_change_pw.setOnClickListener(changePwButtonClickListener);

        fragementbtn_change_pw.setEnabled(false);
        fragementbtn_change_pw.setBackgroundResource(R.drawable.radiusgray);

        fragement_et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches())
                {
                    fragement_pw_alert.setVisibility(View.VISIBLE);
                    //edtRw 비활성화해야댐
                    emailChecked = false;
                }else {
                    //fragement_pw_alert.setVisibility(View.INVISIBLE);
                    //정규표현식 통과
                    emailString = fragement_et_pw.getText().toString();
                    emailChecked = false;


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
                                            fragement_pw_alert.setVisibility(View.INVISIBLE);
                                            autoID = document.getId();
                                            emailChecked = true;

                                            if(emailChecked){
                                                fragementbtn_change_pw.setEnabled(true);
                                                fragementbtn_change_pw.setBackgroundResource(R.drawable.radius);
                                            }else{
                                                fragementbtn_change_pw.setEnabled(false);
                                                fragementbtn_change_pw.setBackgroundResource(R.drawable.radiusgray);
                                            }
                                        }
                                    } else {
                                        Log.d("check Error getting documents: ", String.valueOf(task.getException()));
                                        fragement_pw_alert.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                    if(emailChecked){
                        fragementbtn_change_pw.setEnabled(true);
                        fragementbtn_change_pw.setBackgroundResource(R.drawable.radius);
                    }else{
                        fragementbtn_change_pw.setEnabled(false);
                        fragementbtn_change_pw.setBackgroundResource(R.drawable.radiusgray);
                    }
                }
                Log.d("emailChecked ", String.valueOf(emailChecked));
                Log.d("autoIDChecked ", String.valueOf(autoID));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailString = fragement_et_pw.getText().toString();
            }

        });

        // Inflate the layout for this fragment
        return view;
    }

    View.OnClickListener changePwButtonClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            ResetPwFinalFragment fragment = new ResetPwFinalFragment();

            // Fragment 생성
            Bundle bundle = new Bundle();
            bundle.putString("emailForReset", emailString);
            // Key, Value
            bundle.putString("autoIDForReset", autoID);
            // Key, Value
            fragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    };

    
}