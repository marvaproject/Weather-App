package com.example.weatherapp.network

import retrofit2.Call
import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    fun weatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String? = API_KEY
    ): Call<WeatherResponse>

    @GET("forecast")
    fun forecastByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String? = API_KEY
    ): Call<ForecastResponse>

    @GET("weather")
    fun weatherByCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String? = API_KEY
    ): Call<WeatherResponse>

    @GET("weather")
    fun forecastByCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String? = API_KEY
    ): Call<ForecastResponse>
}