package com.example.CapstonDesign;


import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyBadge extends TabActivity {
    TabHost tabHost;

    FragmentManager fragmentManager;
    ArrayList<Integer> badgeList;
    ArrayList<String> getDateList;
    List<MyBadgeData> myBadgeDataList = new ArrayList<>();

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybadge);
        badgeList = new ArrayList();

        ImageView btn_back_white = findViewById(R.id.btn_back_white);
        btn_back_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //탭호스트
        tabHost = getTabHost();
        TabHost.TabSpec tabSpecHoldingBadge = tabHost.newTabSpec("holdingbadge").setIndicator("보유 배지");
        tabSpecHoldingBadge.setContent(R.id.tab_holdingbadge);
        tabHost.addTab(tabSpecHoldingBadge);


        TabHost.TabSpec tabSpecWholeBadge = tabHost.newTabSpec("wholebadge").setIndicator("전체 배지");
        tabSpecWholeBadge.setContent(R.id.tab_wholebadge);
        tabHost.addTab(tabSpecWholeBadge);

        tabHost.setCurrentTab(0);
        /*tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).getBackground().setColorFilter(Color.rgb(52,152,219), PorterDuff.Mode.MULTIPLY);*/

        fragmentManager = getFragmentManager();

        final GridView grv_badge_allitem = findViewById(R.id.grv_badge_allitem);
        GridAdapter2 gAdapter2 = new GridAdapter2(this, fragmentManager);
        grv_badge_allitem.setAdapter(gAdapter2);

        String autoId=UserPreferenceData.getString(MyBadge.this,"autoID");
        CollectionReference productRef = db
                .collection("users").document(autoId)
                .collection("Badge");
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //작업이 성공적으로 마쳤을때

                if (task.isSuccessful()) {
                    //컬렉션 아래에 있는 모든 정보를 가져온다.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> map = document.getData();
                        String getDate = String.valueOf(map.get("getDate"));
//                        badgeIdList.add(document.getId());
                        switch (document.getId()) {
                            case "badge01":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_JUNIOR, getDate));
                                break;
                            case "badge02":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_INTER, getDate));
                                break;
                            case "badge03":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_MAJ, getDate));
                                break;
                            case "badge04":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_50, getDate));
                                break;
                            case "badge05":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_100, getDate));
                                break;
                            case "badge06":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_200, getDate));
                                break;
                            case "badge07":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_300, getDate));
                                break;
                            case "badge08":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_400, getDate));
                                break;
                            case "badge09":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_500, getDate));
                                break;
                            case "badge10":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_2SPOT, getDate));
                                break;
                            case "badge11":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_4SPOT, getDate));
                                break;
                            case "badge12":
                                myBadgeDataList.add(new MyBadgeData(BadgeData.BADGE_6SPOT, getDate));
                                break;
                        }
                    }
                    final GridView grv_badge_havingitem = findViewById(R.id.grv_badge_havingitem);
                    GridAdapter1 gAdapter1 = new GridAdapter1(MyBadge.this, fragmentManager, myBadgeDataList);
                    grv_badge_havingitem.setAdapter(gAdapter1);

                    //그렇지 않을때
                } else {
                    Log.d(TAG, "Error", task.getException());
                }
            }
        });

    }

}

class BadgeData {

    int id;
    int image;
    String title;

    public BadgeData(int id, int image, String title) {
        this.id = id;
        this.image = image;
        this.title = title;
    }

    public static final BadgeData BADGE_JUNIOR = new BadgeData(R.mipmap.ic_junior_mi, R.mipmap.badge_success_min, "초급자 달성 성공");
    public static final BadgeData BADGE_INTER = new BadgeData(R.mipmap.ic_inter_mi_1x, R.mipmap.badge_success_inter, "중급자 달성 성공");
    public static final BadgeData BADGE_MAJ = new BadgeData(R.mipmap.ic_maj_mi_1x, R.mipmap.badge_success_major, "고급자 달성 성공");
    public static final BadgeData BADGE_50 = new BadgeData(R.mipmap.ic_50km_mi_1x, R.mipmap.badge_success_50km, "50km 달성 성공");
    public static final BadgeData BADGE_100 = new BadgeData(R.mipmap.ic_100km_mi_1x, R.mipmap.badge_success_100km, "100km 달성 성공");
    public static final BadgeData BADGE_200 = new BadgeData(R.mipmap.ic_200km_mi_1x, R.mipmap.badge_success_200km, "200km 달성 성공");
    public static final BadgeData BADGE_300 = new BadgeData(R.mipmap.ic_300km_mi_1x, R.mipmap.badge_success_300km, "300km 달성 성공");
    public static final BadgeData BADGE_400 = new BadgeData(R.mipmap.ic_400km_mi_1x, R.mipmap.badge_success_400km, "400km 달성 성공");
    public static final BadgeData BADGE_500 = new BadgeData(R.mipmap.ic_500km_mi_1x, R.mipmap.badge_success_500km, "500km 달성 성공");
    public static final BadgeData BADGE_2SPOT = new BadgeData(R.mipmap.ic_2spot_mi_1x, R.mipmap.badge_success_2spot, "2 spot 달성 성공");
    public static final BadgeData BADGE_4SPOT = new BadgeData(R.mipmap.ic_4spot_mi_1x, R.mipmap.badge_success_4spot, "4 spot 달성 성공");
    public static final BadgeData BADGE_6SPOT = new BadgeData(R.mipmap.ic_6spot_mi_1x, R.mipmap.badge_success_6spot, "6 spot 달성 성공");


}

class MyBadgeData {
    BadgeData badgeData;
    String getDate;

    public MyBadgeData(BadgeData badgeData, String getDate) {
        this.badgeData = badgeData;
        this.getDate = getDate;
    }
}

class GridAdapter1 extends BaseAdapter {
    Context context;
    FragmentManager fragmentManager;
    List<MyBadgeData> myBadgeDataList;

    public GridAdapter1(Context c, FragmentManager fragmentManager, List<MyBadgeData> myBadgeDataList) {
        context = c;
        this.fragmentManager = fragmentManager;
        this.myBadgeDataList = myBadgeDataList;

    }

    public int getCount() {
        return myBadgeDataList.size();
    }

    public Object getItem(int position) {
        return 0;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgview = new ImageView(context);
        imgview.setLayoutParams(new GridView.LayoutParams(200, 300));
        imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgview.setPadding(5, 5, 5, 5);

        imgview.setImageResource(myBadgeDataList.get(position).badgeData.id);
        final int pos = position;


        //배지이미지마다 보여주기
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBadgeFragment1 fragment = new MyBadgeFragment1(fragmentManager);
                Bundle bundle = new Bundle();
                bundle.putString("tabId", "tab1");
                bundle.putString("badgeCode", String.valueOf(myBadgeDataList.get(pos).badgeData.image));
                bundle.putString("badgeTitle", myBadgeDataList.get(pos).badgeData.title);
                bundle.putString("badgeDetail", myBadgeDataList.get(pos).getDate);

                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.badge_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return imgview;
    }

}

class GridAdapter2 extends BaseAdapter {
    Context context;
    Integer[] badgeID = {
            R.mipmap.ic_junior_mi, R.mipmap.ic_inter_mi_1x, R.mipmap.ic_maj_mi_1x,
            R.mipmap.ic_50km_mi_1x, R.mipmap.ic_100km_mi_1x, R.mipmap.ic_200km_mi_1x,
            R.mipmap.ic_300km_mi_1x, R.mipmap.ic_400km_mi_1x, R.mipmap.ic_500km_mi_1x,
            R.mipmap.ic_2spot_mi_1x, R.mipmap.ic_4spot_mi_1x, R.mipmap.ic_6spot_mi_1x
    };
    Integer[] badgeImage = {
            R.mipmap.badge_info_min, R.mipmap.badge_info_inter, R.mipmap.badge_info_major,
            R.mipmap.badge_info_50km, R.mipmap.badge_info_100km, R.mipmap.badge_info_200km,
            R.mipmap.badge_info_300km, R.mipmap.badge_info_400km, R.mipmap.badge_info_500km,
            R.mipmap.badge_info_2spot, R.mipmap.badge_info_4spot, R.mipmap.badge_info_6spot
    };
    String[] badgeTitle = {
            "초급자 달성", "중급자 달성", "고급자 달성",
            "50km 달성", "100km 달성", "200km 달성",
            "300km 달성", "400km 달성", "500km 달성",
            "2 spot 달성", "4 spot 달성", "6 spot 달성"
    };
    String[] badgeDetail = {
            "배지 3개 획득 시\n획득가능한 배지입니다.", "배지 8개 획득 시\n획득가능한 배지입니다.", "배지 12개 획득 시\n획득가능한 배지입니다.",
            "50km 달성 시\n획득가능한 배지입니다.", "100km 달성 시\n획득가능한 배지입니다.", "200km 달성 시\n획득가능한 배지입니다.",
            "300km 달성 시\n획득가능한 배지입니다.", "400km 달성 시\n획득가능한 배지입니다.", "500km 달성 시\n획득가능한 배지입니다.",
            "2 spot 완주 시\n획득가능한 배지입니다.", "4 spot 완주 시\n획득가능한 배지입니다.", "6 spot 완주 시\n획득가능한 배지입니다."
    };

    FragmentManager fragmentManager;

    public GridAdapter2(Context c, FragmentManager fragmentManager) {
        context = c;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return badgeID.length;
    }

    public Object getItem(int position) {
        return 0;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgview = new ImageView(context);
        imgview.setLayoutParams(new GridView.LayoutParams(200, 300));
        imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgview.setPadding(5, 5, 5, 5);

        imgview.setImageResource(badgeID[position]);
        final int pos = position;

        //배지이미지마다 보여주기
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBadgeFragment2 fragment = new MyBadgeFragment2(fragmentManager);
                Bundle bundle = new Bundle();
                bundle.putString("tabId", "tab2");
                bundle.putString("badgeCode", String.valueOf(badgeImage[pos]));
                bundle.putString("badgeTitle", badgeTitle[pos]);
                bundle.putString("badgeDetail", badgeDetail[pos]);

                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.badge_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        return imgview;
    }

}
