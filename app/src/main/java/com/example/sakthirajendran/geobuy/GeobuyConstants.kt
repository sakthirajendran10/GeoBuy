package com.example.sakthirajendran.geobuy

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

/**
 * Created by user on 10-04-2018.
 */

public class GeobuyConstants {
    companion object {


        val PLACE_PICKER_REQUEST = 1
        var NEAR_BY_ORGS: List<Organization>? = null

        var ORG_PRODUCTS_TABLE = "organization-products"
        var ORG_TABLE = "organization"
        val REQUEST_LOCATION = 1000
        val HOME_REQUEST_LOCATION = 1001
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute

        val GEOBUY_LAT_LNG_BOUNDS = LatLngBounds(LatLng(java.lang.Double.valueOf("12.554036640276914")!!, java.lang.Double.valueOf("79.61670991033316")!!),
            LatLng(java.lang.Double.valueOf("13.437359837232286")!!, java.lang.Double.valueOf("80.49166694283485")!!))

    }
}