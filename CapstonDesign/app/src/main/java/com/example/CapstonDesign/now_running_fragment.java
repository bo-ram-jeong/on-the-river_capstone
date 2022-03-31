package com.example.CapstonDesign;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


public class now_running_fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView setTimer;
    int elapsed = 0;
    private boolean isPause=false,isClick=false;
    Timer timer;
    ImageButton runningPause_Btn;
    Button runningStop_Btn;
    int heightPixels;
    int widthPixels;
    ImageView runningman;
    TextView currentSpeed,currentSpeed2;
    Bundle bundle;
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    public now_running_fragment() {

    }

    public int getElapsed() {
        return elapsed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_running_fragment, container, false);
        setTimer = view.findViewById(R.id.setTimer);
        currentSpeed2 = view.findViewById(R.id.currentSpeed2);
        runningPause_Btn = view.findViewById(R.id.runningPause_Btn);
        runningStop_Btn = view.findViewById(R.id.runningStop_Btn);
        currentSpeed = view.findViewById(R.id.currentSpeed);
        runningman = view.findViewById(R.id.runningman);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        heightPixels = metrics.heightPixels;
        widthPixels = metrics.widthPixels;


        //파편화 작업
        runningPause_Btn.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = runningPause_Btn.getMeasuredWidth();
        int height =runningPause_Btn.getMeasuredHeight();

        runningStop_Btn.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width2 = runningStop_Btn.getMeasuredWidth();
        int height2 =runningStop_Btn.getMeasuredHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //파편화 작업
        float density = displayMetrics.density; // density에는 dip/160 값이 들어 있음.

            if((heightPixels*1.0)/(widthPixels*1.0) < 1.9){ //파편화
                double changeWidth = 1.96 - (heightPixels*1.0)/(widthPixels*1.0);
                double changeHeight = 1.96 - (heightPixels*1.0)/(widthPixels*1.0);


                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        (int) (width * (1-changeWidth)),
                        (int) (height * (1-changeHeight)));
                lp.setMargins(0, 20, 0, 30);


                runningPause_Btn.setLayoutParams(lp);

                runningStop_Btn.setLayoutParams(new LinearLayout.LayoutParams(
                        (int) (width2 * 2),
                        (int) (height2 * (1-changeHeight))));

                setTimer.setTextSize(40);
                runningman.getLayoutParams().width = (int) (35 * density + 0.5);
                runningman.getLayoutParams().height = (int) (35 * density + 0.5);
                Log.d("(heightPixels*1.0)/(widthPixels*1.0)", width+","+height);
            }



        timer = new Timer();
        startTimer();


        runningPause_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause){
                    isPause = false;//재생버튼 누름1
                    ((MapActivity)getActivity()).slideFullDownView(heightPixels,heightPixels/3,0,heightPixels/3*2);
                    timer = new Timer();
                    startTimer();
                }else {
                    isPause = true;//멈춤 버튼 누름
                    timer.cancel();
                    ((MapActivity)getActivity()).slideFullUpView(heightPixels/3,heightPixels,heightPixels/3*2,0);
                }
                btnChange();

            }
        });
        runningStop_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null)
                    timer.cancel();
                ((MapActivity)getActivity()).goToFinish(elapsed);
                elapsed = 0;

                runningPause_Btn.setEnabled(false);

                runningPause_Btn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.radius_circle_black) );
                runningPause_Btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                runningStop_Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.radius_black));

                String time = String.format(Locale.getDefault(),"%d:%02d:%02d",0,0,0);
                setTimer.setText(time);
            }
        });

        return view;
    }

    private  void btnChange(){
        if (isPause){
            runningPause_Btn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.radius_circle_black) );
            runningPause_Btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            runningStop_Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.radius_black));
        }else {
            runningPause_Btn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.radius_circle) );
            runningPause_Btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
            runningStop_Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.radius));
        }
    }
    public void startTimer(){
        //타이머 구현
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if(!isPause){
                                elapsed = elapsed + 1;
                            }

                            int hours = (elapsed / 3600);
                            int minutes = (elapsed % 3600) / 60;
                            int secs = elapsed % 60;

                            String time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes, secs);

                            setTimer.setText(time);
                        }
                    });
                }

            }
        },1500, 1000);
    }
    public void endTimer(){
        //타이머 종료
        if (timer != null)
            timer.cancel();
    }
    public void stopTimer(Boolean isStop){
        //타이머 멈추고 재생
        if (isStop)
           isPause = true;
        else
            isPause = false;
    }
    @SuppressLint("DefaultLocale")
    public void showCurrentSpeed(double speed){
        Log.d("locationChange",""+speed);
        if (speed == 0.0){
            currentSpeed.setText("0.0");
        }else
            currentSpeed.setText(String.format("%.2f",speed));
    }

}