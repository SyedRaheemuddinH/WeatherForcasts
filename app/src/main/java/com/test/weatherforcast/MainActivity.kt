package com.test.weatherforcast

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.cs.drcafe.Widgets.NetworkUtil
import com.test.weatherforcast.Models.WeatherCityList
import com.test.weatherforcast.Models.WeatherList
import com.test.weatherforcast.Rest.APIInterface
import com.test.weatherforcast.Rest.ApiClient
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var ed_search: EditText

    lateinit var img_search: ImageView
    lateinit var img_weather: ImageView

    lateinit var temperature: TextView
    lateinit var txt_temperature: TextView
    lateinit var txt_location: TextView
    lateinit var txt_humidity: TextView
    lateinit var txt_chance_rain: TextView
    lateinit var txt_pressure: TextView
    lateinit var txt_wind_speed: TextView

    lateinit var gps: GPSTracker

    var lat = 0.0
    var long = 0.0

    var city_name = ""

    companion object {
        private val LOCATION_PERMS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val INITIAL_REQUEST = 1337
        private const val LOCATION_REQUEST = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gps = GPSTracker(this)

        ed_search = findViewById(R.id.ed_search) as EditText

        img_search = findViewById(R.id.img_search) as ImageView
        img_weather = findViewById(R.id.img_weather) as ImageView

        temperature = findViewById(R.id.temperature) as TextView
        txt_temperature = findViewById(R.id.txt_temperature) as TextView
        txt_location = findViewById(R.id.txt_location) as TextView
        txt_humidity = findViewById(R.id.txt_humidity) as TextView
        txt_chance_rain = findViewById(R.id.txt_chance_rain) as TextView
        txt_pressure = findViewById(R.id.txt_pressure) as TextView
        txt_wind_speed = findViewById(R.id.txt_wind_speed) as TextView

        img_search.setOnClickListener {

            if (ed_search.text.toString().equals("")) {

                Toast.makeText(
                    this@MainActivity,
                    "Please enter US city name",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                city_name = ed_search.text.toString()
                getWeatherCityData().execute()

            }

        }

        val currentapiVersion: Int = Build.VERSION.SDK_INT
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (!canAccessLocation()) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
            } else {
                lat = gps!!.getLatitude()
                long = gps!!.getLongitude()

                getWeatherData().execute()
            }
        } else {
            lat = gps!!.getLatitude()
            long = gps!!.getLongitude()

            getWeatherData().execute()
        }

    }

    private fun canAccessLocation(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED === ActivityCompat.checkSelfPermission(
            this,
            perm
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> if (canAccessLocation()) {

                lat = gps!!.getLatitude()
                long = gps!!.getLongitude()

                getWeatherData().execute()

            }

        }
    }

    inner class getWeatherData : AsyncTask<String?, String?, String?>() {
        var inputStr: String? = null
        var customDialog: androidx.appcompat.app.AlertDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            val dialogBuilder =
                this@MainActivity?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            // ...Irrelevant code for customizing the buttons and title
            val inflater = layoutInflater
            val layout: Int?
            layout = R.layout.progress_bar_alert
            val dialogView = inflater.inflate(layout, null)
            dialogBuilder!!.setView(dialogView)
            dialogBuilder.setCancelable(false)
            customDialog = dialogBuilder.create()
            try {
                customDialog!!.show()
            } catch (e: WindowManager.BadTokenException) {
                Log.e("WindowManagerBad ", e.toString())
            }
            val lp = WindowManager.LayoutParams()
            val window = customDialog!!.getWindow()
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            lp.copyFrom(window.attributes)
            //This makes the progressDialog take up the full width
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val screenWidth = size.x
            val d = screenWidth * 0.45
            lp.width = d.toInt()
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
        }

        override fun doInBackground(vararg params: String?): String? {
            val networkStatus: String =
                NetworkUtil.getConnectivityStatusString(this@MainActivity)!!
            val apiService: APIInterface = ApiClient.client!!.create(APIInterface::class.java)
            val call: Call<WeatherList?>? = apiService.getWeather(
                lat.toString(),
                long.toString(),
                "44e64aeee4562cbd12ecd1f13865d6b2"
            )
            call!!.enqueue(object : Callback<WeatherList?> {
                override fun onResponse(
                    call: Call<WeatherList?>,
                    response: Response<WeatherList?>
                ) {
                    Log.d("TAG", "onResponse: $response")
                    if (response.isSuccessful()) {
                        val weatherList: WeatherList? = response.body()
                        try {
                            if (customDialog != null) {
                                customDialog!!.dismiss()
                            }

                            var decimalFormat = DecimalFormat("0.00")

                            Glide.with(this@MainActivity)
                                .load("https://openweathermap.org/img/wn/" + weatherList!!.getWeather()!![0]!!.icon + ".png")
                                .into(img_weather)

                            temperature.text =
                                "" + decimalFormat.format(convertFahrenhettToCelsius(weatherList!!.getMain()!!.temp)) + " Celsius"
                            txt_temperature.text =
                                "" + weatherList!!.getWeather()!![0]!!.main + "\n" + weatherList!!.getWeather()!![0]!!.description
                            txt_location.text = "" + weatherList!!.getName()

                            txt_humidity.text =
                                "" + weatherList!!.getMain()!!.humidity + "%"

                            txt_chance_rain.text =
                                "" + decimalFormat.format(convertFahrenhettToCelsius(weatherList!!.getMain()!!.feels_like)) + " Celsius"

                            txt_pressure.text =
                                "" + weatherList!!.getMain()!!.pressure

                            txt_wind_speed.text =
                                "" + weatherList!!.getWind()!!.speed


                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            if (customDialog != null) {
                                customDialog!!.dismiss()
                            }
                        }
                    } else {

                        if (customDialog != null) {
                            customDialog!!.dismiss()
                        }

                    }
                }

                override fun onFailure(
                    call: Call<WeatherList?>,
                    t: Throwable
                ) {
                    Log.d("TAG", "onFailure: $t")

                    if (customDialog != null) {
                        customDialog!!.dismiss()
                    }
                }
            })
            return null
        }
    }

    inner class getWeatherCityData : AsyncTask<String?, String?, String?>() {
        var inputStr: String? = null
        var customDialog: androidx.appcompat.app.AlertDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            val dialogBuilder =
                this@MainActivity?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            // ...Irrelevant code for customizing the buttons and title
            val inflater = layoutInflater
            val layout: Int?
            layout = R.layout.progress_bar_alert
            val dialogView = inflater.inflate(layout, null)
            dialogBuilder!!.setView(dialogView)
            dialogBuilder.setCancelable(false)
            customDialog = dialogBuilder.create()
            try {
                customDialog!!.show()
            } catch (e: WindowManager.BadTokenException) {
                Log.e("WindowManagerBad ", e.toString())
            }
            val lp = WindowManager.LayoutParams()
            val window = customDialog!!.getWindow()
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            lp.copyFrom(window.attributes)
            //This makes the progressDialog take up the full width
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val screenWidth = size.x
            val d = screenWidth * 0.45
            lp.width = d.toInt()
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
        }

        override fun doInBackground(vararg params: String?): String? {
            val networkStatus: String =
                NetworkUtil.getConnectivityStatusString(this@MainActivity)!!
            val apiService: APIInterface = ApiClient.client!!.create(APIInterface::class.java)
            val call: Call<WeatherCityList?>? = apiService.getCityWeather(
                city_name,
                "44e64aeee4562cbd12ecd1f13865d6b2"
            )
            call!!.enqueue(object : Callback<WeatherCityList?> {
                override fun onResponse(
                    call: Call<WeatherCityList?>,
                    response: Response<WeatherCityList?>
                ) {
                    Log.d("TAG", "onResponse: $response")
                    if (response.isSuccessful()) {
                        val weatherList: WeatherCityList? = response.body()
                        try {
                            if (customDialog != null) {
                                customDialog!!.dismiss()
                            }

                            var decimalFormat = DecimalFormat("0.00")

                            Glide.with(this@MainActivity)
                                .load("https://openweathermap.org/img/wn/" + weatherList!!.getWeather()!![0]!!.icon + ".png")
                                .into(img_weather)

                            temperature.text =
                                "" + decimalFormat.format(convertFahrenhettToCelsius(weatherList!!.getMain()!!.temp)) + " Celsius"
                            txt_temperature.text =
                                "" + weatherList!!.getWeather()!![0]!!.main + "\n" + weatherList!!.getWeather()!![0]!!.description
                            txt_location.text = "" + weatherList!!.getName()

                            txt_humidity.text =
                                "" + weatherList!!.getMain()!!.humidity + "%"

                            txt_chance_rain.text =
                                "" + decimalFormat.format(convertFahrenhettToCelsius(weatherList!!.getMain()!!.feels_like)) + " Celsius"

                            txt_pressure.text =
                                "" + weatherList!!.getMain()!!.pressure

                            txt_wind_speed.text =
                                "" + weatherList!!.getWind()!!.speed


                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            if (customDialog != null) {
                                customDialog!!.dismiss()
                            }
                        }
                    } else {

                        if (customDialog != null) {
                            customDialog!!.dismiss()
                        }

                    }
                }

                override fun onFailure(
                    call: Call<WeatherCityList?>,
                    t: Throwable
                ) {
                    Log.d("TAG", "onFailure: $t")

                    if (customDialog != null) {
                        customDialog!!.dismiss()
                    }
                }
            })
            return null
        }
    }

    fun convertFahrenhettToCelsius(fahrenheit: Double): Double {

        //Convert  Fahrenheit to Celsius
        val celsius = ((fahrenheit - 32) * 5) / 9
        return celsius
    }

}