package com.example.CapstonDesign;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ArActivity extends AppCompatActivity {

    //자 여름방학이왔습니다 우리모두 열심히 해보아요 ^^ !
    //추가 ^^

    private Scene scene;
    private Camera camera;
    private ModelRenderable bulletRenderable;
    private boolean shouldStartTimer = true;
    private int balloonsLeft = 30;
    private Point point;
    private TextView balloonsLeftTxt;
    private SoundPool soundPool;
    private int sound;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    int score;
    int seconds;
    String spotName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Intent intent = getIntent();
        spotName = intent.getStringExtra("spotName");
        if (spotName.equals("1")){
            spotName = "A";
        }else if(spotName.equals("2")){
            spotName = "B";
        }else if(spotName.equals("3")){
            spotName = "C";
        }else if(spotName.equals("4")){
            spotName = "D";
        }else if(spotName.equals("5")){
            spotName = "E";
        }else{
            spotName = "F";
        }

        Display display = getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getRealSize(point);

        loadSoundPool();

        balloonsLeftTxt = findViewById(R.id.balloonsCntTxt);
        CustomArFragment arFragment =
                (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);


        scene = arFragment.getArSceneView().getScene();
        camera = scene.getCamera();

        addBalloonsToScene();
        buildBulletModel();


        Button shoot = findViewById(R.id.shootButton);

        shoot.setOnClickListener(v -> {

            if (shouldStartTimer) {
                startTimer();
                shouldStartTimer = false;
            }

            shoot();
        });

    }

    private void loadSoundPool() {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound = soundPool.load(this, R.raw.blop_sound, 1);

    }

    private void shoot() {

        Ray ray = camera.screenPointToRay(point.x / 2f, point.y / 2f);
        Node node = new Node();
        node.setRenderable(bulletRenderable);
        scene.addChild(node);

        new Thread(() -> {

            for (int i = 0;i < 200;i++) {

                int finalI = i;
                runOnUiThread(() -> {

                    Vector3 vector3 = ray.getPoint(finalI * 0.1f);
                    node.setWorldPosition(vector3);

                    Node nodeInContact = scene.overlapTest(node);

                    if (nodeInContact != null) {

                        balloonsLeft--;
                        balloonsLeftTxt.setText("Balloons Left: " + balloonsLeft);
                        scene.removeChild(nodeInContact);

                        soundPool.play(sound, 1f, 1f, 1, 0
                                , 1f);

                    }

                });

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            runOnUiThread(() -> scene.removeChild(node));

        }).start();

    }

    private void startTimer() {

        TextView timer = findViewById(R.id.timerText);

        new Thread(() -> {

            seconds = 20;

            while (balloonsLeft > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                seconds--;
                int minutesPassed = seconds / 60;
                int secondsPassed = seconds % 60;

                runOnUiThread(() -> timer.setText(minutesPassed + ":" + secondsPassed));

                if(seconds == 0){

                    String dsautoID =UserPreferenceData.getString(ArActivity.this, "autoID");
                    String dsnick =UserPreferenceData.getString(ArActivity.this, "userNickname");

                    //TODO: .document("dOjolBcphNxaKpzW6L1l") -> .document(dsautoID) / .collection("A") -> .collection(진짜게임한스팟)
                    //TODO: .게임한 날짜 추가 "YYYY-MM-DD"
                    if(!dsautoID.equals("")){
                       //Score 데이터 조회
                        db.collection("AR").document("Spots").collection(spotName)
                                .document(dsautoID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Map<String, Object> mmap = document.getData();
                                            if (document.exists()) {
                                                Log.d("ARCheck result", "DocumentSnapshot data: " + mmap);
                                                int compareScore = Integer.parseInt(String.valueOf(mmap.get("score")));

                                                //데이터 비교
                                                if (compareScore < 30 - balloonsLeft){
                                                    //Score 데이터 수정
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("score", 30 - balloonsLeft);
                                                    data.put("nickname", dsnick);
                                                    data.put("date", new Timestamp(new Date()));
                                                    data.put("game", "balloonGame");

                                                    db.collection("AR").document("Spots").collection(spotName)  //collection("A")는 하드코
                                                            .document(dsautoID)
                                                            .set(data, SetOptions.merge())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("AR Score rewrite", "AR Score rewrite : successfully written!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("AR Score rewrite", "AR Score rewrite : failed",e);
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d("ARCheck result", "No such document");
                                                //Score 데이터 삽입
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("score", 30 - balloonsLeft);
                                                data.put("nickname", dsnick);
                                                data.put("date", new Timestamp(new Date()));
                                                data.put("game", "balloonGame");

                                                db.collection("AR").document("Spots").collection(spotName)  //collection("A")는 하드코
                                                        .document(dsautoID)
                                                        .set(data)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("new AR Score", "new AR Score : successfully written!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("new AR Score", "new AR Score : failed", e);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("ARCheck result", "get failed with ", task.getException());
                                        }
                                    }
                                });

                        Intent outIntent = new Intent(ArActivity.this, MapActivity.class);
                        outIntent.putExtra("ScoreResult", 30 - balloonsLeft);
                        setResult(RESULT_OK, outIntent);
                        finish();
                    }
                }
            }
        }).start();

    }

    private void buildBulletModel() {

        Texture
                .builder()
                .setSource(this, R.drawable.texture)
                .build()
                .thenAccept(texture -> {


                    MaterialFactory
                            .makeOpaqueWithTexture(this, texture)
                            .thenAccept(material -> {

                                bulletRenderable = ShapeFactory
                                        .makeSphere(0.01f,
                                                new Vector3(0f, 0f, 0f),
                                                material);
                            });
                });

    }

    private void addBalloonsToScene() {

        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("balloon.sfb"))
                .build()
                .thenAccept(renderable -> {

                    for (int i = 0;i < 30;i++) {

                        Node node = new Node();
                        node.setRenderable(renderable);
                        scene.addChild(node);


                        Random random = new Random();
                        int x = random.nextInt(10);
                        int z = random.nextInt(10);
                        int y = random.nextInt(20);

                        z = -z;

                        node.setWorldPosition(new Vector3(
                                (float) x,
                                y / 10f,
                                (float) z
                        ));
                    }

                });
    }
}

