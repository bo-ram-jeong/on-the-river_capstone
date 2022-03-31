package com.example.CapstonDesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.maps.android.SphericalUtil;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private ExtendedFloatingActionButton startFab, resetFab;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap gMap;
    private LatLng currentSpot; // 현재 스팟 위치
    private ArrayList<Marker> markers;
    private ArrayList<Marker> selectedMarkers;
    private int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        getSupportActionBar().hide();

        SelectMarkerTab selectMarkerTab = new SelectMarkerTab();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, selectMarkerTab).commit();
        findViewById(R.id.fragment_container).bringToFront();

        markers = new ArrayList<>();
        selectedMarkers = new ArrayList<>();

        Intent intent = getIntent();
        currentSpot = new LatLng(intent.getDoubleExtra("lat",0),
                intent.getDoubleExtra("lng",0));

        startFab = findViewById(R.id.startFab);
        resetFab = findViewById(R.id.resetFab);

        startFab.setOnClickListener(this);
        resetFab.setOnClickListener(this);

        // 위치 정보 제공자에 관한 매개변수 설정, 현재 위치를 받기 위함
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 업데이트 간격
        locationRequest.setFastestInterval(1000); // 가장 빠른 업데이트 간격
        locationRequest.setSmallestDisplacement(1); // 업데이트 최소 거리 m단위
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 우선순위. 가장 정확한 위치 요청

        MapFragment mapFragment2 = (MapFragment) getFragmentManager().findFragmentById(R.id.map2);
        mapFragment2.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        initMap();
    }
    private void initMap() { // 지도 초기 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        addMarker();
        setDefaultLocation();
    }

    private void addMarker() { // 스팟 생성
        if (markers.size() >0)
            for (Marker marker:markers){
                marker.remove();
            }
        markers.clear();

        for (LatLng spot: _TempSpot.spots) {
            BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.ic_spot_1+i-1);
            Bitmap b= bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70,100, false);

            Marker marker = gMap.addMarker(new MarkerOptions()
                    .position(spot)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(String.valueOf(i))
            );
            i++;
        }
    }

    private void setDefaultLocation() { // 현재 스팟을 첫번째 루트로 지정 및 카메라 이동
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        selectedMarkers.clear();
        for (Marker marker:markers){

            // 스팟들 중 현재 위치와 가까운 스팟을 현재스팟으로 설정
            if (marker.getPosition().equals(currentSpot)) {
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_spot_1));
                marker.setTitle("현재 위치");
                selectedMarkers.add(marker);
            }
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentSpot, 15);
        gMap.moveCamera(cameraUpdate);
    }


    private void setMyCurrentLocation() { // 현재 위치 가져오기
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    // 통합 위치 정보 제공자가 호출하는 콜백 메서드, 현재 위치 지속적으로 갱신
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
        }
    };
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (selectedMarkers.contains(marker)) { // 선택한 마커가 이미 선택된 마커일 경우
            if (selectedMarkers.get(0).equals(marker)) { // 첫번째 마커를 재선택한 경우 pass

            } else { // 첫번째 마커 이외의 마커를 재선택한 경우 경로에서 제거
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                selectedMarkers.remove(marker);
            }
        } else { // 선택한 마커가 아직 선택되지 않은 마커일 경우 경로에 추가
            selectedMarkers.add(marker);
        }

        int i = 0;
        for (Marker selectedMarker:selectedMarkers){
            selectedMarker.setIcon(BitmapDescriptorFactory.
                    fromResource(getResources().getIdentifier("ic_spot" + i, "drawable"
                            , getApplicationContext().getPackageName())));
            i++;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetFab: // 리셋 버튼 클릭 시
                initMap();
                Toast.makeText(this, "경로 설정이 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.startFab: // 스타트 버튼 클릭 시
                if (selectedMarkers.size()<2) { // 선택한 스팟이 2개보다 적으면
                    Toast.makeText(this, "2개 이상의 스팟을 선택하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "운동 시작", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}