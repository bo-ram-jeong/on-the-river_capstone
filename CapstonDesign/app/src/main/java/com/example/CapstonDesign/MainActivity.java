package com.example.CapstonDesign;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends BaseActivity implements LoginActivity.OnButtonClickListener {

    // 앱 사용을 위해 필요한 퍼미션
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    // 퍼미션 요청을 구별하기 위한 코드
    private static final int PERMISSION_REQUEST_CODE = 100;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mLayout = findViewById(R.id.mLayout);

        // 퍼미션 확인 후 init 추가 혹은 퍼미션 요청
        if (checkPermission()) {
            LoginActivity fragment = new LoginActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onJoinButtonClick() {
        JoinMembershipFragment fragment = new JoinMembershipFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResetPwClick() {
        ResetPwFragment fragment = new ResetPwFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onSignClick() {
        ResetPwFragment fragment = new ResetPwFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override // 퍼미션 요청시 콜백메서드
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE &&
                grantResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result:grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) { // 퍼미션 요청 허용시 init
                LoginActivity fragment = new LoginActivity();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
            } else { // 퍼미션 요청 거절시 스낵바 출력
                Snackbar.make(mLayout, "퍼미션이 거부되었습니다.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
            }
        }
    }

    // 퍼미션 확인용 메서드
    private boolean checkPermission() {
        for (int i = 0; i< REQUIRED_PERMISSIONS.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}