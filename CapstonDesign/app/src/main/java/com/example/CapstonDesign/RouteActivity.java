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
    private LatLng currentSpot; // ?????? ?????? ??????
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

        // ?????? ?????? ???????????? ?????? ???????????? ??????, ?????? ????????? ?????? ??????
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // ???????????? ??????
        locationRequest.setFastestInterval(1000); // ?????? ?????? ???????????? ??????
        locationRequest.setSmallestDisplacement(1); // ???????????? ?????? ?????? m??????
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // ????????????. ?????? ????????? ?????? ??????

        MapFragment mapFragment2 = (MapFragment) getFragmentManager().findFragmentById(R.id.map2);
        mapFragment2.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        initMap();
    }
    private void initMap() { // ?????? ?????? ??????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        addMarker();
        setDefaultLocation();
    }

    private void addMarker() { // ?????? ??????
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

    private void setDefaultLocation() { // ?????? ????????? ????????? ????????? ?????? ??? ????????? ??????
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        selectedMarkers.clear();
        for (Marker marker:markers){

            // ????????? ??? ?????? ????????? ????????? ????????? ?????????????????? ??????
            if (marker.getPosition().equals(currentSpot)) {
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_spot_1));
                marker.setTitle("?????? ??????");
                selectedMarkers.add(marker);
            }
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentSpot, 15);
        gMap.moveCamera(cameraUpdate);
    }


    private void setMyCurrentLocation() { // ?????? ?????? ????????????
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    // ?????? ?????? ?????? ???????????? ???????????? ?????? ?????????, ?????? ?????? ??????????????? ??????
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
        }
    };
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (selectedMarkers.contains(marker)) { // ????????? ????????? ?????? ????????? ????????? ??????
            if (selectedMarkers.get(0).equals(marker)) { // ????????? ????????? ???????????? ?????? pass

            } else { // ????????? ?????? ????????? ????????? ???????????? ?????? ???????????? ??????
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                selectedMarkers.remove(marker);
            }
        } else { // ????????? ????????? ?????? ???????????? ?????? ????????? ?????? ????????? ??????
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
            case R.id.resetFab: // ?????? ?????? ?????? ???
                initMap();
                Toast.makeText(this, "?????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.startFab: // ????????? ?????? ?????? ???
                if (selectedMarkers.size()<2) { // ????????? ????????? 2????????? ?????????
                    Toast.makeText(this, "2??? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "?????? ??????", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}