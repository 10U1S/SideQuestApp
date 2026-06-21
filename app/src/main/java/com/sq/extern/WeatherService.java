package com.sq.extern;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {
    private static final String API_KEY = "55cba7f876b7d36217b306d79b4864c5";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public interface WeatherCallback {
        void onSuccess(double temperature, String description, boolean isRaining);
        void onError(Exception e);
    }

    public static void getWeather(double lat, double lon, WeatherCallback callback) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric&lang=de";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    WeatherResponse data = gson.fromJson(jsonData, WeatherResponse.class);
                    boolean isRaining = data.weather[0].main.equalsIgnoreCase("Rain") || 
                                       data.weather[0].main.equalsIgnoreCase("Drizzle");
                    
                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onSuccess(data.main.temp, data.weather[0].description, isRaining)
                    );
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Exception("Response not successful")));
                }
            }
        });
    }

    // Helper classes for GSON
    private static class WeatherResponse {
        Main main;
        Weather[] weather;
    }

    private static class Main {
        double temp;
    }

    private static class Weather {
        String main;
        String description;
    }
}
