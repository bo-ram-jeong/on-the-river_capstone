package com.example.CapstonDesign;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ARGameRankingAdapter extends BaseAdapter {
    Context context;

    //Adapter에 추가될 데이터를 저장하기 위한 ArrayList<>
    private ArrayList<ARGameRankingListItem> arGameRankingListItemList = new ArrayList<ARGameRankingListItem>();


    public ARGameRankingAdapter() {

    }

    @Override
    public int getCount() {
        return arGameRankingListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return arGameRankingListItemList.get(position);
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
            convertView = inflater.inflate(R.layout.argame_list_item, parent, false);
        }

        ImageView ranking_medal = (ImageView) convertView.findViewById(R.id.ranking_medal);
        TextView ranking_numbering = (TextView) convertView.findViewById(R.id.ranking_numbering);
//        ImageView ranking_updown = (ImageView) convertView.findViewById(R.id.ranking_updown);
//        TextView ranking_diff = (TextView) convertView.findViewById(R.id.ranking_diff);
        TextView ranking_name = (TextView) convertView.findViewById(R.id.ranking_name);
        TextView ranking_score = (TextView) convertView.findViewById(R.id.ranking_score);


        // Data Set(arGameRankingListItem)에서 position에 위치한 데이터 참조 획득
        ARGameRankingListItem arGameRankingListItem = arGameRankingListItemList.get(position);


        // 아이템 내 각 위젯에 데이터 반영
        ranking_medal.setImageResource(arGameRankingListItem.getMedalimg());
        ranking_numbering.setText(String.valueOf(arGameRankingListItem.getRankingnum()));
//        ranking_updown.setImageResource(arGameRankingListItem.getUpdownimg());
//        ranking_diff.setText(String.valueOf(arGameRankingListItem.getUpdownnum()));
        ranking_name.setText(String.valueOf(arGameRankingListItem.getNickname()));
        ranking_score.setText(String.valueOf(arGameRankingListItem.getScore()));

        return convertView;
    }

    public void addItem(int ranking_medal, int ranking_numbering, String ranking_name, int ranking_score) {
        ARGameRankingListItem item = new ARGameRankingListItem();

        item.setMedalimg(ranking_medal);
        item.setRankingnum(ranking_numbering);
//        item.setUpdownimg(ranking_updown);
//        item.setUpdownnum(ranking_diff);
        item.setNickname(ranking_name);
        item.setScore(ranking_score);

        arGameRankingListItemList.add(item);
    }

}
