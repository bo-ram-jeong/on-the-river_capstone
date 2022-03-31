package com.example.CapstonDesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BottomSheetAdapter extends BaseAdapter {
    Context context;

    private ArrayList<BottomSheetList_Item> arraylist = new ArrayList<BottomSheetList_Item>();

    public BottomSheetAdapter(Context _context) {
        context = _context;
    }   //생성자

    @Override
    public int getCount() {
        return arraylist.size();  // 항목의 개수(=데이터의 개수)를 정수형으로 리턴
    }

    @Override
    public Object getItem(int i) {
        return arraylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i; //객체데이터 다룰때 사용
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {  //getView()에서 항목을 구성, 이벤트를 처리
        final Context context = viewGroup.getContext();

        // "bottomsheetlist.xml"Layout을 통으로 inflate하여 convertView 참조 획득.
        // bottomsheetlist.xml -> ListViewItem의 레이아웃
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bottomsheetlist_layout, viewGroup, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
//        TextView startpoint = view.findViewById(R.id.startpoint) ;
        ImageView spotImg1 = view.findViewById(R.id.spotImg1);
        ImageView spotImg2 = view.findViewById(R.id.spotImg2);
        ImageView spotImg3 = view.findViewById(R.id.spotImg3);
        ImageView spotImg4 = view.findViewById(R.id.spotImg4);
        ImageView spotImg5 = view.findViewById(R.id.spotImg5);
        ImageView spotImg6 = view.findViewById(R.id.spotImg6);
        TextView mode = view.findViewById(R.id.mode);
        TextView takedtime = view.findViewById(R.id.takedtime);
        TextView kcal = view.findViewById(R.id.kcal);
        TextView distance = view.findViewById(R.id.distance);
        TextView rundate = view.findViewById(R.id.rundate);


        // Data Set(BottomSheetList_Item)에서 i(position)에 위치한 데이터 참조 획득
        BottomSheetList_Item DailyRunningItem = arraylist.get(i);

        // 아이템 내 각 위젯(ImageView, TextView)에 데이터 반영
//        startpoint.setText(DailyRunningItem.getStartpoint());
        spotImg1.setImageResource(DailyRunningItem.getSpotImg1());
        spotImg2.setImageResource(DailyRunningItem.getSpotImg2());
        spotImg3.setImageResource(DailyRunningItem.getSpotImg3());
        spotImg4.setImageResource(DailyRunningItem.getSpotImg4());
        spotImg5.setImageResource(DailyRunningItem.getSpotImg5());
        spotImg6.setImageResource(DailyRunningItem.getSpotImg6());
        mode.setText(DailyRunningItem.getMode());
        takedtime.setText(DailyRunningItem.getTakedTime());
        kcal.setText(DailyRunningItem.getKcal() + "kcal");
        distance.setText(DailyRunningItem.getDistance() + "km");
        rundate.setText(DailyRunningItem.getRundate());

        return view;
    }

    //BottomSheetList_Item data 추가용 메소드(외부에서 추가 시 사용)
    public void addItem(int iv1, int iv2, int iv3, int iv4, int iv5, int iv6, String m, String t, String k, String di, String d) {
        BottomSheetList_Item item = new BottomSheetList_Item();

//        item.setStartpoint(s);
        item.setSpotImg1(iv1);
        item.setSpotImg2(iv2);
        item.setSpotImg3(iv3);
        item.setSpotImg4(iv4);
        item.setSpotImg5(iv5);
        item.setSpotImg6(iv6);
        item.setMode(m);
        item.setTakedTime(t);
        item.setKcal(k);
        item.setDistance(di);
        item.setRundate(d);

        arraylist.add(item);
    }
}

