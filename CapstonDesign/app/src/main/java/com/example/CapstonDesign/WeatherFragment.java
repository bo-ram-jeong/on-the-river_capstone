package com.example.CapstonDesign;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {
    private LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private TransLocalPoint transLocalPoint;
    private String Weather,T1H, REH, WSD, POP; // 날씨, 기온, 습도, 풍속, 강수
    private TextView tv1,tv2,tv3,tv4,tv5;
    private ImageView imgWeather;
    String fcstTime = "";
    String usnUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather,container,false);

        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);
        tv5 = view.findViewById(R.id.weather);
        imgWeather = view.findViewById(R.id.imgWeather);

        double latitude = DEFAULT_LOCATION.latitude;
        double longitude = DEFAULT_LOCATION.longitude;

        transLocalPoint = new TransLocalPoint();
        TransLocalPoint.LatXLngY tmp = transLocalPoint.convertGRID_GPS(TransLocalPoint.TO_GRID,latitude,longitude);

        Date mDate = new Date(System.currentTimeMillis());
        Date mTime = new Date(System.currentTimeMillis()-(1000*60*30));


        String formatYDM = new SimpleDateFormat("yyyyMMdd").format(mDate);
        String formatTime = new SimpleDateFormat("HH00").format(mTime);
        int formatTimeInt = Integer.parseInt(formatTime);

        String service_key = "5NdTMBhtPN2K0PRysHWlBCwXfjOWHj7Ylk0%2FQZxMFKpm43Vqr5OkM2YCt4Kfn%2FzyVJx1T%2FE33JgkRRjXEDijSA%3D%3D";
        String service_key2 = "KHbtz%2Bz9p7g3gbXZpqnaEtEVleJqXQZlxef0XTi0NyYbYLkSLiNMeiNHwVSnDPNpxPek1rBJuwKVZ61ithw39w%3D%3D";
        String base_date = formatYDM;
        String base_time = formatTime;
        fcstTime = formatTime;
        String nx = String.format("%.0f",tmp.x);
        String ny = String.format("%.0f",tmp.y);


        // 초단기예보조회 url
        String fcstUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?" +
                "serviceKey=" + service_key2 +
                "&pageNo=" + "1" +
                "&numOfRows=" + "60" +
                "&dataType=" + "JSON" +
                "&base_date=" + base_date +
                "&base_time=" + base_time +
                "&nx=" + nx +
                "&ny=" + ny;

        // 초단기실황조회 url
        String ncstUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?" +
                "serviceKey=" + service_key +
                "&pageNo=" + "1" +
                "&numOfRows=" + "10" +
                "&dataType=" + "JSON" +
                "&base_date=" + base_date +
                "&base_time=" + base_time +
                "&nx=" + nx +
                "&ny=" + ny;

        //동네예보 발표시간 0200, 0800, 1400, 2000
        String presentedTime = "";

       if(formatTimeInt <= Integer.parseInt("0200")){      // 02시 이전
            presentedTime = "2000";
        }else if(formatTimeInt <= Integer.parseInt("0800")){ //02시 ~ 08시
            presentedTime = "0200";
        }else if (formatTimeInt <= 1400){                        // 08시 ~ 14시
            presentedTime = "0800";
        }else if(formatTimeInt <= 2000){                         // 14시 ~ 20시
            presentedTime = "1400";
        }else{                                                  // 20시~24시
            presentedTime = "2000";
        }
        Log.d("presentedTime : ",""+presentedTime);



        // 동네예보조회 url
         usnUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
                "serviceKey=" + service_key2 +
                "&pageNo=" + "1" +
                "&numOfRows=" + "70" +
                "&dataType=" + "JSON" +
                "&base_date=" + base_date +
                "&base_time=" + presentedTime +
                "&nx=" + nx +
                "&ny=" + ny;
        Log.d("baseTime = ", "" + base_time);
        Log.d("currentTimeMillis", String.valueOf(mTime));

       // AsyncTask를 통해 HttpUrlConnection 수행
        NetworkTask fcstUrlTask = new NetworkTask(fcstUrl,"fcst");
        fcstUrlTask.execute();

        NetworkTask2 fcstUrlTask2 = new NetworkTask2(usnUrl,"fcst");
        fcstUrlTask2.execute();

        // NetworkTask ncstUrlTask = new NetworkTask(ncstUrl,"obsr");
        // ncstUrlTask.execute();
        return view;

    }

    void getPOPAgain(){
        NetworkTask2 fcstUrlTask2 = new NetworkTask2(usnUrl,"fcst");
        fcstUrlTask2.execute();
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {
        String url;
        String searchType;

        public NetworkTask(String url, String searchType) {
            this.url = url;
            this.searchType = searchType;
            Log.d("kkh", "" + this.url);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수
            RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
            result = requestHttpConnection.request(url);

            return result;
        }

        @Override
        // doInBackground()의 리턴값이 onPostRxecute()의 매개변수로 넘어와서 호출
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject json = new JSONObject(s);
                JSONObject parseResponse = new JSONObject(json.getString("response"));
                JSONObject parseBody = new JSONObject(parseResponse.getString("body"));
                JSONObject parseItems = new JSONObject(parseBody.getString("items"));

                JSONArray jsonArray = (JSONArray) parseItems.get("item");

                String prevCategory = "";
                int ptyInt = 0;

                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    if (item.getString("category").equals(prevCategory) != true) {
                        prevCategory = item.getString("category");
                        switch (prevCategory) {

                            case "T1H": // 기온
                                T1H = item.getString(searchType + "Value") + "℃";
                                break;

                            case "REH": // 습도
                                REH = item.getString(searchType + "Value") + "%";
                                break;

                            case "WSD": // 풍속
                                WSD = item.getString(searchType + "Value") + "m/s";
                                break;

                            case "SKY": // 하늘상태
                                Weather = item.getString(searchType + "Value");
                                int w = Integer.parseInt(Weather);

                                if (w > 5){
                                    Weather = "흐림";
                                    imgWeather.setImageResource(R.drawable.ic_weather_cloudy_1x);
                                }else{
                                    Weather = "맑음";
                                    imgWeather.setImageResource(R.drawable.ic_weather_sun_1x);
                                }
                                break;

                            case "PTY": // 강수형태
                                Weather = item.getString(searchType + "Value");
                                if (Weather.equals("0"))
                                    Weather = "없음";
                                else if (Weather.equals("1") || Weather.equals("5")) {
                                    Weather = "비";
                                    imgWeather.setImageResource(R.drawable.ic_weather_rainy_1x);
                                }else if (Weather.equals("3")|| Weather.equals("7")) {
                                    Weather = "눈";
                                    imgWeather.setImageResource(R.drawable.ic_weather_snow_1x);
                                }else if (Weather.equals("4")) {
                                    Weather = "소나기";
                                    imgWeather.setImageResource(R.drawable.ic_weather_rainy_1x);
                                }
                                break;
                        }
                    }
                }
                tv1.setText( T1H);
                tv2.setText( REH);
                tv3.setText( WSD);
                tv5.setText(Weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class NetworkTask2 extends AsyncTask<Void, Void, String> {
        String url;
        String searchType;

        public NetworkTask2(String url, String searchType) {
            this.url = url;
            this.searchType = searchType;
            Log.d("dsw", "" + this.url);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수
            RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
            result = requestHttpConnection.request(url);

            return result;
        }

        @Override
        // doInBackground()의 리턴값이 onPostRxecute()의 매개변수로 넘어와서 호출
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject json = new JSONObject(s);
                JSONObject parseResponse = new JSONObject(json.getString("response"));
                JSONObject parseBody = new JSONObject(parseResponse.getString("body"));
                JSONObject parseItems = new JSONObject(parseBody.getString("items"));

                JSONArray jsonArray = (JSONArray) parseItems.get("item");

                String prevCategory = "";
                int ptyInt = 0;

                Log.d("dsw", "" + jsonArray);

                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    if (item.getString("fcstTime").equals(fcstTime)) {

                        if (item.getString("category").equals(prevCategory) != true) {

                            prevCategory = item.getString("category");
                            switch (prevCategory) {

                                case "POP": // 강수확률
                                    POP = item.getString(searchType + "Value") + "%";
                                    if (POP.equals("0%")) {
                                        POP = "10%";
                                    }else if(POP == null){
                                        POP = "10%";
                                    }

                                    break;
                                default:
                                break;

                            }
                        }
                    }
                }
                Log.d("popTime : ",""+POP);
                if(POP == null){
                    getPOPAgain();
                   // POP = "10%";
                    Log.d("popTime : ","POP failed");
                }
                tv4.setText(POP);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}