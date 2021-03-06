package com.example.CapstonDesign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.KakaoSdkError;
import com.kakao.sdk.user.UserApiClient;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import org.w3c.dom.Text;

public class Setting extends Activity {
    private FloatingActionButton mainFab, subFab1, subFab2;
    //dialog
    Dialog dialog_logout;
    Dialog dialog_secession;

    Context mContext = Setting.this;
    String userAutoID = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        userAutoID = UserPreferenceData.getString(Setting.this, "autoID");

        ConstraintLayout btn_nickname_update = findViewById(R.id.btn_nickname_update);
        ConstraintLayout btn_pw = findViewById(R.id.btn_pw);
        ConstraintLayout btn_myinfo_update = findViewById(R.id.btn_myinfo_update);
        ImageView btn_back_white = findViewById(R.id.btn_back_white);
        btn_back_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_nickname_update.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_nickname_update.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_nickname_update.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(Setting.this, NickNameUpdate.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        btn_pw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_pw.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_pw.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(Setting.this, PWUpdate.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        btn_myinfo_update.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionCode = motionEvent.getAction();

                switch (actionCode) {
                    case MotionEvent.ACTION_DOWN:
                        btn_myinfo_update.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage2));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        btn_myinfo_update.setBackground(ContextCompat.getDrawable(Setting.this, R.drawable.btn_style_mypage));
                        Intent intent = new Intent(Setting.this, MyInfoUpdate.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        TextView tv_logout = findViewById(R.id.tv_logout);
        TextView tv_secession = findViewById(R.id.tv_secession);
        //??????
        tv_logout.setPaintFlags(tv_logout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_secession.setPaintFlags(tv_secession.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        dialog_logout = new Dialog(Setting.this);       // Dialog ?????????
        dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialog_logout.setContentView(R.layout.dialog_custom_logout);             // xml ???????????? ????????? ??????

        dialog_secession = new Dialog(Setting.this);       // Dialog ?????????
        dialog_secession.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialog_secession.setContentView(R.layout.dialog_custom_secession);             // xml ???????????? ????????? ??????

        // ??????: ????????? ??????????????? ?????????
        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_logout(); // ?????? showDialog_logout ?????? ??????
            }
        });
        // ??????: ????????? ??????????????? ?????????
        findViewById(R.id.tv_secession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_secession(); // ?????? showDialog_logout ?????? ??????
            }
        });
    }

    public void showDialog_logout() {
        dialog_logout.show(); // ??????????????? ?????????
        // *????????? ???: findViewById()??? ??? ?????? -> ?????? ????????? ??????????????? ????????? ????????? ??????.
        Button btn_yes = dialog_logout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????? ????????????
                UserApiClient.getInstance().logout(error ->{
                    if (error != null){
                        Log.d("kakaokakao", "???????????? ??????, SDK?????? ?????? ????????? ");
                    }else{
                        Log.d("kakaokakao", "???????????? ??????, SDK?????? ?????? ????????? " + error);
                    }
                    return null;
                });
               //????????? ????????????
                OAuthLogin.getInstance().logout(Setting.this);
                boolean isSuccessNaverLogout = OAuthLogin.getInstance().logoutAndDeleteToken(Setting.this);
                if (!isSuccessNaverLogout) {
                    // ???????????? ?????? ????????? ??????????????? ?????????????????? ?????? ????????? ???????????? ??????????????? ??????.
                    Log.d("navernaver", "errorCode:" + OAuthLogin.getInstance().getLastErrorCode(Setting.this));
                }else{
                    Log.d("navernaver", "???????????? ??????");
                }

                dialog_logout.dismiss();
                Toast.makeText(Setting.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                //???????????? ??????
                UserPreferenceData.setString(Setting.this, "autoID", "");
                UserPreferenceData.setString(Setting.this, "userEmail", "");
                UserPreferenceData.setString(Setting.this, "userNickname", "");
                UserPreferenceData.setString(Setting.this, "userSNSType", "");

                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            }
        });
        dialog_logout.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_logout.dismiss();
            }
        });
    }

    public void showDialog_secession() {
        dialog_secession.show(); // ??????????????? ?????????
        // *????????? ???: findViewById()??? ??? ?????? -> ?????? ????????? ??????????????? ????????? ????????? ??????.
        Button btn_yes = dialog_secession.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_secession.dismiss();
                //?????? ????????? y/n ???????????? ??? ??????????????????.?

                //kakao ????????????
                UserApiClient.getInstance().unlink(error ->{
                    if(error != null){
                        Log.d("sdsd", "kakao ???????????? ??????");
                    }else{
                        Log.d("sdsd", "kakao ???????????? ??????. SDK?????? ?????? ?????? ???");
                    }
                    return null;
                });

                // ????????? ?????? ??????
                    try {
                        DeleteTokenTask deleteTokenTask = new DeleteTokenTask();
                        deleteTokenTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                db.collection("users").document(userAutoID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Setting.this, "????????? ??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("sdsd", "?????? ?????????????????????.");
                    }
                });

                UserPreferenceData.setString(Setting.this, "autoID", "");
                UserPreferenceData.setString(Setting.this, "userEmail", "");
                UserPreferenceData.setString(Setting.this, "userNickname", "");
                UserPreferenceData.setString(Setting.this, "userSNSType", "");

                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        dialog_secession.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_secession.dismiss();
            }
        });
    }

    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = OAuthLogin.getInstance().logoutAndDeleteToken(mContext);
            Log.d("sdsd", "check1");

            if (!isSuccessDeleteToken) {
                // ???????????? token ????????? ??????????????? ?????????????????? ?????? token ??? ???????????? ??????????????? ????????????
                // ??????????????? ??????????????? ?????? token ????????? ?????? ????????? ??????????????? ?????? ??? ?????? ?????? ??????
                Log.d("sdsd", "errorCode:" + OAuthLogin.getInstance().getLastErrorCode(mContext));
                Log.d("sdsd", "errorDesc:" + OAuthLogin.getInstance().getLastErrorDesc(mContext));
            }
            Log.d("sdsd", "check2");
            return null;
        }

        protected void onPostExecute(Void v) {
            //updateView();
            Log.d("sdsd", "check3");
        }
    }
}

