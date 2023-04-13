package com.test.weatherforcast

import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class GPSTracker(var mContext: Context) : Service(),
    LocationListener {

    // flag for GPS Status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false
    var canGetLocation = false
    var location1: Location? = null
    var latitude1 = 0.0
    var longitude1 = 0.0
    

    fun getLocation(): Location? {
        try {
            locationManager = mContext
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                if (isGPSEnabled) {
                    if (location1 == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d(
                            "GPS Enabled",
                            "========================GPS Enabled"
                        )
                        if (locationManager != null) {
                            location1 = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            updateGPSCoordinates()
                        }
                    }
                }

                // First get location from Network Provider
                if (null == location1 && isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "====================Network")
                    if (locationManager != null) {
                        location1 = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        updateGPSCoordinates()
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
            }
        } catch (e: Exception) {
            // e.printStackTrace();
            Log.e(
                "Error : Location",
                "Impossible to connect to LocationManager", e
            )
        }
        return location1
    }

    fun updateGPSCoordinates() {
        if (location1 != null) {
            latitude1 = location1!!.latitude
            longitude1 = location1!!.longitude
            Log.d(
                "GPS Enabled", "========================GPS Enabled"
                        + latitude1
            )
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location1 != null) {
            latitude1 = location1!!.latitude
        }
        println(latitude1)
        return latitude1
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location1 != null) {
            longitude1 = location1!!.longitude
        }
        return longitude1
    }

    /**
     * Function to check GPS/wifi enabled
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS")

        // Setting Dialog Message
        alertDialog.setMessage("GPS Alert")

        // On Pressing Setting button
        alertDialog.setPositiveButton(
            "ok"
        ) { dialog, which ->
            val intent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            mContext.startActivity(intent)
        }

        // On pressing cancel button
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    /**
     * Get list of address by latitude and longitude
     *
     * @return null or List<Address>
    </Address> */
    fun getGeocoderAddress(context: Context?): List<Address>? {
        if (location1 != null) {
            val geocoder = Geocoder(context!!, Locale.ENGLISH)
            try {
                Log.i(
                    "", "==========================" + latitude1 + "---"
                            + longitude1
                )
                val addresses =
                    geocoder.getFromLocation(
                        latitude1,
                        longitude1, 1
                    )
                Log.i(
                    "", "===========address size==============="
                            + addresses!!.size
                )
                return addresses
            } catch (e: IOException) {
                // e.printStackTrace();
                Log.e(
                    "Error : Geocoder", "Impossible to connect to Geocoder",
                    e
                )
            }
        }
        return null
    }

    /**
     * Try to get AddressLine
     *
     * @return null or addressLine
     */
    fun getAddressLine(context: Context?): String? {
        val addresses =
            getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address = addresses[0]
            address.getAddressLine(0)
        } else {
            null
        }
    }

    /**
     * Try to get Locality
     *
     * @return null or locality
     */
    fun getLocality(context: Context?): String? {
        val addresses =
            getGeocoderAddress(context)
        Log.i(
            "", "=======================getGeocoderAddress==========="
                    + addresses
        )
        return if (addresses != null && addresses.size > 0) {
            val address = addresses[0]
            address.locality
        } else {
            null
        }
    }

    /**
     * Try to get Postal Code
     *
     * @return null or postalCode
     */
    fun getPostalCode(context: Context?): String? {
        val addresses =
            getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address = addresses[0]
            address.postalCode
        } else {
            null
        }
    }

    /**
     * Try to get CountryName
     *
     * @return null or postalCode
     */
    fun getCountryName(context: Context?): String? {
        val addresses =
            getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address = addresses[0]
            address.countryName
        } else {
            null
        }
    }

    override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        // Declaring a Location Manager
        var locationManager: LocationManager? = null
        // The minimum distance to change updates in metters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10

        // metters
        // The minimum time beetwen updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
            .toLong()
    }

    init {
        getLocation()
    }
}