package com.example.sakthirajendran.geobuy


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener


abstract class CClocation(internal var ctx: Context, internal var activity: Activity) : LocationListener {


    // flag for GPS status
    internal var isGPSEnabled = false

    // flag for network status
    internal var isNetworkEnabled = false

    // flag for GPS status
    internal var canGetLocation = false

    internal var location: Location? = null // location
    internal var latitude: Double = 0.toDouble() // latitude
    internal var longitude: Double = 0.toDouble() // longitude

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    // getting GPS status
    // getting network status
    // TODO: Consider calling
    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.
    //  return TODO;
    // Toast.makeText(this.ctx, "No Network permission", Toast.LENGTH_LONG).show();
    // Toast.makeText(this.ctx, "longitude latitude"+latitude+" :: "+longitude, Toast.LENGTH_LONG).show();
    // if GPS Enabled get lat/long using GPS Services
    // Toast.makeText(this.ctx, "getLastKnownLocation :: "+location, Toast.LENGTH_SHORT).show();
    // First get location from Network Provider
    // Toast.makeText(this.ctx, "getLastKnownLocation :: "+location, Toast.LENGTH_SHORT).show();
    val location2: Location?
        get() {
            try {
                locationManager = myAndroidContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                Toast.makeText(this.ctx, "isGPSEnabled :: $isGPSEnabled  isNetworkEnabled $isNetworkEnabled", Toast.LENGTH_SHORT).show()
                Log.i("d", "isGPSEnabled $isGPSEnabled")
                Log.i("d", "isNetworkEnabled $isNetworkEnabled")
                if (!isGPSEnabled && !isNetworkEnabled) {
                    ActivityCompat.requestPermissions(this.activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                } else {
                    this.canGetLocation = true
                    if (isNetworkEnabled) {
                        if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d("activity", "LOC Network Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                Log.d("activity", "LOC by Network")
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                            Log.d("activity", "RLOC: GPS Enabled")
                            if (locationManager != null) {
                                location = locationManager!!
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    Log.d("activity", "RLOC: loc by GPS")
                                    nativeupdatePosition(location!!)
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                    Toast.makeText(this.ctx, "longitude latitude$latitude :: $longitude", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this.ctx, "location is null", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        ActivityCompat.requestPermissions(this.activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    }
                    if (isNetworkEnabled) {
                        if (location == null) {
                            locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)

                            Log.d("Network", "Network")

                            if (locationManager != null) {
                                location = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                if (location != null) {
                                    nativeupdatePosition(location!!)
                                    Log.d("activity", "RLOC: loc by GPS")

                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                    Toast.makeText(this.ctx, "longitude latitude$latitude :: $longitude", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this.ctx, "location is null", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Log.d("activity", "RLOC: Location xx $latitude $longitude")

            return location
        }

    override fun onLocationChanged(loc: Location) {
        Log.d("activity", "RLOC: onLocationChanged")
        location = loc
        invokeNativeCode(loc)

    }

    override fun onProviderDisabled(provider: String) {
        Log.d("activity", "RLOC: onProviderDisabled")
    }

    override fun onProviderEnabled(provider: String) {
        val locManager = myAndroidContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("activity", "RLOC: onProviderEnabled")
        if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        val location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            var latitude = 0.0
            var longitude = 0.0
            latitude = location.latitude
            longitude = location.longitude
            Log.d("activity", "RLOC: onProviderEnabled $latitude $longitude")
        }

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d("activity", "RLOC: onStatusChanged")

    }

    fun run(my: Context) {
        Log.d("activity", "RLOC: run")
        myAndroidContext = my
    }

    abstract fun nativeupdatePosition(loc: Location)

    fun invokeNativeCode(loc: Location) {
        latitude = location!!.latitude
        longitude = location!!.longitude
        Toast.makeText(this.ctx, "invokeNativeCode latitude$latitude :: $longitude", Toast.LENGTH_SHORT).show()
        nativeupdatePosition(loc)
    }

    companion object {

        internal var locationListener: LocationListener? = null
        private val TAG = "CClocation"
        internal lateinit var myAndroidContext: Context
        internal var instance: CClocation? = null

        fun setContext(c: Context) {
            Log.d("activity", "RLOC: setContext")
            myAndroidContext = c
        }


        fun helloWorld() {
            Log.v("InternetConnection", "HELLO WORLD")
        }

        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute


        internal var truc: CClocation? = null


        fun getLocation(ctx: Context, activity: Activity): Location? {
            Log.d("activity", "getLocation")
            setContext(ctx)
            //truc = new CClocation(ctx, activity);

            return truc!!.location2
        }
    }

}
