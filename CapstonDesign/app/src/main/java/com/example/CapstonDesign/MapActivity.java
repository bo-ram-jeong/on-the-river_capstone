package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.skt.Tmap.TMapPoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private ConstraintLayout mView;
    //맵관련 변수
    private GoogleMap gMap;
    private double radius = 20; // 간격 20m
    private LatLng myLatLng; // 현재 위치
    private LatLng currentSpot; // 현재 스팟 위치
    // 위치 정보를 얻기 위한 변수
    private FusedLocationProviderClient fusedLocationClient;
    // 위치 업데이트 속성 설정을 위한 변수
    private LocationRequest locationRequest;
    // 초기 위치를 서울로
    private LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

    // fab 관련 변수
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton mainFab, currentFab;
    private ExtendedFloatingActionButton modeSelectFab;

    private Button btnModeAR, btnModeRun, btnOk, btnCancel,btnConfirm;
    private AlertDialog dialog,arDialog;
    private int mode; // AR미션 or 러닝
    private int i = 1;
    int score = 0; //[AR] 터뜨린 풍선의 개수
    private int arGame = 0; // 1 = balloonGame, 2 = birdCatch
    private boolean arGameModeFinish = false;
    private boolean arGameModeFinish2 = false;
    String dsSpotName = "";
    private ArrayList<Marker> dsSpotList;
    private int arDialogcount = 0;

    //Marker 관련
    private Marker marker;
    private Boolean markerFlag = false;
    private int mClickTime = 0;
    String makerCompare = "-1";
    private ArrayList<Marker> markers;
    private ArrayList<Marker> selectedMarkers;
    private String curMarkerTitle = "-2";

    // FinishActivity 에서 사용
    private Marker startMarker;
    private Marker currentMarker;

    //뒤로가기 버튼 관련
    private long backKeyPressedTime = 0;    // '뒤로' 버튼을 클릭했을 때의 시간
    private long TIME_INTERVAL = 2000;
    private Toast toast;

    //경로 그리기 관련
    private ArrayList<LatLng> arrayPoints;
    String passListURL = null;
    double totalDistance;
    int totalTime, markerTime = 0;
    Polyline polylineFinal;
    Polyline polylineNext;
    PolylineOptions polylineOverlay;
    ArrayList<Polyline> ArrayPolylines;
    ArrayList<LatLng> middleSpot;
    Boolean isDialog = true;
    //운동 시작 관련
    int heightPixels;
    boolean isRun = false;
    FrameLayout running_container;

    //거리,속도 구하기
    double theta, dist = 0.0;
    double bef_lat = 0.0, bef_long = 0.0, cur_lat = 0.0, cur_long = 0.0;
    LatLng startPo;
    double totalDis = 0.0, speed = 0.0;
    LocationManager locationManager;
    private Location mLastlocation = null;

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String autoId;

    //뒤로가기 이벤트 클릭시
    public void onBackPressed() {

        if (markerFlag) {
            markerFlag = false;
            modeChange();

            //상단 ui변경
            WeatherFragment weatherFragment = new WeatherFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, weatherFragment).commit();

            //마커 클릭 변수 초기화
            mClickTime = 0;
            makerCompare = "-1";
            passListURL = null;

            //경로그리기 변수 초기화
            markerTime = 0;
            isDialog = true;

            //카메라 현위치로 이동
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
            gMap.moveCamera(cameraUpdate);

            if (polylineOverlay != null) {
                for (int i = 0; i < ArrayPolylines.size(); i++) {
                    polylineFinal = ArrayPolylines.get(i);
                    polylineFinal.remove();
                }
            }

        } else {
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
                MapActivity.this.finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().hide();
        WeatherFragment weatherFragment = new WeatherFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, weatherFragment).commit();

        autoId = UserPreferenceData.getString(MapActivity.this, "autoID");

        mView = findViewById(R.id.mView);

        //weather Fragment뷰 최상단
        findViewById(R.id.fragment_container).bringToFront();
        running_container = findViewById(R.id.running_container);

        mainFab = findViewById(R.id.mainFab);

        modeSelectFab = findViewById(R.id.modeSelectFab);
        currentFab = findViewById(R.id.currentFab);

        //플로팅버튼 main icon 흰색으로 변경
        mainFab.setColorFilter(ContextCompat.getColor(this, R.color.white));

        mainFab.setOnClickListener(fabClickListener);
        currentFab.setOnClickListener(fabClickListener);
        currentFab.setColorFilter(ContextCompat.getColor(this, R.color.white));

        modeSelectFab.setOnClickListener(fabClickListener);

        // fab 애니메이션 설정
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        // 통합 위치 정보 제공자
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 정보 제공자에 관한 매개변수 설정, 현재 위치를 받기 위함
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100); // 업데이트 간격
        locationRequest.setFastestInterval(100); // 가장 빠른 업데이트 간격
        locationRequest.setSmallestDisplacement(1); // 업데이트 최소 거리 m단위
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 우선순위. 가장 정확한 위치 요청

        //거속시 구하기 위한
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        markers = new ArrayList<>();
        ArrayPolylines = new ArrayList<>();
        middleSpot = new ArrayList<>();
        isDialog = true;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        heightPixels = metrics.heightPixels;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        speed = 0.0;

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        initMap();
        //마커 클릭 이벤트 설정
        gMap.setOnMarkerClickListener(this::onMarkerClick);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void initMap() { // 지도 초기 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        addMarker();
        setDefaultLocation();
        setMyCurrentLocation();
    }

    private void addMarker() { // 스팟 생성
        for (LatLng spot: _TempSpot.spots) {
            BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.ic_spot_1+i-1);
            Bitmap b= bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70,100, false);

            marker = gMap.addMarker(new MarkerOptions()
                    .position(spot)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(String.valueOf(i))
            );
            marker.setTag(smallMarker);
            i++;
            markers.add(marker);
        }
    }


    private void setDefaultLocation() { // 초기 위치 가져오기
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        myLatLng = DEFAULT_LOCATION;

        // 해당 디바이스의 마지막 위치 정보
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                gMap.moveCamera(cameraUpdate);
            }
        });

    }

    private void setMyCurrentLocation() { // 현재 위치 가져오기
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    @Override
    //속도 계산
    public void onLocationChanged(Location location) {
        //자동 속도 계산값
        double getSpeed = Double.parseDouble(String.format("%3f",location.getSpeed()));
        double deltaTime = 0;
        now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);

        //미터로 구한거리를 시간간격으로 나누어 계산 -> m/s
        if (mLastlocation != null){
            deltaTime = (location.getTime() - mLastlocation.getTime())/1000.0;
            speed = mLastlocation.distanceTo(location) / deltaTime;

            if (speed != 0 && isRun){
                mf.showCurrentSpeed(speed);
            }else if (speed == 0.0 && isRun)
                mf.showCurrentSpeed(0.0);
        }

        //현재위치를 지난 위치로 변경
        mLastlocation = location;
        //Log.d("locationChange","locationChan∂ge" + speed);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) { }

    // 통합 위치 정보 제공자가 호출하는 콜백 메서드, 현재 위치 지속적으로 갱신
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location currentLocation = locationList.get(locationList.size() - 1);
                myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                if(startPo!=null && isRun){
                    cur_long = myLatLng.longitude;
                    cur_lat = myLatLng.latitude;

                    theta = bef_long - cur_long;

                    dist = Math.sin(deg2rad(bef_lat)) * Math.sin(deg2rad(cur_lat)) + Math.cos(deg2rad(bef_lat)) * Math.cos(deg2rad(cur_lat)) * Math.cos(deg2rad(theta));

                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = dist * 60 * 1.1515;
                    dist = dist * 1.609344;

                    bef_lat = myLatLng.latitude;
                    bef_long = myLatLng.longitude;

                    dist = Math.round(dist * 10000)/ 10000.0; // 소수점 둘째 자리 계산\
                    Log.d("TESTLOG",dist+"");
                    totalDis += dist;


                    Log.d("dbwldsbwls_dis",""+dist);
                    Log.d("dbwldsbwls_total",""+totalDis);
                }

//                //TODO: 이부분 나중에 주석처리
                if(isRun){
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                gMap.animateCamera(cameraUpdate);
                }
                currentSpot = null;

                // 현재 위치와 스팟들 간의 거리 비교
                for (LatLng spot: _TempSpot.spots) {
                    double distance = SphericalUtil.computeDistanceBetween(spot, myLatLng);

                    // 현재 위치와 10m 이내의 거리에 스팟이 있으면
                    if (distance < radius) {
                        // 해당 스팟을 변수에 저장
                        currentSpot = spot;
                        for (Marker marker:markers){

                            // 스팟들 중 현재 위치와 가까운 스팟을 현재스팟으로 설정
                            if (marker.getPosition().equals(currentSpot)) {
                                Log.d("currentSpot",""+marker.getTitle());
                                currentMarker = marker;
                                curMarkerTitle = marker.getTitle();
                                makerCompare = marker.getTitle();
                            }
                        }

                        if (middleSpot.isEmpty()){
                            middleSpot.add(currentSpot);

                        } else {
                            if (selectedMarkers != null && markerTime+1 <= selectedMarkers.size()) {
                                if (curMarkerTitle.equals(selectedMarkers.get(markerTime).getTitle())) {

                                    if(markerTime+1 == selectedMarkers.size()){
                                        //TODO 다이얼로그 띄워야함
                                        //유대상작업 A=1~ F=6
                                        if(mode == 1) { //AR 모드일때
                                            if(arDialogcount == 0) {
                                                arDialogcount++;
                                                dsSpotName = selectedMarkers.get(markerTime).getTitle();
                                                if (dsSpotName.equals("1") || dsSpotName.equals("3") || dsSpotName.equals("5")) {
                                                    //ballonGame 시작
                                                    arGame = 1;
                                                    arGameModeFinish = true;
                                                    openARConfrimDialog();
                                                } else {
                                                    //birdCatch 시작
                                                    arGame = 2;
                                                    arGameModeFinish = true;
                                                    openARConfrimDialog();
                                                }
                                                dsSpotList.add(selectedMarkers.get(markerTime));
                                                //dsSpotList.add(String.valueOf(selectedMarkers.get(markerTime).getTitle()));
                                                Log.d("saveDS99", "dsSpotList : " + dsSpotList);
                                            }
                                        }else if(isDialog){ //러닝 모드일때
                                            FragmentManager fm = getSupportFragmentManager();
                                            FinishDialogFragment dialogFragment = new FinishDialogFragment();
                                            fm.beginTransaction().add(dialogFragment, "fragment_dialog_test").commitAllowingStateLoss();
                                            //dialogFragment.show(fm, "fragment_dialog_test");
                                            now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                                            if(mf.getActivity() != null) {
                                                Log.d("mfmfmf","");
                                                mf.endTimer();
                                            }
                                            isDialog = false;
                                            dsSpotList.add(selectedMarkers.get(markerTime));
                                            //dsSpotList.add(String.valueOf(selectedMarkers.get(markerTime).getTitle()));
                                            Log.d("saveDS3", "dsSpotList : " + dsSpotList);
                                        }

                                    }else {
                                        //유대상작업 A=1~ F=6
                                        if(mode == 1) {
                                            dsSpotName = selectedMarkers.get(markerTime).getTitle();
                                            if (dsSpotName.equals("1") || dsSpotName.equals("3") || dsSpotName.equals("5")) {
                                                //ballonGame 시작
                                                arGame = 1;
                                                openARConfrimDialog();
                                            } else {
                                                //birdCatch 시작
                                                arGame = 2;
                                                openARConfrimDialog();
                                            }
                                        }

                                        polylineNext.remove();
                                        String url = trackingNext(currentSpot, selectedMarkers.get(markerTime+1).getPosition());
                                        //검색한 url을 가지고 데이터를 파싱한다
                                        NetworkTask networkTask = new NetworkTask(url, null);
                                        networkTask.execute();

                                        markerTime+=1;
                                        middleSpot.add(currentSpot);

                                        dsSpotList.add(selectedMarkers.get(markerTime-1));
                                        //dsSpotList.add(String.valueOf(selectedMarkers.get(markerTime-1).getTitle()));
                                        Log.d("saveDS2", "dsSpotList : " + dsSpotList);

                                    }
                                }


                            }

                        }
                    }
                }

                // 현재 위치의 10m 이내에 스팟이 있을 경우에만 운동시작 버튼이 보이도록
                if (currentSpot != null && !isRun) {
                    modeSelectFab.setVisibility(View.VISIBLE);
                } else {
                    modeSelectFab.setVisibility(View.INVISIBLE);
                }
            }

        }
    };
    private double deg2rad(double deg){

        return (double)(deg * Math.PI / (double)180d);

    }
    private double rad2deg(double rad){

        return (double)(rad * (double)180d / Math.PI);

    }

    private final View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mainFab:
                    anim();
                    Intent intent = new Intent(getApplicationContext(),MyPage.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    overridePendingTransition(R.anim.horizon_exit, R.anim.horizon_enter);
                    finish();
                    break;
                case R.id.home:
                    anim();
                    break;
                case R.id.modeSelectFab:
                    //경로 선택 버튼과 운동 시작 버튼을 구분
                    if(!markerFlag){
                        openModeSelectDialog();
                    }
                    else{//운동 시작 버튼
                        if(mClickTime < 1){
                            Toast.makeText(MapActivity.this, "최소 2개 이상의 경로를 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            runningMode(true);
                            isRun = true;

                            slideView(mView,heightPixels,heightPixels/3*2);
                            slideUpView(running_container,0,heightPixels/3);

                            String url=trackingNext(currentSpot,selectedMarkers.get(0).getPosition());

                            //검색한 url을 가지고 데이터를 파싱한다
                            NetworkTask networkTask = new NetworkTask(url, null);
                            networkTask.execute();
                        }//heightPixels/3
                    }
                    break;
                case R.id.currentFab:
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                    gMap.animateCamera(cameraUpdate);
                    break;
            }
        }
    };

    private void anim() { // fab 버튼 애니메이션
//        if (isFabOpen) {
//            isFabOpen = false;
//        } else {
//            isFabOpen = true;
//        }
    }
    //대상작업
    private void openARConfrimDialog() { // AR 게임 시작 다이얼로그
        View view2  = View.inflate(getApplicationContext(),R.layout.dialog_ar_confirm, null);
        btnConfirm = view2.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(arDialogButtonClickListener);

        if(! MapActivity.this.isFinishing()) {
            if (!arGameModeFinish2) {
            arDialog = new AlertDialog.Builder(this).create();
            arDialog.setView(view2);
                arDialog.show();
            }
        }
    }

    View.OnClickListener arDialogButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnConfirm:
                    //대상작업 : AR게임 오픈
                    if(arGame == 1){
                        //[AR] balloonGame Start
                        arDialog.dismiss();
                        now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                        if(mf.getActivity() != null) {  //nullpointError발생
                            Log.d("mfmfmf","");
                            mf.stopTimer(true);//타이머 stop
                        }
                        Intent intent = new Intent(MapActivity.this, ArActivity.class);
                        intent.putExtra("spotName", dsSpotName);
                        startActivityForResult(intent, 100);
                    }else if (arGame == 2){
                        //[AR] birdCatch Start
                        now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                        mf.stopTimer(true);//타이머 stop
                        arDialog.dismiss();
                        Intent intent2 = new Intent(getApplicationContext(),ArActivity2.class);
                        intent2.putExtra("spotName", dsSpotName);
                        startActivityForResult(intent2, 200);
                    }
                    break;
            }
            if (arGameModeFinish){
                arGameModeFinish2 = true;
            }
        }
    };

    private void openModeSelectDialog() { // 모드 선택 다이얼로그
        mode = 0;
        View view  = View.inflate(getApplicationContext(),R.layout.dialog, null);
        btnModeAR = view.findViewById(R.id.missionOn);
        btnModeRun = view.findViewById(R.id.missionOff);
        btnOk = view.findViewById(R.id.btnOk);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnModeAR.setOnClickListener(dialogButtonClickListener);
        btnModeRun.setOnClickListener(dialogButtonClickListener);
        btnOk.setOnClickListener(dialogButtonClickListener);
        btnCancel.setOnClickListener(dialogButtonClickListener);

        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        dialog.show();

    }

    View.OnClickListener dialogButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.missionOn: // AR미션 선택 시
                    mode = 1;
                    btnModeAR.setBackgroundColor(Color.parseColor("#3498DB"));
                    btnModeAR.setTextColor(Color.parseColor("#ffffff"));
                    btnModeRun.setBackgroundColor(Color.parseColor("#ffffff"));
                    btnModeRun.setTextColor(Color.parseColor("#000000"));
                    break;

                case R.id.missionOff: // 러닝 선택 시
                    mode = 2;
                    btnModeRun.setBackgroundColor(Color.parseColor("#3498DB"));
                    btnModeRun.setTextColor(Color.parseColor("#ffffff"));
                    btnModeAR.setBackgroundColor(Color.parseColor("#ffffff"));
                    btnModeAR.setTextColor(Color.parseColor("#000000"));
                    break;

                case R.id.btnOk:
                    if (mode == 1){
                        //[AR] AR Game Start
//                        dialog.dismiss();
//                        Intent intent = new Intent(MapActivity.this, ArActivity.class);
//                        startActivityForResult(intent, 100);
                        dialog.dismiss();

                        markerFlag = true;
                        modeChange();
                        SelectMarkerTab selectMarkerTab = new SelectMarkerTab();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectMarkerTab).commit();
                        findViewById(R.id.fragment_container).bringToFront();
                        selectedMarkers = new ArrayList<>();
                        startMarker = currentMarker;
                        passListURL = null;

                        dsSpotList = new ArrayList<>();
                        dsSpotList.add(startMarker);
                        //dsSpotList.add(String.valueOf(startMarker.getTitle()));
                        Log.d("saveDS", "dsSpotList : " + dsSpotList);

                    } else if (mode == 2){

                        dialog.dismiss();

                        markerFlag = true;
                        modeChange();
                        SelectMarkerTab selectMarkerTab = new SelectMarkerTab();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectMarkerTab).commit();
                        findViewById(R.id.fragment_container).bringToFront();
                        selectedMarkers = new ArrayList<>();
                        startMarker = currentMarker;
                        passListURL = null;

                        dsSpotList = new ArrayList<>();
                        dsSpotList.add(startMarker);
                        //dsSpotList.add(String.valueOf(startMarker.getTitle()));
                        Log.d("saveDS", "dsSpotList : " + dsSpotList);


                    } else {
                        Toast.makeText(MapActivity.this, "모드를 선택하세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btnCancel:
                    dialog.dismiss();
                    break;
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                // 리퀘스트 코드가 100이면 AR1 결과 처리
                score = data.getIntExtra("ScoreResult", 0);
                Toast.makeText(this, "터뜨린 풍선 : " + score, Toast.LENGTH_LONG).show();

                String s1 = "게임이 종료되었습니다.";
                String s2 = "터뜨린 풍선의 개수 : ";
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("TIME OVER").setMessage(s1 + "\n" + s2 + score);

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(arGameModeFinish){ //마지막 경로일 시
                            FragmentManager fm = getSupportFragmentManager();
                            FinishDialogFragment dialogFragment = new FinishDialogFragment();
                            fm.beginTransaction().add(dialogFragment, "fragment_dialog_test").commitAllowingStateLoss();
                            //dialogFragment.show(fm, "fragment_dialog_test");
                            now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                            mf.endTimer();
                            isDialog = false;
                            arGameModeFinish = false;
                        }else{
                            now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                            mf.stopTimer(false);
                            arDialogcount = 0;
                        }
                    }
                });
                dlg.show();
            } else if (requestCode == 200){
                // 리퀘스트 코드가 200이면 AR2 결과 처리
                score = data.getIntExtra("ScoreResult", 0);
                Toast.makeText(this, "잡은 새: " + score, Toast.LENGTH_LONG).show();

                String s1 = "게임이 종료되었습니다.";
                String s2 = "잡은 새 : ";
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("TIME OVER").setMessage(s1 + "\n" + s2 + score);

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(arGameModeFinish){ //마지막 경로일 시
                            FragmentManager fm = getSupportFragmentManager();
                            FinishDialogFragment dialogFragment = new FinishDialogFragment();
                            fm.beginTransaction().add(dialogFragment, "fragment_dialog_test").commitAllowingStateLoss();
                            //dialogFragment.show(fm, "fragment_dialog_test");
                            now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                            mf.endTimer();
                            isDialog = false;
                            arGameModeFinish = false;
                        }
                        else{
                            now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
                            mf.stopTimer(false);
                            arDialogcount = 0;
                        }
                    }
                });
                dlg.show();
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        if (markerFlag && !marker.getTitle().equals(makerCompare)){
            mClickTime++;
            makerCompare = marker.getTitle();

            if (mClickTime > 0 && mClickTime <= 6){
                //Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                selectedMarkers.add(marker);
                LatLng lastSpot = selectedMarkers.get(mClickTime-1).getPosition();

                SelectMarkerTab mf = (SelectMarkerTab) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                mf.addListMarker(marker.getTitle());

                //검색한 좌표와 현재 위치를 넣어서 url을 가지고 온다.

                String url=TMapWalkerTrackerURL(currentSpot,lastSpot);
                startPo = currentSpot;
                bef_lat = startPo.latitude;
                bef_long = startPo.longitude;

                //검색한 url을 가지고 데이터를 파싱한다
                NetworkTask networkTask = new NetworkTask(url, null);
                networkTask.execute();

            }else if (mClickTime > 6){
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("경로 설정 안내").setMessage("경로 설정은 6개까지만 가능합니다.");

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                dlg.show();
            }

        }

        return true;
    }
    private void modeChange(){
        if(markerFlag){
            modeSelectFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapActivity.this,R.color.white)));
            modeSelectFab.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(MapActivity.this,R.color.main)));
            modeSelectFab.setText(" Start");
        }else if (!markerFlag){
            modeSelectFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapActivity.this,R.color.main)));
            modeSelectFab.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(MapActivity.this,R.color.white)));
            modeSelectFab.setText(" 모드선택");
        }
    }
    private void runningMode(boolean isRun){
        if(isRun){
            markerFlag = false;
            currentFab.setVisibility(View.INVISIBLE);
            modeSelectFab.setVisibility(View.INVISIBLE);
            mainFab.setVisibility(View.INVISIBLE);
        }else {
            currentFab.setVisibility(View.VISIBLE);
            modeSelectFab.setVisibility(View.VISIBLE);
            mainFab.setVisibility(View.VISIBLE);
        }
    }
    public void showMessage() {
        toast = Toast.makeText(MapActivity.this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
    public String TMapWalkerTrackerURL(LatLng startPoint, LatLng endPoint){
        String url = null;
        passListURL = null;

        if (selectedMarkers.size()==1){
            //출발점->마지막 : 마지막이 선택된 경우
            passListURL = null;
        }
        else if (selectedMarkers.size()==2){
            //출발점-> 경유지,마지막 : 경유지와 마지막이 선택된 경우

            String middleX = new Double(selectedMarkers.get(0).getPosition().longitude).toString();
            String middleY = new Double(selectedMarkers.get(0).getPosition().latitude).toString();

                passListURL = middleX + "," + middleY;


        }
        else if (selectedMarkers.size()>=3){
            for(int i = 0 ; i < selectedMarkers.size()-1 ; i++) {

                String middleX = new Double(selectedMarkers.get(i).getPosition().longitude).toString();
                String middleY = new Double(selectedMarkers.get(i).getPosition().latitude).toString();

                if (passListURL == null){
                    passListURL = (middleX + "," + middleY);
                }else{
                    passListURL += ("_"+ (middleX + "," + middleY));
                }


            }
        }

        try {
            String appKey = "l7xxdfdc68ab099f42058b931e7108ce0586";

            String startX = new Double(startPoint.longitude).toString();
            String startY = new Double(startPoint.latitude).toString();
            String endX = new Double(endPoint.longitude).toString();
            String endY = new Double(endPoint.latitude).toString();

            String startName = URLEncoder.encode("출발지", "UTF-8");

            String endName = URLEncoder.encode("도착지", "UTF-8");


            url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=result&appKey=" + appKey
                    + "&reqCoordType=WGS84GEO"
                    + "&resCoordType=WGS84GEO"
                    + "&startX=" + startX
                    + "&startY=" + startY
                    + "&endX=" + endX
                    + "&endY=" + endY
                    + "&startName=" + startName
                    + "&endName=" + endName
                    + "&searchOption=10";

            if (passListURL != null) {
                url = url +"&passList=" + passListURL;
            }
            Log.d("eotkdeotkd", ""+passListURL);
            Log.d("eotkdeotkd2", ""+url);


        } catch ( UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;

    }
    public String trackingNext(LatLng startPoint, LatLng endPoint){
        String url = null;

        try {
            String appKey = "l7xxdfdc68ab099f42058b931e7108ce0586";

            String startX = new Double(startPoint.longitude).toString();
            String startY = new Double(startPoint.latitude).toString();
            String endX = new Double(endPoint.longitude).toString();
            String endY = new Double(endPoint.latitude).toString();

            String startName = URLEncoder.encode("출발지", "UTF-8");

            String endName = URLEncoder.encode("도착지", "UTF-8");


            url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=result&appKey=" + appKey
                    + "&reqCoordType=WGS84GEO"
                    + "&resCoordType=WGS84GEO"
                    + "&startX=" + startX
                    + "&startY=" + startY
                    + "&endX=" + endX
                    + "&endY=" + endY
                    + "&startName=" + startName
                    + "&endName=" + endName
                    + "&searchOption=10";

        } catch ( UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        ArrayList<LatLng> latLngArrayList=new ArrayList<LatLng>();

//        TextView totalDistanceText = findViewById(R.id.totalDistance);
//        TextView totalTimeText = findViewById(R.id.totalTime);
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            ArrayList<LatLng> inputPoly = new ArrayList<>();
            try {
                //전체 데이터를 제이슨 객체로 변환
                JSONObject root = new JSONObject(s);
                //System.out.println("제일 상위 "+root);


                //총 경로 횟수 featuresArray에 저장
                JSONArray featuresArray = root.getJSONArray("features");

                for (int i = 0; i < featuresArray.length(); i++) {
                    JSONObject featuresIndex = (JSONObject) featuresArray.get(i);
                    JSONObject geometry = featuresIndex.getJSONObject("geometry");

                    String type = geometry.getString("type");

                    if (type.equals("LineString")) {


                        JSONArray coordinatesArray = geometry.getJSONArray("coordinates");

                        for (int j = 0; j < coordinatesArray.length(); j++) {

                            JSONArray pointArray = (JSONArray) coordinatesArray.get(j);
                            double longitude = Double.parseDouble(pointArray.get(0).toString());
                            double latitude = Double.parseDouble(pointArray.get(1).toString());

                            latLngArrayList.add(new LatLng(latitude, longitude));
                        }

                    }

                    if (type.equals("Point")) {
                        JSONObject properties = featuresIndex.getJSONObject("properties");
                        try {
                            totalDistance = Integer.parseInt(properties.getString("totalDistance"));

                            if(!isRun){
                                SelectMarkerTab mf = (SelectMarkerTab) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                mf.addTotalDistance(totalDistance);

                                totalTime = Integer.parseInt(properties.getString("totalTime"));
                                mf.addTotalTime(totalTime);
                            }

                        } catch (Exception e) {

                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!isRun){
                polylineOverlay = new PolylineOptions();
                polylineOverlay.color(Color.parseColor("#3498DB"));
                polylineOverlay.width(15);
                polylineOverlay.addAll(latLngArrayList);
                polylineFinal = gMap.addPolyline(polylineOverlay);
                ArrayPolylines.add(polylineFinal);

            }
            else {
                polylineOverlay = new PolylineOptions();
                polylineOverlay.color(Color.parseColor("#ff22d2"));
                polylineOverlay.width(15);
                polylineOverlay.addAll(latLngArrayList);
                polylineNext = gMap.addPolyline(polylineOverlay);

            }

        }
    }

    public String CurMarker(){
        return curMarkerTitle;
    }

    //운동 중 화면 애니메이션 - 액티비티
    public void slideView(View view,
                                 int currentHeight,
                                 int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(1000);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
    //운동 중 화면 애니메이션 - 프래그먼트
    public void slideUpView(View view,
                            int currentHeight,
                            int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(1000);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
            if (view.getLayoutParams().height > value.intValue()/2){

            }
            now_running_fragment running_fragment = new now_running_fragment();
            getSupportFragmentManager().beginTransaction().add(R.id.running_container,running_fragment).commit();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();

    }
    public void slideFullUpView(
                             int currentHeight,
                             int newHeight,
                             int currentMapHeight,
                             int newMapHeight) {
        Log.d("dbwlsdbwls","dbwls");

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(1000);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            running_container.getLayoutParams().height = value.intValue();
            running_container.requestLayout();
        });

        ValueAnimator slideMapAnimator = ValueAnimator
                .ofInt(currentMapHeight, newMapHeight)
                .setDuration(1000);

        slideMapAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            mView.getLayoutParams().height = value.intValue();
            mView.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);

        AnimatorSet animationSet2 = new AnimatorSet();
        animationSet2.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet2.play(slideMapAnimator);

        animationSet.start();
        animationSet2.start();

    }

    public void slideFullDownView(
            int currentHeight,
            int newHeight,
            int currentMapHeight,
            int newMapHeight) {
        Log.d("dbwlsdbwls","dbwls");

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(1000);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            running_container.getLayoutParams().height = value.intValue();
            running_container.requestLayout();
        });

        ValueAnimator slideMapAnimator = ValueAnimator
                .ofInt(currentMapHeight, newMapHeight)
                .setDuration(1000);

        slideMapAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            mView.getLayoutParams().height = value.intValue();
            mView.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);

        AnimatorSet animationSet2 = new AnimatorSet();
        animationSet2.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet2.play(slideMapAnimator);

        animationSet.start();
        animationSet2.start();

    }
    public void goToFinishFromDialog(){
        int totalTime = 0;
        now_running_fragment mf = (now_running_fragment) getSupportFragmentManager().findFragmentById(R.id.running_container);
        totalTime = mf.getElapsed();
        goToFinish(totalTime);
    }
    public void goToFinish(int totalTime) {
        locationManager.removeUpdates(this);

        ArrayList<Bitmap> markerImgList = new ArrayList<>();
        char markerNameChar;
        String markerName;

        int myRouteCnt = 0;
        String myRoute = "";

        for(Marker marker:dsSpotList){
            myRouteCnt++;
            markerNameChar = (char) (Integer.parseInt(marker.getTitle())+64);
            markerName = String.valueOf(markerNameChar);
            if (myRoute == "") {
                myRoute += markerName;
            }
            else {
                myRoute += "," + markerName;
            }
            markerImgList.add((Bitmap) marker.getTag());
        }

        Date mDate = new Date();

        int hour = totalTime/(60*60);
        int minute = totalTime/60-(hour*60);
        int second = totalTime%60;

        String strTime = String.format("%02d",hour) + ":" + String.format("%02d",minute) + ":" + String.format("%02d",second);

        Double realDistance = totalDistance / 1000;
        realDistance = Math.round(realDistance * 100) / 100.0;

        Double myDistance = totalDis;
        myDistance = Math.round(myDistance * 100) / 100.0;

        String currentMode = "";
        if (mode==1){
            currentMode = "AR 모드";
        } else {
            currentMode = "러닝 모드";
        }
        Log.d("TESTLOG",myDistance+"");
        double perHour = (myDistance * 3600) / totalTime;
        double mets = 0.0;

        if (perHour < 3.2){
            mets = 2;
        }else if(perHour < 4.7){
            mets = 3;
        }else if(perHour < 6.4) {
            mets = 4;
        }else if(perHour < 7.2) {
            mets = 5;
        }else if(perHour < 7.9) {
            mets = 7;
        } else if(perHour < 8.5) {
            mets = 10;
        }else{
            mets = 12;
        }

        double finalMets = mets;
        Double finalMyDistance = myDistance;
        Double finalRealDistance = realDistance;
        String finalMyRoute = myRoute;
        String finalCurrentMode = currentMode;
        int finalMyRouteCnt = myRouteCnt;

        new dbValues().getWeight(new GetValuesListener() {
            @Override
            public void onWeightLoaded(int weight) {
                int kcal = (int) (3.5 * finalMets * weight * (totalTime/60) * 5 / 1000);
                Map<String, Object> map = new HashMap<>();
                map.put("date", mDate);
                map.put("distance", finalMyDistance);
                map.put("kcal", kcal);
                map.put("mode", finalCurrentMode);
                map.put("route", finalMyRoute);
                map.put("routeCnt", finalMyRouteCnt);
                map.put("runningTime", totalTime);

                db.collection("users").document(autoId)
                        .collection("Records")
                        .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Intent intent = new Intent(MapActivity.this,FinishActivity.class);
                        intent.putExtra("realDistance", finalRealDistance+"");
                        intent.putExtra("myDistance",finalMyDistance+"");
                        intent.putExtra("time",strTime);
                        intent.putExtra("kcal",kcal+"");
                        intent.putExtra("markerImgList",markerImgList);

                        startActivity(intent);
                        overridePendingTransition(R.anim.horizon_exit2, R.anim.horizon_enter2);

                        finish();
                    }
                });
            }
        });
    }
    private interface GetValuesListener{
        void onWeightLoaded(int weight);
    }

    public class dbValues {
        public void getWeight(final GetValuesListener valuesListener) {
            DocumentReference productRef = db.collection("users").document(autoId);
            productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> map = document.getData();
                            int temp = Integer.parseInt(String.valueOf(map.get("weight")));
                            valuesListener.onWeightLoaded(temp);
                        }
                    }
                }
            });
        }
    }
}
