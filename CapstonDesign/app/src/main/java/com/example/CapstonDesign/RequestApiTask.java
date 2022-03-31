package com.example.CapstonDesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestApiTask extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private final OAuthLogin mOAuthLoginModule;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Boolean naverEmpty = false;
    private FragmentActivity fcontext;

    public RequestApiTask(Context mContext, OAuthLogin mOAuthLoginModule) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
        this.fcontext = (FragmentActivity)mContext;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Void... params) {
        String url = "https://openapi.naver.com/v1/nid/me";
        String at = mOAuthLoginModule.getAccessToken(mContext);
        return mOAuthLoginModule.requestApi(mContext, at, url);
    }

    protected void onPostExecute(String content) {
        try {
            JSONObject loginResult = new JSONObject(content);
            if (loginResult.getString("resultcode").equals("00")){
                JSONObject response = loginResult.getJSONObject("response");
                String id = response.getString("id");
                String email = response.getString("email");
                //Toast.makeText(mContext," id : "+id, Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext," email : "+email, Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, "id : "+id +" email : "+email, Toast.LENGTH_SHORT).show();

//                        //DB에 정보없면 회원가입, 있으면 openmap
//                        //이메일 동의 여부도 체크

                        db.collection("users")
                            .whereEqualTo("snsID", id)
                            .whereEqualTo("snsType", "naver")
                            .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                naverEmpty = true;
                                                // Log.d(TAG, document.getId() + " => " + document.getData());
                                                String n = document.getData().get("nickname").toString();

                                                UserPreferenceData.setString(mContext, "autoID", document.getId());
                                                UserPreferenceData.setString(mContext, "userEmail", email);
                                                UserPreferenceData.setString(mContext, "userNickname", n);
                                                UserPreferenceData.setString(mContext, "userSNSType", "naver");
                                            }
                                            if (!naverEmpty){
                                                //open joinMembershipFragment

                                                JoinMembershipFragment fragment = new JoinMembershipFragment();

                                                // Fragment 생성
                                                Bundle bundle = new Bundle();
                                                bundle.putString("newEmail", email);
                                                bundle.putString("newNaverID", id);
                                                // Key, Value
                                                bundle.putString("newSNS", "naver");
                                                fragment.setArguments(bundle);

                                                fcontext.getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.fragment_container, fragment)
                                                        .addToBackStack(null)
                                                        .commit();
                                            }else {
                                                ((Activity)mContext).finish();
                                                Intent intent = new Intent(mContext, MapActivity.class);
                                                mContext.startActivity(intent);
                                            }

                                        } else {
                                            //Log.d(TAG, "Error getting documents: ", task.getException());
                                        }

                                    }
                                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}