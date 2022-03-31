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
        //밑줄
        tv_logout.setPaintFlags(tv_logout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_secession.setPaintFlags(tv_secession.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        dialog_logout = new Dialog(Setting.this);       // Dialog 초기화
        dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_logout.setContentView(R.layout.dialog_custom_logout);             // xml 레이아웃 파일과 연결

        dialog_secession = new Dialog(Setting.this);       // Dialog 초기화
        dialog_secession.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_secession.setContentView(R.layout.dialog_custom_secession);             // xml 레이아웃 파일과 연결

        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_logout(); // 아래 showDialog_logout 함수 호출
            }
        });
        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.tv_secession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_secession(); // 아래 showDialog_logout 함수 호출
            }
        });
    }

    public void showDialog_logout() {
        dialog_logout.show(); // 다이얼로그 띄우기
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        Button btn_yes = dialog_logout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //카카오 로그아웃
                UserApiClient.getInstance().logout(error ->{
                    if (error != null){
                        Log.d("kakaokakao", "로그아웃 성공, SDK에서 토큰 삭제됨 ");
                    }else{
                        Log.d("kakaokakao", "로그아웃 실패, SDK에서 토큰 삭제됨 " + error);
                    }
                    return null;
                });
               //네이버 로그아웃
                OAuthLogin.getInstance().logout(Setting.this);
                boolean isSuccessNaverLogout = OAuthLogin.getInstance().logoutAndDeleteToken(Setting.this);
                if (!isSuccessNaverLogout) {
                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태.
                    Log.d("navernaver", "errorCode:" + OAuthLogin.getInstance().getLastErrorCode(Setting.this));
                }else{
                    Log.d("navernaver", "로그아웃 완료");
                }

                dialog_logout.dismiss();
                Toast.makeText(Setting.this, "로그아웃을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                //로그아웃 처리
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
        dialog_secession.show(); // 다이얼로그 띄우기
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        Button btn_yes = dialog_secession.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_secession.dismiss();
                //탈퇴 상태값 y/n 업데이트 뭐 체크해야할거.?

                //kakao 회원탈퇴
                UserApiClient.getInstance().unlink(error ->{
                    if(error != null){
                        Log.d("sdsd", "kakao 회원탈퇴 실패");
                    }else{
                        Log.d("sdsd", "kakao 회원탈퇴 성공. SDK에서 토큰 삭제 됨");
                    }
                    return null;
                });

                // 네이버 연동 해제
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
                        Toast.makeText(Setting.this, "탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("sdsd", "탈퇴 실패하였습니다.");
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
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
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

