package com.example.acer.weatherapplication;

/**Author:Manasa
 * Date:05/11/2018
 **/


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Jsondata_parsing {

    // API key and link
    public static String API_KEY = "4bd7e5c29a1bb79ccb9269c514aa1c85";
    public static String API_LINK = "https://api.openweathermap.org/data/2.5/weather";

    public static String stream = null;

    public String apiRequest(String cityName ) {
        // builds api link with the given city name
        StringBuilder apiReqStr = new StringBuilder(API_LINK);
        apiReqStr.append(String.format("?q=%s&appid=%s",cityName,API_KEY));
        return apiReqStr.toString();
    }

    public String getJsonData(String urlStr){
        // establishing http connection
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            if (httpConnection.getResponseCode() == 200) {
// if response is successful,build JSON string
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = bufferReader.readLine()) != null) {
                    str.append(line);
                }
                stream = str.toString();
                httpConnection.disconnect();
            }
            else if ((httpConnection.getResponseCode() == 404) || (httpConnection.getResponseCode() == 400)){
                // if invalid city name is entered by user or if the city name is not accessible to API
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = bufferReader.readLine()) != null) {
                    str.append(line);
                }
                stream = str.toString();
                httpConnection.disconnect();
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return stream;
    }
}
