package com.example.CapstonDesign;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinishActivity extends BaseActivity {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String autoId;
    private ArrayList<String> obtainedBadgeList = new ArrayList<>();
    private ArrayList<String> obtainedBadgeContList = new ArrayList<>();
    private ArrayList<Integer> obtainedBadgeImgList = new ArrayList<Integer>();

    //뒤로가기 버튼 관련
    private long backKeyPressedTime = 0;    // '뒤로' 버튼을 클릭했을 때의 시간
    private long TIME_INTERVAL = 2000;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_layout);
        getSupportActionBar().hide();
        autoId = UserPreferenceData.getString(FinishActivity.this, "autoID");
        Button button = findViewById(R.id.btnShare);
        Button btnMain = findViewById(R.id.btnMain);
        TextView txtMyDistance = findViewById(R.id.myDistance);
        TextView txtRealDistance = findViewById(R.id.realDistance);
        TextView txtTime = findViewById(R.id.totalTime);
        TextView txtKcal = findViewById(R.id.kcal);
        ImageView imgMarker1 = findViewById(R.id.marker1);
        ImageView imgMarker2 = findViewById(R.id.marker2);
        ImageView imgMarker3 = findViewById(R.id.marker3);
        ImageView imgMarker4 = findViewById(R.id.marker4);
        ImageView imgMarker5 = findViewById(R.id.marker5);
        ImageView imgMarker6 = findViewById(R.id.marker6);

        Intent intent = getIntent();

        ArrayList<Bitmap> markerImgList = (ArrayList<Bitmap>) intent.getSerializableExtra("markerImgList");
        String strRealDistance = intent.getStringExtra("realDistance");
        String strMyDistance = intent.getStringExtra("myDistance");
        String strTime = intent.getStringExtra("time");
        String strKcal = intent.getStringExtra("kcal");

        txtRealDistance.setText(strRealDistance);
        txtMyDistance.setText(strMyDistance);
        txtTime.setText(strTime);
        txtKcal.setText(strKcal);

        int cnt = 0;
        for(Bitmap img:markerImgList){
            cnt++;
            if (cnt==1) imgMarker1.setImageBitmap(img);
            if (cnt==2) imgMarker2.setImageBitmap(img);
            if (cnt==3) imgMarker3.setImageBitmap(img);
            if (cnt==4) imgMarker4.setImageBitmap(img);
            if (cnt==5) imgMarker5.setImageBitmap(img);
            if (cnt==6) imgMarker6.setImageBitmap(img);
        }

        dbInsertBadge();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = findViewById(R.id.recordContainer);
                view.setDrawingCacheEnabled(true);
                Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);

                try {
                    File cachePath = new File(getApplicationContext().getCacheDir(), "images");
                    cachePath.mkdirs();
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
                    screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    File newFile = new File(cachePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                            "com.example.CapstonDesign.fileprovider", newFile);

                    Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                    Sharing_intent.setType("image/png");
                    Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(Sharing_intent, "공유하기"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FinishActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.horizon_exit, R.anim.horizon_enter);
                finish();
            }
        });
    }

    //뒤로가기 이벤트 클릭시
    public void onBackPressed() {

            // '뒤로' 버튼 클릭 시간과 현재 시간을 비교 게산한다.
            // 마지막 '뒤로'버튼 클릭 시간이 이전 '뒤로'버튼 클릭시간과의 차이가 TIME_INTERVAL(여기서는 2초)보다 클 때 true
            if (System.currentTimeMillis() > backKeyPressedTime + TIME_INTERVAL) {

                // 현재 시간을 backKeyPressedTime에 저장한다.
                backKeyPressedTime = System.currentTimeMillis();

                // 종료 안내문구를 노출한다.
                showMessage();
            } else {
                // 마지막 '뒤로'버튼 클릭시간이 이전 '뒤로'버튼 클릭시간과의 차이가 TIME_INTERVAL(2초)보다 작을때

                // Toast가 아직 노출중이라면 취소한다.
                toast.cancel();

                // 앱을 종료한다.
                FinishActivity.this.finish();
            }
    }

    public void showMessage() {
        toast = Toast.makeText(FinishActivity.this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void dbInsertBadge() {
        CollectionReference collection1 = db
                .collection("users").document(autoId)
                .collection("Records");
        collection1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String strDistance;
                    String strRouteCnt;
                    int routeCnt=0;
                    double distance=0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> map = document.getData();
                        strDistance = String.valueOf(map.get("distance"));
                        strRouteCnt = String.valueOf(map.get("routeCnt"));

                        if (strDistance != "null")
                            distance += Double.parseDouble(strDistance);
                        if (strRouteCnt != "null")
                            routeCnt += Integer.parseInt(strRouteCnt);
                    }

                    if ((int)distance >= 500){
                        obtainedBadgeList.add("badge09");
                        obtainedBadgeContList.add("500km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_500km_mi_1x);
                    } else if ((int)distance >= 400){
                        obtainedBadgeList.add("badge08");
                        obtainedBadgeContList.add("400km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_400km_mi_1x);
                    } else if ((int)distance >= 300){
                        obtainedBadgeList.add("badge07");
                        obtainedBadgeContList.add("300km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_300km_mi_1x);
                    } else if ((int)distance >= 200){
                        obtainedBadgeList.add("badge06");
                        obtainedBadgeContList.add("200km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_200km_mi_1x);
                    } else if ((int)distance >= 100){
                        obtainedBadgeList.add("badge05");
                        obtainedBadgeContList.add("100km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_100km_mi_1x);
                    } else if ((int)distance >= 50){
                        obtainedBadgeList.add("badge04");
                        obtainedBadgeContList.add("50km 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_50km_mi_1x);
                    }

                    if (routeCnt >= 6) {
                        obtainedBadgeList.add("badge12");
                        obtainedBadgeContList.add("6 spot 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_6spot_mi_1x);
                    } else if (routeCnt >= 4) {
                        obtainedBadgeList.add("badge11");
                        obtainedBadgeContList.add("4 spot 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_4spot_mi_1x);
                    } else if (routeCnt >= 2) {
                        obtainedBadgeList.add("badge10");
                        obtainedBadgeContList.add("2 spot 달성 성공");
                        obtainedBadgeImgList.add(R.mipmap.ic_2spot_mi_1x);
                    }
                }
            }
        });

        CollectionReference collection2 = db
                .collection("users").document(autoId)
                .collection("Badge");
        collection2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int myCurrentBadgeCnt = 0;
                    String myCurrentBadge;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        myCurrentBadgeCnt++;
                        myCurrentBadge = document.getId();
                        if (obtainedBadgeList.contains(myCurrentBadge))
                            obtainedBadgeList.remove(myCurrentBadge);
                    }
                    int myTotalBadgeCnt = myCurrentBadgeCnt + obtainedBadgeList.size();
                    switch (myTotalBadgeCnt){
                        case 12:
                            obtainedBadgeList.add("badge03");
                            obtainedBadgeContList.add("고급자 달성 성공");
                            obtainedBadgeImgList.add(R.mipmap.ic_maj_mi_1x);
                            break;
                        case 8:
                            obtainedBadgeList.add("badge02");
                            obtainedBadgeContList.add("중급자 달성 성공");
                            obtainedBadgeImgList.add(R.mipmap.ic_inter_mi_1x);
                            break;
                        case 3:
                            obtainedBadgeList.add("badge01");
                            obtainedBadgeContList.add("초급자 달성 성공");
                            obtainedBadgeImgList.add(R.mipmap.ic_junior_mi);
                            break;
                    }

                    Date nowDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String strNowDate = simpleDateFormat.format(nowDate);

                    Map<String, Object> data = new HashMap<>();
                    data.put("getDate", strNowDate);

                    if (obtainedBadgeList.size()>0) {
                        for (String obtainedBadge : obtainedBadgeList) {
                            db.collection("users").document(autoId)
                                    .collection("Badge").document(obtainedBadge).set(data);
                        }

                        ArrayList<Map<String, Object>> finalObtainedBadgeImgList = new ArrayList<>();
                        for (int i=0; i<obtainedBadgeList.size(); i++) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("IMAGE", obtainedBadgeImgList.get(i));
                            map.put("TEXT", obtainedBadgeContList.get(i));
                            finalObtainedBadgeImgList.add(map);
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.dialog_obtained_badge_list,null);
                        builder.setView(view);

                        ListView listView =  view.findViewById(R.id.obtainedBadgeList);
                        AlertDialog dialog = builder.create();
                        SimpleAdapter simpleAdapter = new SimpleAdapter(FinishActivity.this,
                                finalObtainedBadgeImgList,
                                R.layout.item_obtained_badge,
                                new String[]{"IMAGE","TEXT"},
                                new int[]{R.id.obtainedBadgeImage,R.id.obtainedBadgeCont});

                        listView.setAdapter(simpleAdapter);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
            }
        });
    }

}