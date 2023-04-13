package com.test.weatherforcast.Rest

import com.test.weatherforcast.Models.WeatherCityList
import com.test.weatherforcast.Models.WeatherList
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @GET("weather")
    fun getWeather(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") appid: String): Call<WeatherList?>?

    @GET("weather")
    fun getCityWeather(@Query("q") city_name: String, @Query("appid") appid: String): Call<WeatherCityList?>?


}