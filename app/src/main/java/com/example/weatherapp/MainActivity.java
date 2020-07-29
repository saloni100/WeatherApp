package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Helper.Helper;
import com.example.weatherapp.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView tv_city, tv_humidity, tv_description, tv_lastupdate, tv_celcius, tv_time;
    ImageView image;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intitViews();

        //Get Co-ordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);


        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.e("TAG", "NO Location");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);


        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);

        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    private void intitViews() {
        tv_city = findViewById(R.id.tv_city);
        tv_celcius = findViewById(R.id.tv_celsius);
        tv_description = findViewById(R.id.tv_description);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_lastupdate = findViewById(R.id.tv_lastUpdate);
        tv_time = findViewById(R.id.tv_time);
        image = findViewById(R.id.imageview);
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class GetWeather extends AsyncTask<String,Void,String> {

        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please Wait !");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String stream = null;
            String urlString = params[0];

            Helper Http = new Helper();
            try {
                stream = Http.getHTTPData(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error not found city"))
            {
                pd.dismiss();
                return;
            }

            Gson gson = new Gson();
            Type mtype = new TypeToken<OpenWeatherMap>() {}.getType();
            openWeatherMap = gson.fromJson(s,mtype);
            pd.dismiss();

            tv_city.setText(String.format("%s , %s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            tv_lastupdate.setText(String.format("Last Updated : %s", Common.getDateNow()));
            tv_description.setText(String.format("%s",openWeatherMap.getWeatherList().get(0).getDescription()));
            tv_humidity.setText(String.format("%d %%",openWeatherMap.getMain().getHumidity()));
            tv_time.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            tv_celcius.setText(String.format("%.2f  °C",openWeatherMap.getMain().getTemp()));
           // Picasso.with(MainActivity.this).load(Common.getImage(openWeatherMap.getWeatherList().get(0).getIcon())).into(image);
        }
    }
}