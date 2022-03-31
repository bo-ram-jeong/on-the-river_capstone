package com.example.CapstonDesign;

import android.widget.ImageView;

public class SelectListData {

    private int marker;
    private String tvNumber;

    public SelectListData(int marker,String tvNumber){
        this.marker = marker;
        this.tvNumber = tvNumber;
    }

    public int getMarker(){
        return marker;
    }
    public void setMarker(int marker){
        this.marker = marker;
    }
    public String getTvNumber(){
        return tvNumber;
    }

    public void setTvNumber(String tvNumber) {
        this.tvNumber = tvNumber;
    }
}
