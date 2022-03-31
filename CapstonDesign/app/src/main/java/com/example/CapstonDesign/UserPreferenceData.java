package com.example.CapstonDesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserPreferenceData { //저장되는 정보 : 자동ID, 이메일, snsType

    public static final String PREFERENCES_NAME = "ontheriver_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

// get 예제코드
//    String dsautoID =UserPreferenceData.getString(MapActivity.this, "autoID");
//    String dsemail =UserPreferenceData.getString(MapActivity.this, "userEmail");
//    String dssns =UserPreferenceData.getString(MapActivity.this, "userSNSType");
//    Log.d("DS UserPreferenceData - autoID : ", dsautoID+"");
//    Log.d("DS UserPreferenceData - email : ", dsemail+"");
//    Log.d("DS UserPreferenceData - dssns : ", dssns+"");

// 참고 사이트 : https://re-build.tistory.com/37
}

