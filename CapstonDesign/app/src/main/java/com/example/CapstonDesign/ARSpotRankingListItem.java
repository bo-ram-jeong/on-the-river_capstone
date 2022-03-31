package com.example.CapstonDesign;

public class ARSpotRankingListItem {
    private int medalimg; //메달이미지
    private String rankingnum;  // 순위
//    private int updownimg; //등락이미지
//    private int updownnum;  // 등락숫자
    private String nickname;  //닉네임
    private String score;   //score

    public void setMedalimg(int medalimg) {
        this.medalimg = medalimg;
    }

    public void setRankingnum(String rankingnum) {
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

    public void setScore(String score) {
        this.score = score;
    }

    public int getMedalimg() {
        return medalimg;
    }

    public String getRankingnum() {
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

    public String getScore() {
        return score;
    }


}
