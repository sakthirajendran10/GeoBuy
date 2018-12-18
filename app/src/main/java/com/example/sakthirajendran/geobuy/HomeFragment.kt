package com.example.sakthirajendran.geobuy
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

//import com.example.sakthirajendran.demonov25.BannerPagerAdapter
import cz.msebera.android.httpclient.Header
import me.relex.circleindicator.CircleIndicator


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), LocationListener {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    internal var pd: ProgressDialog? = null
    private var mListener: OnFragmentInteractionListener? = null
    internal var home_ll: LinearLayout? = null
    lateinit var banners: List<Banner>

    lateinit var home_view_switch: Switch
    lateinit var distance_text: TextView
    private var distance = 5
    lateinit var filter_by_distance: DiscreteSeekBar
    lateinit var distance_filter_view: LinearLayout

    lateinit var location_settings: ImageView
    lateinit var sessionManager: SessionManager
    lateinit var location_text: TextView
    internal var currentPage = 0
    lateinit var timer: Timer
    internal val DELAY_MS: Long = 1000//delay in milliseconds before task is to be executed
    internal val PERIOD_MS: Long = 4000

    lateinit var mainActivity: MainActivity
    lateinit var rootView: View
    internal var location: Location? = null
    lateinit var locationManager: LocationManager
    lateinit var details: Map<String, *>
    lateinit var settingsDialog: Dialog
    internal var lat: String? = null
    internal var lon: String? = null

    private var no_offers: LinearLayout? = null

    private val homeViewSwitchStatus: Boolean
        get() {
            location = lastBestLocation
            return if (location != null) {
                true
            } else false
        }

    /**
     * @return the last know best location
     */
    private//toast("Not granted");
    val lastBestLocation: Location?
        get() {
            if (ActivityCompat.checkSelfPermission(this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager = this.context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                GeobuyConstants.MIN_TIME_BW_UPDATES,
                GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
            val locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            var GPSLocationTime: Long = 0
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.time
            }

            var NetLocationTime: Long = 0

            if (null != locationNet) {
                NetLocationTime = locationNet.time
            }

            return if (0 < GPSLocationTime - NetLocationTime) {
                locationGPS
            } else {
                locationNet
            }
        }

    private val isInCurrentFragment: Boolean
        get() = mainActivity.module!!.equals("HOMEFRAGMENT", ignoreCase = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sessionManager = SessionManager(this.context!!)
        mainActivity = this.activity as MainActivity
        mainActivity.module = "HOMEFRAGMENT"
        rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        initializeAllViews()
        getHomeBanners()
        return rootView
    }

    private fun initializeAllViews() {
        details = sessionManager.userDetails
        lat = if (details["lat"] != null) details["lat"].toString() else null
        lon = if (details["lon"] != null) details["lon"].toString() else null

        pd = ProgressDialog(this.context)
        pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd!!.setCancelable(false)
        pd!!.setCanceledOnTouchOutside(false)
        pd!!.isIndeterminate = true
        pd!!.setMessage("Loading")
        location_text = rootView.findViewById(R.id.location_text)
        home_view_switch = rootView.findViewById(R.id.home_view_switch)
        distance_filter_view = rootView.findViewById(R.id.distance_filter_view)
        location_settings = rootView.findViewById(R.id.location_settings)
        location_settings.setOnClickListener { settingsDialog.show() }
        no_offers = rootView.findViewById(R.id.no_offers)
        hideView(distance_filter_view)

        home_view_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                showView(location_settings)
                showView(distance_filter_view)
                lat = if (details["lat"] != null) details["lat"].toString() else null
                lon = if (details["lon"] != null) details["lon"].toString() else null
                val place = if (details["place"] != null) details["place"].toString() else null

                if (lat != null && lon != null)
                    getHomeBanners()
                else
                    openPlacePicker()
                if (place != null)
                    location_text.text = place
            } else {
                hideView(distance_filter_view)
                hideView(location_settings)
                getHomeBanners()
            }
        }
        settingsDialog = Dialog(this.context)
        settingsDialog.setContentView(R.layout.banner_location_settings)
        distance_text = settingsDialog.findViewById(R.id.distance_text)
        filter_by_distance = settingsDialog.findViewById(R.id.filter_by_distance)
        val txtclose = settingsDialog.findViewById<View>(R.id.txtclose) as TextView
        txtclose.setOnClickListener { settingsDialog.dismiss() }
        val select_location = settingsDialog.findViewById<Button>(R.id.select_location)
        select_location.setOnClickListener(locationSelectListener())
        filter_by_distance.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                distance = value
                distance_text.text = "Km($distance)"
                getHomeBanners()
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {

            }
        })
    }

    private fun getHomeBanners() {

        val queryBuffer = StringBuffer("banners")
        val requestParams = RequestParams()
        pd!!.show()
        if (home_view_switch.isChecked) {

            queryBuffer.append("?maxlattitude=" + (java.lang.Double.valueOf(lat)!! + distance * 0.0043352))
            queryBuffer.append("&maxlongitude=" + (java.lang.Double.valueOf(lon)!! + distance * 0.0043352))
            queryBuffer.append("&minlattitude=" + (java.lang.Double.valueOf(lat)!! - distance * 0.0043352))
            queryBuffer.append("&minlongitude=" + (java.lang.Double.valueOf(lon)!! - distance * 0.0043352))
        }
        RestCall.get(queryBuffer.toString(), requestParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                if (pd != null)
                    pd!!.dismiss()
                val gson = Gson()
                val type = object : TypeToken<List<Banner>>() {

                }.type
                banners = gson.fromJson(String(responseBody), type)
                val topBanners = ArrayList<Banner>()
                val offerBanners = ArrayList<Banner>()
                for (banner in banners) {
                    if (!banner.isBanner)
                        topBanners.add(banner)
                    else
                        offerBanners.add(banner)
                }
                updateHomeBanners(topBanners, rootView)
                updateOfferBanners(offerBanners, rootView)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (pd != null)
                    pd!!.dismiss()
                toast(resources.getString(R.string.try_later))
            }
        })
    }

    private fun updateOfferBanners(offerBanners: List<Banner>?, rootView: View) {
        var categoryAdapter: BannerAdapter? = null
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.banner_view)
        if (offerBanners != null && !offerBanners.isEmpty()) {
            showView(recyclerView)
            hideView(no_offers!!)
            if (lat != null && lon != null)
                categoryAdapter = BannerAdapter(this.context!!, offerBanners, home_view_switch.isChecked, LatLng(java.lang.Double.valueOf(lat)!!, java.lang.Double.valueOf(lon)))
            else
                categoryAdapter = BannerAdapter(this.context!!, offerBanners, home_view_switch.isChecked, null)
            recyclerView.layoutManager = GridLayoutManager(this.context, 2)
            recyclerView.adapter = categoryAdapter
        } else {
            showView(no_offers!!)
            hideView(recyclerView)
        }
    }


    private fun updateHomeBanners(banners: List<Banner>, rootView: View) {

        val viewPager = rootView.findViewById<ViewPager>(R.id.home_banner)
        val indicator = rootView.findViewById<View>(R.id.indicator) as CircleIndicator
        val myCustomPagerAdapter = BannerPagerAdapter(this.activity!!, banners)
        viewPager.adapter = myCustomPagerAdapter
        indicator.setViewPager(viewPager)
        if (!mainActivity.isBannerRunning) {
            val handler = Handler()
            val Update = Runnable {
                if (currentPage == banners.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
            }

            timer = Timer() // This will create a new Thread
            timer.schedule(object : TimerTask() { // task to be scheduled

                override fun run() {
                    mainActivity.setRunning(true)
                    handler.post(Update)
                }
            }, DELAY_MS, PERIOD_MS)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onLocationChanged(location: Location) {
        //toast("onLocationChanged");
        nativeupdatePosition(location)
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
        //toast("onStatusChanged");
        nativeupdatePosition(null)
    }

    override fun onProviderEnabled(s: String) {
        //toast("onProviderEnabled");
        //goToCurrentLocationOffers();
    }

    override fun onProviderDisabled(s: String) {
        //toast("onProviderDisabled");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    private fun requestToTurnOnGPS() {
        if (ActivityCompat.checkSelfPermission(this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val googleApiClient = GoogleApiClient.Builder(this.context!!)
            .addApi(LocationServices.API)
            //.addConnectionCallbacks(this)
            //.addOnConnectionFailedListener(this)
            .build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //locationRequest.setInterval(5 * 1000);
        //locationRequest.setFastestInterval(2 * 1000);
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            //                final LocationSettingsStates state = result.getLocationSettingsStates();
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> goToCurrentLocationOffers()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        getS(status)

                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GeobuyConstants.PLACE_PICKER_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    val place = PlacePicker.getPlace(data!!, this.mainActivity)
                    val latLng = place.latLng
                    updateGeobuyLocation(latLng.latitude, latLng.longitude, place)
                    lat = latLng.latitude.toString()
                    lon = latLng.longitude.toString()
                    location_text.text = place.name
                    getHomeBanners()
                }
                else -> {
                }
            }
        }

    }

    private fun updateGeobuyLocation(lat: Double, lon: Double, place: Place) {
        val editor = sessionManager.editor
        editor.putString("lat", lat.toString())
        editor.putString("lon", lon.toString())
        editor.putString("place", place.name.toString())
        sessionManager.put(editor)
    }


    private fun goToCurrentLocationOffers() {
        if (isInCurrentFragment) {
            //requestToTurnOnGPS ();
            openPlacePicker()
            /*try {
                if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        GeobuyConstants.MIN_TIME_BW_UPDATES,
                        GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

    }


    fun nativeupdatePosition(location: Location?) {
        this.location = lastBestLocation
        getHomeBanners()
    }

    @Throws(IntentSender.SendIntentException::class)
    private fun getS(status: Status) {
        status.startResolutionForResult(this.activity, GeobuyConstants.HOME_REQUEST_LOCATION)
    }

    private fun showView(vararg views: View) {
        for (v in views) {
            v.visibility = View.VISIBLE
        }
    }


    private fun hideView(vararg views: View) {
        for (v in views) {
            v.visibility = View.GONE
        }
    }


    private fun toast(s: String) {
        if (isInCurrentFragment) {
            Toast.makeText(this.activity, s, Toast.LENGTH_SHORT).show()

        }
    }

    private fun locationSelectListener(): View.OnClickListener {
        return View.OnClickListener { openPlacePicker() }
    }


    private fun openPlacePicker() {
        val builder = PlacePicker.IntentBuilder()
        builder.setLatLngBounds(GeobuyConstants.GEOBUY_LAT_LNG_BOUNDS)
        try {
            startActivityForResult(builder.build(mainActivity), GeobuyConstants.PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
