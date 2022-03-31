package com.example.CapstonDesign;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyActivity extends AppCompatActivity {

    private MaterialCalendarView mCalendar;
    private FrameLayout sheet;
    private ListView runningFinishedList;

    private TextView d_total;
    private TextView d_runningMode;
    private TextView d_arMode;
    private TextView t_total;
    private TextView t_runningMode;
    private TextView t_arMode;
    private TextView tv_month_activity;
    private Button open_bottom_sheet;

    private String[] spotName = new String[6];

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private BottomSheetAdapter bsAdapter = new BottomSheetAdapter(MyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivity);
        getSupportActionBar().hide();

        mCalendar = findViewById(R.id.mCalendar);
        mCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

        d_total = findViewById(R.id.d_total);
        d_runningMode = findViewById(R.id.d_runningMode);
        d_arMode = findViewById(R.id.d_arMode);
        t_total = findViewById(R.id.t_total);
        t_runningMode = findViewById(R.id.t_runningMode);
        t_arMode = findViewById(R.id.t_arMode);
        tv_month_activity = findViewById(R.id.tv_month_activity);
        open_bottom_sheet = findViewById(R.id.open_bottom_sheet);

        ImageView btn_back_white = findViewById(R.id.btn_back_white);
        btn_back_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //로그인한 회원아이디 가져오기
        String autoId = UserPreferenceData.getString(MyActivity.this, "autoID");

        //onCreate에서 세팅해야 할것
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
        int m = calendar.get(Calendar.MONTH) + 1;
        tv_month_activity.setText(m + "월의 활동");
        //운동한기록 계산
        calculateRecord(String.valueOf(m), autoId);
        //BottomSheet에 record 넣기
        addRecord(String.valueOf(m), autoId);


        //운동한 날 표시
        CollectionReference productRef = db
                .collection("users").document(autoId)
                .collection("Records");
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Date getDate = document.getDate("date");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(getDate);

                        ArrayList<CalendarDay> calendarDayList = new ArrayList<>();
                        calendarDayList.add(CalendarDay.from(cal));

                        mCalendar.addDecorator(new CalendarDecorator(Color.parseColor("#FF3498DB"), calendarDayList));
                        //https://gwynn.tistory.com/69
                    }
                } else {

                }
            }
        });


//        sheet = findViewById(R.id.sheet);

//        runningFinishedList = findViewById(R.id.runningFinishedList);


//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(sheet);

        Button OpenBottomSheet = findViewById(R.id.open_bottom_sheet);

        OpenBottomSheet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheet = new BottomSheetDialog(MyActivity.this, bsAdapter);
                        bottomSheet.show(getSupportFragmentManager(),
                                "ModalBottomSheet");
                    }
                });


        //달이 changed 됐을 때,
        mCalendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String m = String.valueOf(date.getMonth() + 1);
                tv_month_activity.setText(m + "월의 활동");

                //운동한기록 계산
                calculateRecord(m, autoId);
                //BottomSheet에 record 넣기
                addRecord(m, autoId);

            }
        });
//        bottomSheetBehavior.setPeekHeight(100);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH, -2);
//        ArrayList<CalendarDay> dates = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            CalendarDay day = CalendarDay.from(calendar);
//            dates.add(day);
//            calendar.add(Calendar.DATE, 1);
//            mCalendar.setDateSelected(calendar, true);
//        }

//        mCalendar.setOnRangeSelectedListener(new OnRangeSelectedListener() {    //범위 설정 시
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onRangeSelected(MaterialCalendarView widget, List<CalendarDay> dates) {
//                String s[] = new String[0];
//                for (int i = 0; i < dates.size(); i++){
//                    //s[i] = dates.get(i).toString();
//                    Toast.makeText(MyCalendar.this, dates.get(i).toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        mCalendar.setOnDateChangedListener(new OnDateSelectedListener() {    //날짜 선택 시
//            @Override
//            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
//                Toast.makeText(MyCalendar.this, date.toString(), Toast.LENGTH_LONG).show();
//            }
//        });

    }

    //운동기록 바텀시트 리스트뷰에 add
    private void addRecord(String month, String autoId) {
        CollectionReference productRef2 = db
                .collection("users").document(autoId)
                .collection("Records");
        productRef2.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    bsAdapter = new BottomSheetAdapter(MyActivity.this);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> map = document.getData();
                        Date getDate = document.getDate("date");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd aa hh:mm:ss", Locale.getDefault());
                        String date = dateFormat.format(getDate);
                        SimpleDateFormat monthFormat = new SimpleDateFormat("M", Locale.getDefault());
                        String mDate = monthFormat.format(getDate);

                        String route = String.valueOf(map.get("route"));
//                        String route_aft = route.replace(",", " -> ");
                        Arrays.fill(spotName, "N");
                        String[] spotName_tmp = route.split(",");
//                        spotName = route.split(",");
                        for (int i = 0; i < spotName_tmp.length; i++) {
                            spotName[i] = spotName_tmp[i];
                        }

                        int[] spotImages = new int[6];
                        Arrays.fill(spotImages, R.drawable.empty_img);

                        for (int i = 0; i < spotName.length; i++) {
                            switch (spotName[i]) {
                                case "A":
                                    spotImages[i] = R.mipmap.ic_aspot_1x;
                                    break;
                                case "B":
                                    spotImages[i] = R.mipmap.ic_bspot_1x;
                                    break;
                                case "C":
                                    spotImages[i] = R.mipmap.ic_cspot_1x;
                                    break;
                                case "D":
                                    spotImages[i] = R.mipmap.ic_dspot_1x;
                                    break;
                                case "E":
                                    spotImages[i] = R.mipmap.ic_espot_1x;
                                    break;
                                case "F":
                                    spotImages[i] = R.mipmap.ic_fspot_1x;
                                    break;
                            }

                        }
                        DecimalFormat form = new DecimalFormat("#.##");
                        double distance_double=Double.parseDouble(String.valueOf(map.get("distance")));
                        String runningTime = calculateToTimeString(Integer.parseInt(String.valueOf(map.get("runningTime"))));
                        if (month.equals(mDate)) {    //해당 달 리스트만
                            bsAdapter.addItem(spotImages[0], spotImages[1], spotImages[2], spotImages[3], spotImages[4], spotImages[5], String.valueOf(map.get("mode")), runningTime, String.valueOf(map.get("kcal")), form.format(distance_double), String.valueOf(date));
                        }

                    }
//                    runningFinishedList.setAdapter(bsAdapter);
                } else {
                    Log.d(TAG, "Error", task.getException());
                }
            }
        });
    }

    //운동기록계산
    private void calculateRecord(String month, String autoId) {
        CollectionReference productRef3 = db
                .collection("users").document(autoId)
                .collection("Records");
        productRef3.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    String distance_rMode, distance_aMode, runningTime_rMode, runningTime_aMode;
                    int num_t_rMode, num_t_aMode, total_t_rMode, total_t_aMode;
                    double num_d_rMode, num_d_aMode, total_d_rMode, total_d_aMode;
                    total_d_rMode = 0;
                    total_t_rMode = 0;
                    total_d_aMode = 0;
                    total_t_aMode = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> map = document.getData();

                        Date getDate = document.getDate("date");
                        SimpleDateFormat monthFormat = new SimpleDateFormat("M", Locale.getDefault());
                        String mDate = monthFormat.format(getDate);

                        if (month.equals(mDate)) {

                            String mode = String.valueOf(map.get("mode"));
                            if (mode.equals("러닝 모드")) {
                                distance_rMode = String.valueOf(map.get("distance"));
                                runningTime_rMode = String.valueOf(map.get("runningTime"));
                                num_d_rMode = Double.parseDouble(distance_rMode);
                                num_t_rMode = Integer.parseInt(runningTime_rMode);
                                total_d_rMode += num_d_rMode;
                                total_t_rMode += num_t_rMode;

                            } else if (mode.equals("AR 모드")) {
                                distance_aMode = String.valueOf(map.get("distance"));
                                runningTime_aMode = String.valueOf(map.get("runningTime"));
                                num_d_aMode =Double.parseDouble(distance_aMode);
                                num_t_aMode = Integer.parseInt(runningTime_aMode);
                                total_d_aMode += num_d_aMode;
                                total_t_aMode += num_t_aMode;

                            }
                        }

                    }
                    DecimalFormat form = new DecimalFormat("#.##");

                    d_runningMode.setText(form.format(total_d_rMode) + "km");
                    t_runningMode.setText(calculateToTimeString(total_t_rMode));
                    d_arMode.setText(form.format(total_d_aMode) + "km");
                    t_arMode.setText(calculateToTimeString(total_t_aMode));
                    d_total.setText(form.format(total_d_rMode + total_d_aMode) + "km");
                    t_total.setText(calculateToTimeString(total_t_rMode + total_t_aMode));

                    if (total_d_rMode == 0 && total_d_aMode == 0) {
                        open_bottom_sheet.setVisibility(View.INVISIBLE);
                    } else {
                        open_bottom_sheet.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.d(TAG, "Error", task.getException());
                }
            }
        });
    }

    //초시간 정해진포맷으로 형식맞추기
    private String calculateToTimeString(int sec) {

        if (sec <= 0) {
            return "00:00:00";
        }

        // todo 계산
        int hours, minutes, seconds;
        hours = sec / 3600;
        minutes = (sec % 3600) / 60;
        seconds = (sec % 3600) % 60;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02d", hours)).append(":").append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));

        return sb.toString();
    }
}

//달력 : https://dduddublog.tistory.com/70
// https://github.com/prolificinteractive/material-calendarview
