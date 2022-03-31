package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ARRanking extends TabActivity {

    TabHost tabHost;
    ListView listView;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Spinner spinner_game, spinner_spot;
    ArrayList<String> arrayList_spingame, arrayList_spinspot;
    ArrayAdapter<String> arrayAdapter_spingame, arrayAdapter_spinspot;

    ListView listview_argamepg, listview_arspotpg;
    ARGameRankingAdapter arGameRankingAdapter;
    ARSpotRankingAdapter arSpotRankingAdapter;

    Integer[] game_title = {R.mipmap.ic_ballon_1x, R.mipmap.ic_bird_1x}; //게임별이미지() 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_ranking);
        ImageView btn_back_white = findViewById(R.id.btn_back_white);
        btn_back_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //탭호스트
        tabHost = getTabHost();
        TabHost.TabSpec tabSpecGameRanking = tabHost.newTabSpec("game_ranking").setIndicator("게임 별");
        tabSpecGameRanking.setContent(R.id.tab_gameranking);
        tabHost.addTab(tabSpecGameRanking);

        TabHost.TabSpec tabSpecSpotRanking = tabHost.newTabSpec("spot_ranking").setIndicator("스팟 별");
        tabSpecSpotRanking.setContent(R.id.tab_spotranking);
        tabHost.addTab(tabSpecSpotRanking);

        tabHost.setCurrentTab(0);


        // Adapter 생성1
        arGameRankingAdapter = new ARGameRankingAdapter();

        // 리스트뷰 참조 및 Adapter달기2
        listview_argamepg = (ListView) findViewById(R.id.listview_argamepg);
        listview_argamepg.setAdapter(arGameRankingAdapter);

        // Adapter 생성2
        arSpotRankingAdapter = new ARSpotRankingAdapter();

        // 리스트뷰 참조 및 Adapter달기2
        listview_arspotpg = (ListView) findViewById(R.id.listview_arspotpg);

        arrayList_spingame = new ArrayList<>();
        arrayList_spingame.add("풍선 Boom");
        arrayList_spingame.add("Bird Catch");
        arrayAdapter_spingame = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_textitem, arrayList_spingame);
        arrayAdapter_spingame.setDropDownViewResource(R.layout.spinner_textitem);
        spinner_game = (Spinner) findViewById(R.id.spinner_game);
        spinner_game.setAdapter(arrayAdapter_spingame);

        arrayList_spinspot = new ArrayList<>();
        arrayList_spinspot.add("A spot");
        arrayList_spinspot.add("B spot");
        arrayList_spinspot.add("C spot");
        arrayList_spinspot.add("D spot");
        arrayList_spinspot.add("E spot");
        arrayList_spinspot.add("F spot");
        arrayAdapter_spinspot = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_textitem, arrayList_spinspot);
        arrayAdapter_spinspot.setDropDownViewResource(R.layout.spinner_textitem);
        spinner_spot = (Spinner) findViewById(R.id.spinner_spot);
        spinner_spot.setAdapter(arrayAdapter_spinspot);

        TextView tv_game_title = findViewById(R.id.tv_game_title);
        ImageView img_game = findViewById(R.id.img_game);
        TextView tv_spot_title = findViewById(R.id.tv_spot_title);
        ImageView img_spot = findViewById(R.id.img_spot);

        String autoId = UserPreferenceData.getString(ARRanking.this, "autoID"); //회원 id get
        TextView game_myRank = findViewById(R.id.game_myRank);
        TextView game_myScore = findViewById(R.id.game_myScore);
        TextView spot_myRank = findViewById(R.id.spot_myRank);
        TextView spot_myScore = findViewById(R.id.spot_myScore);

        spinner_game.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList_spingame.get(i) + " 선택되었습니다.", Toast.LENGTH_SHORT).show();
                tv_game_title.setText(arrayList_spingame.get(i));
                img_game.setImageResource(game_title[i]);

                if (arrayList_spingame.get(i).equals("풍선 Boom")) {
                    DocumentReference productRef = db
                            .collection("AR").document("Spots");

                    List<GameData> balloonGameRankingList = new ArrayList<>();

                    productRef.collection("A").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> taskA) {

                            if (taskA.isSuccessful()) {
                                productRef.collection("C").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> taskC) {

                                        if (taskC.isSuccessful()) {
                                            productRef.collection("E").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> taskE) {
                                                    String nickname_spot = "";
                                                    if (taskE.isSuccessful()) {

                                                        for (QueryDocumentSnapshot document : taskA.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            balloonGameRankingList.add(new GameData(id, date, gameName, nickname, score, "A"));
                                                        }
                                                        for (QueryDocumentSnapshot document : taskC.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            balloonGameRankingList.add(new GameData(id, date, gameName, nickname, score, "C"));
                                                        }
                                                        for (QueryDocumentSnapshot document : taskE.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            balloonGameRankingList.add(new GameData(id, date, gameName, nickname, score, "E"));
                                                        }
                                                        //sorting
                                                        Collections.sort(balloonGameRankingList, new GameScoreComparator().reversed());
                                                        arGameRankingAdapter = new ARGameRankingAdapter();
                                                        for (int i = 0; i < 10; i++) {    //10위까지만 리스트뷰에 노출
                                                            String nickname = balloonGameRankingList.get(i).nickname;
                                                            int score = balloonGameRankingList.get(i).score;
                                                            String spot = balloonGameRankingList.get(i).spot;
                                                            if (i == 0) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_goldmedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else if (i == 1) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_silvermedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else if (i == 2) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_coppermedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else {
                                                                arGameRankingAdapter.addItem(R.drawable.empty_img, i + 1, nickname + "(" + spot + ")", score);
                                                            }
                                                        }
                                                        listview_argamepg.setAdapter(arGameRankingAdapter);
                                                        
                                                        int count = 0;
                                                        String myRank = "-";
                                                        String myScore = "-";
                                                        for (int i = 0; i < balloonGameRankingList.size(); i++) {
                                                            ++count;
                                                            String id = balloonGameRankingList.get(i).id;
                                                            if (autoId.equals(id)) {
                                                                int score = balloonGameRankingList.get(i).score;
                                                                myRank = count + "위";
                                                                myScore = score + "개";
                                                            }
                                                        }
                                                        game_myRank.setText(myRank);
                                                        game_myScore.setText(myScore);
                                                    } else { // taskE 실패
                                                        Log.d(TAG, "Error", taskE.getException());
                                                    }

                                                }
                                            });
                                        } else {// taskC 실패
                                            Log.d(TAG, "Error", taskC.getException());
                                        }

                                    }
                                });
                            } else { // taskA 실패
                                Log.d(TAG, "Error", taskA.getException());
                            }

                        }
                    });
                } else if (arrayList_spingame.get(i).equals("Bird Catch")) {
                    DocumentReference productRef = db
                            .collection("AR").document("Spots");

                    List<GameData> birdGameRankingList = new ArrayList<>();

                    productRef.collection("B").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> taskB) {

                            if (taskB.isSuccessful()) {
                                productRef.collection("D").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> taskD) {

                                        if (taskD.isSuccessful()) {
                                            productRef.collection("F").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> taskF) {

                                                    if (taskF.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : taskB.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            birdGameRankingList.add(new GameData(id, date, gameName, nickname, score, "B"));
                                                        }
                                                        for (QueryDocumentSnapshot document : taskD.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            birdGameRankingList.add(new GameData(id, date, gameName, nickname, score, "D"));
                                                        }
                                                        for (QueryDocumentSnapshot document : taskF.getResult()) {
                                                            Map<String, Object> map = document.getData();
                                                            String id = document.getId();
                                                            Date date = document.getDate("date");
                                                            String gameName = String.valueOf(map.get("game"));
                                                            String nickname = String.valueOf(map.get("nickname"));
                                                            int score = Integer.parseInt(String.valueOf(map.get("score")));
                                                            birdGameRankingList.add(new GameData(id, date, gameName, nickname, score, "F"));
                                                        }
                                                        //sorting
                                                        Collections.sort(birdGameRankingList, new GameScoreComparator().reversed());
                                                        arGameRankingAdapter = new ARGameRankingAdapter();
                                                        for (int i = 0; i < 10; i++) {    //10위까지만 리스트뷰에 노출
                                                            String nickname = birdGameRankingList.get(i).nickname;
                                                            int score = birdGameRankingList.get(i).score;
                                                            String spot = birdGameRankingList.get(i).spot;
                                                            if (i == 0) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_goldmedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else if (i == 1) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_silvermedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else if (i == 2) {
                                                                arGameRankingAdapter.addItem(R.drawable.ic_coppermedal_1x, i + 1, nickname + "(" + spot + ")", score);
                                                            } else {
                                                                arGameRankingAdapter.addItem(R.drawable.empty_img, i + 1, nickname + "(" + spot + ")", score);
                                                            }
                                                        }
                                                        listview_argamepg.setAdapter(arGameRankingAdapter);
                                                        String myRank = "-";
                                                        String myScore = "-";
                                                        // todo 내 순위 찾기

                                                        int count = 0;
                                                        for (int i = 0; i < birdGameRankingList.size(); i++) {
                                                            ++count;
                                                            String id = birdGameRankingList.get(i).id;
                                                            if (autoId.equals(id)) {
                                                                int score = birdGameRankingList.get(i).score;
                                                                myRank = count + "위";
                                                                myScore = score + "개";
                                                            }
                                                        }
                                                        game_myRank.setText(myRank);
                                                        game_myScore.setText(myScore);

                                                    } else { // taskF 실패
                                                        Log.d(TAG, "Error", taskF.getException());
                                                    }
                                                }
                                            });
                                        } else {// taskD 실패
                                            Log.d(TAG, "Error", taskD.getException());
                                        }
                                    }
                                });
                            } else { // taskB 실패
                                Log.d(TAG, "Error", taskB.getException());
                            }

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner_spot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList_spinspot.get(i) + " 선택되었습니다.", Toast.LENGTH_SHORT).show();
                String spot = arrayList_spinspot.get(i);
                String spotName = spot.substring(0, 1);
                //스팟마다 순위 db불러오기
                CollectionReference productRef = db
                        .collection("AR").document("Spots")
                        .collection(spotName);
                productRef.orderBy("score", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //작업이 성공적으로 마쳤을때
                        if (task.isSuccessful()) {
                            //컬렉션 아래에 있는 모든 정보를 가져온다.

                            arSpotRankingAdapter = new ARSpotRankingAdapter();
                            int count = 0;
                            String myRank = "-";
                            String myScore = "-";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                if (++count <= 10) {    //10위까지만 리스트뷰에 노출
                                    if (count == 1) {
                                        arSpotRankingAdapter.addItem(R.drawable.ic_goldmedal_1x, String.valueOf(count), String.valueOf(map.get("nickname")), String.valueOf(map.get("score")));
                                    } else if (count == 2) {
                                        arSpotRankingAdapter.addItem(R.drawable.ic_silvermedal_1x, String.valueOf(count), String.valueOf(map.get("nickname")), String.valueOf(map.get("score")));
                                    } else if (count == 3) {
                                        arSpotRankingAdapter.addItem(R.drawable.ic_coppermedal_1x, String.valueOf(count), String.valueOf(map.get("nickname")), String.valueOf(map.get("score")));
                                    } else {
                                        arSpotRankingAdapter.addItem(R.drawable.empty_img, String.valueOf(count), String.valueOf(map.get("nickname")), String.valueOf(map.get("score")));
                                    }
                                }

                                // todo 내 순위 찾기 로직
                                String id = document.getId().trim();
                                if (autoId.equals(id)) {
                                    myRank = count + "위";
                                    String score = String.valueOf(map.get("score"));
                                    myScore = score + "개";
                                }
                            }

                            spot_myRank.setText(myRank);
                            spot_myScore.setText(myScore);

                            listview_arspotpg.setAdapter(arSpotRankingAdapter);
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });

                switch (spotName) {
                    case "A":
                    case "C":
                    case "E":
                        tv_spot_title.setText("풍선 Boom");
                        img_spot.setImageResource(R.mipmap.ic_ballon_1x);
                        break;
                    case "B":
                    case "D":
                    case "F":
                        tv_spot_title.setText("Catch Bird");
                        img_spot.setImageResource(R.mipmap.ic_bird_1x);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

}

class GameData {
    String id;
    Date date;
    String gameName;
    String nickname;
    int score;
    String spot;

    public GameData(String id, Date date, String gameName, String nickname, int score, String spot) {
        this.date = date;
        this.id = id;
        this.gameName = gameName;
        this.nickname = nickname;
        this.score = score;
        this.spot = spot;
    }
}

class GameScoreComparator implements Comparator<GameData> {
    @Override
    public int compare(GameData f1, GameData f2) {
        if (f1.score > f2.score) {
            return 1;
        } else if (f1.score < f2.score) {
            return -1;
        } else {
            //동점일때 날짜순으로
            if (f1.date.after(f2.date)) {
                return 1;

            } else if (f1.date.before(f2.date)) {
                return -1;
            }
            return 0;
        }
    }
}

