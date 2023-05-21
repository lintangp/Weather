package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ActivityRetrofit extends AppCompatActivity {
    interface RequestData {
        @GET("v1/forecast")
        Call<WeatherResponse> getWeatherData(
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("daily") String daily,
                @Query("current_weather") boolean currentWeather,
                @Query("timezone") String timezone
        );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        TextView txtcoordinat = findViewById(R.id.coordinat);
        TextView txtDate = findViewById(R.id.date);
        TextView txtTemperature = findViewById(R.id.temperature);
        TextView txtCondition = findViewById(R.id.condition);
        TextView txtWind = findViewById(R.id.wind);
        ImageView imgCondition = findViewById(R.id.imgCondition);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestData requestUser = retrofit.create(RequestData.class);

        requestUser.getWeatherData(-7.98, 112.63, "weathercode", true, "auto").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                WeatherResponse weatherData = response.body();
                txtcoordinat.setText(String.valueOf(weatherData.getLatitude() + ", " + weatherData.getLongitude()));

                WeatherResponse.CurrentWeather currentWeather = weatherData.getCurrentWeather();

                String date = currentWeather.getTime();
                txtDate.setText(String.valueOf(date));
                String[] dateSplit = date.split("-", 3);
                String day = dateSplit[2].substring(0,2);
                String month = getMonthName(Integer.parseInt(dateSplit[1]));
                String year = dateSplit[0];
                txtDate.setText(day + " " + month + " " + year);

                Float temperature = currentWeather.getTemperature();
                txtTemperature.setText(String.valueOf(temperature + "°"));

                int is_day = currentWeather.getIs_day();
                String condition = Integer.toString(is_day);
                txtCondition.setText(DetailCondition(condition));

                imgCondition.setImageDrawable(setImage(condition));

                float wind = currentWeather.getWindspeed();
                txtWind.setText(String.valueOf(wind + "m/s"));

                WeatherResponse.DailyData dailyData = weatherData.getDailyData();
                String[] timeArray = dailyData.getTime();

                int[] weatherCodes = dailyData.getWeatherCode();
                ShowDataDaily(timeArray, weatherCodes);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(ActivityRetrofit.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowDataDaily(String[] timeArray, int[] weatherCodes) {
        String[] items_daily =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String iterate = timeArray[i];
            items_daily[i] = iterate;
        }
        String[] items_weathercode =  new String[7];
        for(int i=0; i < items_weathercode.length ; i++) {
            int iterate = weatherCodes[i];
            String condition = Integer.toString(iterate);
            items_weathercode[i] = DetailCondition(condition);
        }

        Drawable[] items_images = new Drawable[7];
        for(int i=0; i < items_images.length ; i++) {
            String iterate = String.valueOf(weatherCodes[i]);
            items_images[i] = setImage(iterate);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, items_daily);
        ListView listView1 = (ListView) findViewById(R.id.daily);
        listView1.setAdapter(adapter);

        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.activity_listview, items_weathercode);
        ListView listView2 = (ListView) findViewById(R.id.dailyweather);
        listView2.setAdapter(adapter2);

        ListView listView3 = (ListView) findViewById(R.id.imgWeather);
        ArrayAdapter<Drawable> adapter3 = new ArrayAdapter<Drawable>(this, R.layout.img_list, items_images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.img_list, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.imgWeather);
                imageView.setImageDrawable(getItem(position));
                return convertView;
            }
        };
        listView3.setAdapter(adapter3);
    }

    private Drawable setImage(String cond) {
        Drawable drawable;
        switch (cond){
            case "0":
                drawable = getResources().getDrawable(R.drawable._icon__day_sunny_);
                break;
            case "1":
                drawable =  getResources().getDrawable(R.drawable._icon__day_cloudy_);
                break;
            case "2":
                drawable =  getResources().getDrawable(R.drawable.wi_forecast_io_partly_cloudy_night);
                break;
            case "3":
                drawable =  getResources().getDrawable(R.drawable._icon__cloudy_);
                break;
            case "45":
            case "48":
                drawable =  getResources().getDrawable(R.drawable._icon__fog_);
                break;
            case "51":
            case "53":
            case "55":
                drawable =  getResources().getDrawable(R.drawable._icon__sleet_);
                break;
            case "61":
            case "63":
                drawable =  getResources().getDrawable(R.drawable.wi_forecast_io_hail);
                break;
            case "65":
                drawable =  getResources().getDrawable(R.drawable._icon__rain_);
                break;
            default:
                drawable = getResources().getDrawable(R.drawable._icon__storm_showers_);
                break;
        }
        return drawable;
    }
    private String DetailCondition(String cond) {
        switch (cond){
            case "0":
                cond = "Clear Sky";
                break;
            case "1":
                cond = "Mainly Clear";
                break;
            case "2":
                cond = "Partly Cloudy";
                break;
            case "3":
                cond = "Overcast";
                break;
            case "45":
            case "48":
                cond = "Fog";
                break;
            case "51":
            case "53":
            case "55":
                cond = "Drizzle";
                break;
            case "61":
            case "63":
                cond = "Rain Slight";
                break;
            case "65":
                cond = "Rain Heavy";
                break;
            default:
                cond = "Thunderstorm";
                break;
        }
        return cond;
    }
    public static String getMonthName(int number) {
        String[] monthNames = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };
        return monthNames[number - 1];
    }

}