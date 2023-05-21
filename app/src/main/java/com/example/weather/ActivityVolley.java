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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityVolley extends AppCompatActivity {
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        mQueue = Volley.newRequestQueue(this);
        sendAndRequestResponse();
    }

    private void sendAndRequestResponse() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=-7.98&longitude=112.63&daily=weathercode&current_weather=true&timezone=auto";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("current_weather");
                    double latitude = response.getDouble("latitude");
                    double longitude = response.getDouble("longitude");
                    String time = jsonObject.getString("time");
                    double temperature = jsonObject.getDouble("temperature");
                    int condition = jsonObject.getInt("is_day");
                    String cond =Integer.toString(condition);
                    double windspeed = jsonObject.getDouble("windspeed");

                    JSONObject jsonObject_daily = response.getJSONObject("daily");
                    JSONArray daily = jsonObject_daily.getJSONArray("time");
                    JSONArray weatherCode = jsonObject_daily.getJSONArray("weathercode");

                    //set coordinate
                    TextView coordinat = findViewById(R.id.coordinat);
                    coordinat.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));

                    // set date
                    String[] dateSplit = time.split("-", 3);
                    String day = dateSplit[2].substring(0,2);
                    String month = getMonthName(Integer.parseInt(dateSplit[1]));
                    String year = dateSplit[0];
                    TextView date = findViewById(R.id.date);
                    date.setText(day + " " + month + " " + year);

                    //set temperaure
                    TextView txttemperature = findViewById(R.id.temperature);
                    txttemperature.setText(String.valueOf(temperature) + "°");

                    //setImage
                    ImageView imgcondition = findViewById(R.id.imgCondition);
                    imgcondition.setImageDrawable(setImage(cond));

                    //set conditions this day
                    TextView txtCondition = findViewById(R.id.condition);
                    txtCondition.setText(DetailCondition(cond));

                    //set wind this day
                    TextView txtWindSpeed = findViewById(R.id.wind);
                    txtWindSpeed.setText(String.valueOf(windspeed) + "m/s");

                    ShowDataDaily(daily, weatherCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityVolley.this, "API call failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
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

    private void ShowDataDaily(JSONArray daily, JSONArray weathercode) throws JSONException {
        String[] items_daily =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String iterate = daily.getString(i);
            items_daily[i] = iterate;
        }
        String[] items_weathercode =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String iterate = weathercode.getString(i);
            items_weathercode[i] = DetailCondition(iterate);
        }

        Drawable[] items_images = new Drawable[7];
        for(int i=0; i < items_images.length ; i++) {
            String iterate = weathercode.getString(i);
            items_images[i] = setImage(iterate);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, items_daily);
        ListView listView1 = (ListView) findViewById(R.id.daily);
        listView1.setAdapter(adapter);

        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.activity_listview, items_weathercode);
        ListView listView2 = (ListView) findViewById(R.id.dailyweather);
        listView2.setAdapter(adapter2);

        //ArrayAdapter adapter3 = new ArrayAdapter<Drawable>(this, R.layout.img_list, items_images);
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

    public static String getMonthName(int number) {
        String[] monthNames = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };
        return monthNames[number - 1];
    }
}