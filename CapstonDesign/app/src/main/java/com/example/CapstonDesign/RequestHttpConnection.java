package com.example.CapstonDesign;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHttpConnection {
    public String request(String _url) {
        // HttpURLConnection 참조 변수
        HttpURLConnection urlConn = null;

        // HttpUrlConnection을 통해 web의 데이터를 가져온다.
        try {
            URL url = new URL(_url);

            Log.d("@",_url);

            urlConn = (HttpURLConnection) url.openConnection();

            // urlConn 설정
            urlConn.setRequestMethod("GET"); // URL 요청에 대한 메소드 방식을 GET 방식으로 설정
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset을 UTF-8로 설정
            urlConn.setDoOutput(false); // POST 값을 보낼 땐 true

            // 연결 요청 확인
            // 실패 시 null을 리턴하고 매서드를 종료
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("@", "연결 요청 실패");
                return null;
            }

            // 읽어온 결과물 리턴
            // 요청한 URL을 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            // 출력물의 라인과 그 합에 대한 변수
            String line;
            String page= "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }

            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}
