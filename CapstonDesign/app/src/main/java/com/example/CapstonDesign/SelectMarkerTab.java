package com.example.CapstonDesign;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SelectMarkerTab extends Fragment {
    private ArrayList<SelectListData> arrayList;
    private SelectListAdpater selectListAdpater;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String makerId = null;
    private String curMaker = null;
    SelectListData selectListData = null;
    private TextView tvTime,tvDis;
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_marker_tab, container, false);
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);

        selectListAdpater = new SelectListAdpater(arrayList);
        recyclerView.setAdapter(selectListAdpater);

        tvDis = view.findViewById(R.id.tvDis);
        tvTime =view.findViewById(R.id.tvTime);

        curMaker = ((MapActivity) getActivity()).CurMarker();
        String size = String.valueOf(arrayList.size()+1);
        switch (curMaker) {
            case "1":
                selectListData = new SelectListData(R.drawable.ic_spot_1, "시작점");
                break;
            case "2":
                selectListData = new SelectListData(R.drawable.ic_spot_2, "시작점");
                break;
            case "3":
                selectListData = new SelectListData(R.drawable.ic_spot_3, "시작점");
                break;
            case "4":
                selectListData = new SelectListData(R.drawable.ic_spot_4, "시작점");
                break;
            case "5":
                selectListData = new SelectListData(R.drawable.ic_spot_5, "시작점");
                break;
            case "6":
                selectListData = new SelectListData(R.drawable.ic_spot_6, "시작점");
                break;

        }
        arrayList.add(selectListData);
        selectListAdpater.notifyDataSetChanged();


        return view;
    }

    void addListMarker(String makerId) {
           // makerId = getArguments().getString("marker"); // 전달한 key 값

            String size = String.valueOf(arrayList.size());
            switch (makerId) {
                case "1":
                    selectListData = new SelectListData(R.drawable.ic_spot_1, size);
                    break;
                case "2":
                    selectListData = new SelectListData(R.drawable.ic_spot_2, size);
                    break;
                case "3":
                    selectListData = new SelectListData(R.drawable.ic_spot_3, size);
                    break;
                case "4":
                    selectListData = new SelectListData(R.drawable.ic_spot_4, size);
                    break;
                case "5":
                    selectListData = new SelectListData(R.drawable.ic_spot_5, size);
                    break;
                case "6":
                    selectListData = new SelectListData(R.drawable.ic_spot_6, size);
                    break;

            }
            arrayList.add(selectListData);
            selectListAdpater.notifyDataSetChanged();
    }
    void addTotalDistance(double totalDistance) {
        tvDis.setText("총 거리 : "+ totalDistance/1000+"km");
    }
    void addTotalTime(int totalTime) {
        tvTime.setText("총 시간 : "+ totalTime/60+"분");
    }

}