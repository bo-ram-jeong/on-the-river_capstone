package com.example.CapstonDesign;

import android.graphics.drawable.Drawable;

public class ARGameRankingListItem {
    private int medalimg; //메달이미지
    private int rankingnum;  // 순위
//    private int updownimg; //등락이미지
//    private int updownnum;  // 등락숫자
    private String nickname;  //닉네임
    private int score;   //score

    public void setMedalimg(int medalimg) {
        this.medalimg = medalimg;
    }

    public void setRankingnum(int rankingnum) {
        this.rankingnum = rankingnum;
    }

//    public void setUpdownimg(int updownimg) {
//        this.updownimg = updownimg;
//    }
//
//    public void setUpdownnum(int updownnum) {
//        this.updownnum = updownnum;
//    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMedalimg() {
        return medalimg;
    }

    public int getRankingnum() {
        return rankingnum;
    }

//    public int getUpdownimg() {
//        return updownimg;
//    }
//
//    public int getUpdownnum() {
//        return updownnum;
//    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }


}
