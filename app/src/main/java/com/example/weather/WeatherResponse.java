package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("latitude")
    private double latitude;
    public double getLatitude() {
        return latitude;
    }
    @SerializedName("longitude")
    private double longitude;
    public double getLongitude() {
        return longitude;
    }

    @SerializedName("current_weather")
    private CurrentWeather currentWeather;
    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }
    public class CurrentWeather {
        @SerializedName("time")
        private String time;
        public String getTime() {
            return time;
        }
        @SerializedName("temperature")
        private float temperature;
        public float getTemperature() {
            return temperature;
        }
        @SerializedName("is_day")
        private int is_day;
        public int getIs_day() {
            return is_day;
        }
        @SerializedName("windspeed")
        private float windspeed;
        public float getWindspeed() {
            return windspeed;
        }
    }

    @SerializedName("daily")
    private DailyData dailyData;

    public DailyData getDailyData() {
        return dailyData;
    }
    public class DailyData {
        @SerializedName("weathercode")
        private int[] weatherCode;

        public int[] getWeatherCode() {
            return weatherCode;
        }
        @SerializedName("time")
        private String[] time;

        public String[] getTime() {
            return time;
        }
    }
}
