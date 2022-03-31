package com.example.CapstonDesign;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ArActivity2  extends AppCompatActivity {

    private Scene scene;
    private Camera camera;
    private ModelRenderable mAndyRederable;
    private ArFragment mPlayAreaFragment;
    private Session mSession;
    private ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture schedulerHandler;
    private Anchor anchor;
    private AnchorNode anchorNode;
    private Node andy;
    private int hit = 0;
    private int miss = 0;
    private TextView hitTextView;
    private String hitText = "";
    private TextView missTextView;
    private String missText = "";
    int seconds;
    String spotName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar2);

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

        hitTextView = findViewById(R.id.hitText);
        missTextView = findViewById(R.id.missText);

        mPlayAreaFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.playAreaFragment);
        Log.d("Play Area", mPlayAreaFragment.getArSceneView().getScene().toString());
        buildAndyRenderable();

        scene = mPlayAreaFragment.getArSceneView().getScene();

        new Handler().postDelayed(this::generateAndy, 10000);
        // scheduling the task at fixed rate
        schedulerHandler = schedule.scheduleAtFixedRate(() -> runOnUiThread(() -> placeAndyAtRandom()), 2, 2, TimeUnit.SECONDS);

    }

    @Override
    protected void onPause() {
        super.onPause();
        schedulerHandler.cancel(true);
    }

    private void startTimer() {

        TextView timer = findViewById(R.id.timerText);

        new Thread(() -> {

            seconds = 30;

            for (int i = 0; i< 30 ; i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                seconds--;
                runOnUiThread(() -> timer.setText(seconds + ":" + 00));

                if(seconds == 0){

                    String dsautoID =UserPreferenceData.getString(ArActivity2.this, "autoID");
                    String dsnick =UserPreferenceData.getString(ArActivity2.this, "userNickname");

                    //TODO: .document("dOjolBcphNxaKpzW6L1l") -> .document(dsautoID) / .collection("B") -> .collection(진짜게임한스팟)
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
                                            Log.d("dsAR", "spot : "+ spotName);
                                            Log.d("dsAR", "auto : "+dsautoID);

                                            if (document.exists()) {
                                                Log.d("ARCheck result", "DocumentSnapshot data: " + mmap);
                                                int compareScore = Integer.parseInt(String.valueOf(mmap.get("score")));

                                                //데이터 비교
                                                if (compareScore < hit){
                                                    //Score 데이터 수정
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("score", hit);
                                                    data.put("nickname", dsnick);
                                                    data.put("date", new Timestamp(new Date()));
                                                    data.put("game", "birdCatch");

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
                                                                    Log.w("AR Score rewrite", "AR Score rewrite : failed", e);
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d("ARCheck result", "No such document");
                                                //Score 데이터 삽입
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("score", hit);
                                                data.put("nickname", dsnick);
                                                data.put("date", new Timestamp(new Date()));
                                                data.put("game", "birdCatch");

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
                                                                Log.w("new AR Score", "new AR Score : failed", e);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("ARCheck result", "get failed with ", task.getException());
                                        }
                                    }
                                });

//                        Intent outIntent = new Intent(ArActivity.this, MapActivity.class);
//                        outIntent.putExtra("ScoreResult", 30 - balloonsLeft);
//                        setResult(RESULT_OK, outIntent);
//                        finish();
                        Intent outIntent = new Intent(ArActivity2.this, MapActivity.class);
                        outIntent.putExtra("ScoreResult", hit);
                        setResult(RESULT_OK, outIntent);
                        finish();
                    }

                }
            }

        }).start();

    }


    public void generateAndy() {
        anchor = null;
        mSession = mPlayAreaFragment.getArSceneView().getSession();
        for (Plane plane : mSession.getAllTrackables(Plane.class)) {
            if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING
                    && plane.getTrackingState() == TrackingState.TRACKING) {
                anchor = plane.createAnchor(plane.getCenterPose());
                Log.d("Plane anchor", plane.toString());
                break;
            }
        }

        anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(mPlayAreaFragment.getArSceneView().getScene());
        andy = new Node();
        andy.setParent(anchorNode);
        andy.setRenderable(mAndyRederable);
        Quaternion rotation1 = Quaternion.axisAngle(new Vector3(-1.0f, 0.5f, 0.5f), 90);
        andy.setLocalRotation(rotation1);
        startTimer();
        //andy.setLocalPosition(new Vector3(0f, 0f, 0f));
        andy.setOnTapListener((hitTestResult, motionEvent) -> {
            andy.setEnabled(false);
            hit++;
            String hitText = String.valueOf(hit);
            hitTextView.setText(hitText);
        });
    }

    public void buildAndyRenderable() {
        ModelRenderable.builder().setSource(this, Uri.parse("12213_Bird_v1_l3.sfb"))
                .build()
                .thenAccept(modelRenderable -> mAndyRederable = modelRenderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

    public void placeAndyAtRandom() {
        if (andy != null) {
            if (andy.isEnabled()) {
                miss++;
                missText = String.valueOf(miss);
                missTextView.setText(missText);
            }
            andy.setEnabled(true);
            scene.addChild(andy);
            Random random = new Random();
            int x = random.nextInt(5);
            int z = random.nextInt(10);
            int y = random.nextInt(20);

            z = -z;

            andy.setWorldPosition(new Vector3((float) x, y / 10f,(float) z));
            //andy.setLocalPosition(new Vector3(randFloat(-0.3f,0.8f),0,randFloat(0f,0.8f)));
        }


    }
//
//    public static float randFloat(float min, float max) {
//        Random rand = new Random();
//        float result = rand.nextFloat() * (max - min) + min;
//        return result;
//    }
}