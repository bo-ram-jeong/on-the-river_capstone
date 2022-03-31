package com.example.CapstonDesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ARSpotRankingAdapter extends BaseAdapter {
    Context context;

    //Adapter에 추가될 데이터를 저장하기 위한 ArrayList<>
    private ArrayList<ARSpotRankingListItem> arSpotRankingListItemList = new ArrayList<>();


    public ARSpotRankingAdapter() {

    }

    @Override
    public int getCount() {
        return arSpotRankingListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return arSpotRankingListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  //getView()에서 항목을 구성, 이벤트를 처리
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.arspot_list_item, parent, false);
        }

        ImageView ranking_medal = (ImageView) convertView.findViewById(R.id.ranking_medal);
        TextView ranking_numbering = (TextView) convertView.findViewById(R.id.ranking_numbering);
//        ImageView ranking_updown = (ImageView) convertView.findViewById(R.id.ranking_updown);
//        TextView ranking_diff = (TextView) convertView.findViewById(R.id.ranking_diff);
        TextView ranking_name = (TextView) convertView.findViewById(R.id.ranking_name);
        TextView ranking_score = (TextView) convertView.findViewById(R.id.ranking_score);


        // Data Set(arGameRankingListItem)에서 position에 위치한 데이터 참조 획득
        ARSpotRankingListItem arSpotRankingListItem = arSpotRankingListItemList.get(position);


        // 아이템 내 각 위젯에 데이터 반영
        ranking_medal.setImageResource(arSpotRankingListItem.getMedalimg());
        ranking_numbering.setText(String.valueOf(arSpotRankingListItem.getRankingnum()));
//        ranking_updown.setImageResource(arSpotRankingListItem.getUpdownimg());
//        ranking_diff.setText(String.valueOf(arSpotRankingListItem.getUpdownnum()));
        ranking_name.setText(String.valueOf(arSpotRankingListItem.getNickname()));
        ranking_score.setText(String.valueOf(arSpotRankingListItem.getScore()));

        return convertView;
    }

    public void addItem(int ranking_medal, String ranking_numbering, String ranking_name, String ranking_score) {
        ARSpotRankingListItem item = new ARSpotRankingListItem();

        item.setMedalimg(ranking_medal);
        item.setRankingnum(ranking_numbering);
//        item.setUpdownimg(ranking_updown);
//        item.setUpdownnum(ranking_diff);
        item.setNickname(ranking_name);
        item.setScore(ranking_score);

        arSpotRankingListItemList.add(item);
    }

}
