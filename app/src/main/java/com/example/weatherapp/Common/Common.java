package com.example.weatherapp.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String API_KEY = "c7e46c9e0f783d4f74890469241c7d51";
    public static final String API_LINK = "https://api.openweathermap.org/data/2.5/weather";

    @org.jetbrains.annotations.NotNull
    public static  String apiRequest(String lat, String lng)
    {
        StringBuilder stringBuilder = new StringBuilder(API_LINK);
        //stringBuilder.append("?lat={lat}&lon={lon}&appid={your api key}",lat,lng,API_KEY);
        return stringBuilder.toString();
    }

    public static  String unixTimeStampToDateTime(double unixTimeStamp)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) unixTimeStamp * 1000);
        return dateFormat.format(date);

    }

    public static String getImage(String icon)
    {
        return String.format("https://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getDateNow()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);

    }

}
