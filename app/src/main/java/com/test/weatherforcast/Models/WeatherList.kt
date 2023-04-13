package com.test.weatherforcast.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class WeatherList: Serializable {

    @Expose
    @SerializedName("cod")
    private var cod = 0

    @Expose
    @SerializedName("name")
    private var name: String? = null

    @Expose
    @SerializedName("id")
    private var id = 0

    @Expose
    @SerializedName("timezone")
    private var timezone = 0

    @Expose
    @SerializedName("sys")
    private var sys: SysEntity? = null

    @Expose
    @SerializedName("dt")
    private var dt = 0

    @Expose
    @SerializedName("clouds")
    private var clouds: CloudsEntity? = null

    @Expose
    @SerializedName("wind")
    private var wind: WindEntity? = null

    @Expose
    @SerializedName("visibility")
    private var visibility = 0

    @Expose
    @SerializedName("main")
    private var main: MainEntity? = null

    @Expose
    @SerializedName("base")
    private var base: String? = null

    @Expose
    @SerializedName("weather")
    private var weather: List<WeatherEntity?>? = null

    @Expose
    @SerializedName("coord")
    private var coord: CoordEntity? = null

    fun getCod(): Int {
        return cod
    }

    fun setCod(cod: Int) {
        this.cod = cod
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTimezone(): Int {
        return timezone
    }

    fun setTimezone(timezone: Int) {
        this.timezone = timezone
    }

    fun getSys(): SysEntity? {
        return sys
    }

    fun setSys(sys: SysEntity?) {
        this.sys = sys
    }

    fun getDt(): Int {
        return dt
    }

    fun setDt(dt: Int) {
        this.dt = dt
    }

    fun getClouds(): CloudsEntity? {
        return clouds
    }

    fun setClouds(clouds: CloudsEntity?) {
        this.clouds = clouds
    }

    fun getWind(): WindEntity? {
        return wind
    }

    fun setWind(wind: WindEntity?) {
        this.wind = wind
    }

    fun getVisibility(): Int {
        return visibility
    }

    fun setVisibility(visibility: Int) {
        this.visibility = visibility
    }

    fun getMain(): MainEntity? {
        return main
    }

    fun setMain(main: MainEntity?) {
        this.main = main
    }

    fun getBase(): String? {
        return base
    }

    fun setBase(base: String?) {
        this.base = base
    }

    fun getWeather(): List<WeatherEntity?>? {
        return weather
    }

    fun setWeather(weather: List<WeatherEntity?>?) {
        this.weather = weather
    }

    fun getCoord(): CoordEntity? {
        return coord
    }

    fun setCoord(coord: CoordEntity?) {
        this.coord = coord
    }

    class SysEntity: Serializable {
        @Expose
        @SerializedName("sunset")
        var sunset = 0

        @Expose
        @SerializedName("sunrise")
        var sunrise = 0

        @Expose
        @SerializedName("country")
        var country: String? = null

        @Expose
        @SerializedName("id")
        var id = 0

        @Expose
        @SerializedName("type")
        var type = 0
    }

    class CloudsEntity: Serializable {
        @Expose
        @SerializedName("all")
        var all = 0
    }

    class WindEntity: Serializable {
        @Expose
        @SerializedName("gust")
        var gust = 0.0

        @Expose
        @SerializedName("deg")
        var deg = 0

        @Expose
        @SerializedName("speed")
        var speed = 0.0
    }

    class MainEntity: Serializable {
        @Expose
        @SerializedName("grnd_level")
        var grnd_level = 0

        @Expose
        @SerializedName("sea_level")
        var sea_level = 0

        @Expose
        @SerializedName("humidity")
        var humidity = 0

        @Expose
        @SerializedName("pressure")
        var pressure = 0

        @Expose
        @SerializedName("temp_max")
        var temp_max = 0.0

        @Expose
        @SerializedName("temp_min")
        var temp_min = 0.0

        @Expose
        @SerializedName("feels_like")
        var feels_like = 0.0

        @Expose
        @SerializedName("temp")
        var temp = 0.0
    }

    class WeatherEntity: Serializable {
        @Expose
        @SerializedName("icon")
        var icon: String? = null

        @Expose
        @SerializedName("description")
        var description: String? = null

        @Expose
        @SerializedName("main")
        var main: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class CoordEntity: Serializable {
        @Expose
        @SerializedName("lat")
        var lat = 0.0

        @Expose
        @SerializedName("lon")
        var lon = 0.0
    }

}