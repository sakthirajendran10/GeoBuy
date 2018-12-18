package com.example.sakthirajendran.geobuy

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
//import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header
import me.relex.circleindicator.CircleIndicator


class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mMap: GoogleMap? = null

    //ProgressDialog pd;

    internal var organizations: List<Organization>? = null
    private var mListener: OnFragmentInteractionListener? = null

    lateinit var businessDetailsDialog: Dialog

    internal lateinit var latLng: LatLng

    internal var filter_distance: TextView? = null

    internal var distance = 5

    internal var filter_location: DiscreteSeekBar? = null

    internal var platLngBounds: LatLngBounds? = null

    lateinit var sessionManager: SessionManager

    lateinit var progressDialog: ProgressDialog

    lateinit var mainActivity: MainActivity


    lateinit var location_text: TextView

    lateinit var location_image: ImageView

    lateinit var nearby_map: View

    internal var result: PendingResult<LocationSettingsResult>? = null

    lateinit var locationManager: LocationManager
    private val isCurrentFragment: Boolean
        get() = mainActivity.module!!.equals("MAPFRAGMENT", ignoreCase = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = this.activity as MainActivity
        mainActivity.module = "MAPFRAGMENT"
        var view: View? = null
        view = inflater!!.inflate(com.example.sakthirajendran.geobuy.R.layout.fragment_map, container, false)
        sessionManager = SessionManager(this.context!!)
        progressDialog = ProgressDialog(this.context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Loading")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        //Switch map_view_active_switch = view.findViewById(R.id.map_view_active_switch);
        location_text = view!!.findViewById(R.id.location_text)
        location_image = view.findViewById(R.id.location_image)
        nearby_map = view.findViewById(R.id.nearby_map)
        updateLocation(location_text)
        location_image.setOnClickListener(locationSelectListener())


        val mapFragment = this.childFragmentManager.findFragmentById(com.example.sakthirajendran.geobuy.R.id.nearby_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        businessDetailsDialog = Dialog(this.context)
        return view
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

    private fun updateLocation(textView: TextView) {
        val map = sessionManager.userDetails
        if (map != null && map["lat"] != null && map["place"] != null) {
            val place = map["place"].toString()
            textView.text = place
            //getLocation(Float.valueOf(lat), Float.valueOf(lon), textView);
        } else {
            textView.text = "Select location"
        }
        textView.setOnClickListener(locationSelectListener())
    }

    private fun getLocation(lat: Float, lon: Float, textView: TextView) {
        val query = StringBuffer("getDistance")
        query.append("?lat1=$lat")
        query.append("&lon1=$lon")
        query.append("&lat2=12.9007")
        query.append("&lon2=80.1969")
        RestCall.get(query.toString(), RequestParams(), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: kotlin.Array<Header>, responseBody: ByteArray) {
                try {
                    val jsonObject = JSONObject(String(responseBody))
                    val location = jsonObject.get("origin").toString()
                    textView.text = location.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "," + location.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: kotlin.Array<Header>, responseBody: ByteArray, error: Throwable) {

            }
        })
    }

    fun ShowPopup(marker: Marker) {
        val latLng = marker.position
        Log.i("latLng", latLng.latitude.toString() + "")
        Log.i("latLng", latLng.longitude.toString() + "")
        val txtclose: TextView

        businessDetailsDialog.setContentView(R.layout.business_details_popup)
        txtclose = businessDetailsDialog.findViewById<View>(R.id.txtclose) as TextView
        txtclose.setOnClickListener { businessDetailsDialog.dismiss() }
        businessDetailsDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        for (org in organizations!!) {
            if (org.orgLat == latLng.latitude && latLng.longitude == org.orgLon) {
                assignOrgDetails(org)
            }
        }

    }

    private fun assignOrgDetails(org: Organization) {

        val btv = businessDetailsDialog.findViewById<TextView>(R.id.business_name)
        val bdtv = businessDetailsDialog.findViewById<TextView>(R.id.business_detail)
        //TextView ftv = businessDetailsDialog.findViewById(R.id.followers_count);
        //TextView ptv = businessDetailsDialog.findViewById(R.id.products_count);
        val etv = businessDetailsDialog.findViewById<TextView>(R.id.business_email)
        val ntv = businessDetailsDialog.findViewById<TextView>(R.id.business_phone)
        //final Button btnfollow = businessDetailsDialog.findViewById(R.id.btnfollow);
        val org_view_profile = businessDetailsDialog.findViewById<Button>(R.id.org_view_profile)
        org_view_profile.setOnClickListener { moveToOrgProfile(org.orgid) }
        btv.text = org.orgname
        val indicator = businessDetailsDialog.findViewById<View>(R.id.indicator) as CircleIndicator

        val viewPager = businessDetailsDialog.findViewById<View>(R.id.business_view_pager) as ViewPager
        val images = org.images
        if (images != null && images.size > 0) {
            val myCustomPagerAdapter = MyCustomPagerAdapter(context!!, images)
            viewPager.adapter = myCustomPagerAdapter
            indicator.setViewPager(viewPager)
        }
        bdtv.text = org.orgaddress
        /* if(org.getProducts()!= null && org.getProducts().size()  > 0) {
            ptv.setText(org.getProducts().size()+"");
        } else
            ptv.setText(0+"");
        */
        /* String[] followers = org.getFollowers();
        if (org.getFollowers() != null) {
            //ftv.setText(org.getFollowers().length+"");

            Map<String, ?> userDetails = sessionManager.getUserDetails();
            String email = (String) userDetails.get("useremail");
            List<String> foll = Arrays.asList(followers);
            if (foll.contains(email)) {
                btnfollow.setText("Following");
                btnfollow.setTextColor(getResources().getColor(R.color.white));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selectedbutton));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(org.getOrgid(), btnfollow, false);
                    }
                });
            } else {
                btnfollow.setText("Follow");
                btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(org.getOrgid(), btnfollow, true);
                    }
                });
            }
        } else {
            //ftv.setText(0 + "");
            btnfollow.setText("Follow");
            btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followOrg(org.getOrgid(), btnfollow, true);
                }
            });
        }*/
        if (org.orgemail != null)
            etv.text = org.orgemail
        else
            etv.text = "-"

        if (org.orgphoneno != null)
            ntv.text = org.orgphoneno
        else
            ntv.text = "-"

        businessDetailsDialog.show()
    }

    private fun moveToOrgProfile(orgid: String?) {
//        val intent = Intent(this.context, BusinessActivity::class.java)
//        intent.putExtra("orgid", orgid)
//        this.startActivity(intent)
    }

    private fun followOrg(orgid: String, btnfollow: Button, follow: Boolean) {
        val userDetails = sessionManager.userDetails
        val email = userDetails["useremail"] as String
        val requestParams = RequestParams()
        requestParams.put("orgid", orgid)
        requestParams.put("follower", email)
        requestParams.put("follow", follow)
        progressDialog.show()
        RestCall.post("followOrg", requestParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: kotlin.Array<Header>, responseBody: ByteArray) {
                if (follow) {
                    btnfollow.text = "Following"
                    btnfollow.setTextColor(resources.getColor(R.color.white))
                    btnfollow.background = resources.getDrawable(R.drawable.selectedbutton)
                    btnfollow.setOnClickListener { followOrg(orgid, btnfollow, false) }
                } else {
                    btnfollow.text = "Follow"
                    btnfollow.setTextColor(resources.getColor(R.color.colorPrimary))
                    btnfollow.background = resources.getDrawable(R.drawable.selected)
                    btnfollow.setOnClickListener { followOrg(orgid, btnfollow, true) }
                }
                progressDialog.dismiss()
            }

            override fun onFailure(statusCode: Int, headers: kotlin.Array<Header>, responseBody: ByteArray, error: Throwable) {
                progressDialog.dismiss()
                toast(resources.getString(R.string.try_later))
            }
        })
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

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        // updateGeobuyLocation(location.getLatitude(), location.getLongitude());
        nativeupdatePosition(location.latitude, location.longitude, true)
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.activity, com.example.sakthirajendran.geobuy.R.raw.mapstyle))

            if (!success) {
                Log.e("MF", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MF", "Can't find style. Error: ", e)
        }

        val userdetails = sessionManager.userDetails
        var lat = userdetails["lat"] as? String
        var lon = userdetails["lon"] as? String
        val place = userdetails["place"] as? String

        if (lat != null && lon != null) {
            //toast(lat+"   "+lon);
            nativeupdatePosition(java.lang.Double.parseDouble(lat), java.lang.Double.parseDouble(lon), false)
        } else {

            /*if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestToTurnOnGPS();

            }*/
            //openPlacePicker();
        }

    }
    // The minimum distance to change Updates in meters


    private fun goToCurrentLocationInMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return
            }

            locationManager = this.context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                GeobuyConstants.MIN_TIME_BW_UPDATES,
                GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun nativeupdatePosition(lattitude: Double, longitude: Double, animate: Boolean) {
        //    if (location != null) {
        val params = RequestParams()
        // Add a marker in Sydney and move the camera

        val pos = LatLng(lattitude, longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(pos)
        markerOptions.title("Your location")
        // Drawable dr = getResources().getDrawable(R.drawable.gpsl);
        if (isCurrentFragment) {
            val bitmapdraw = resources.getDrawable(R.drawable.gps) as BitmapDrawable
            val b = bitmapdraw.bitmap
            val smallMarker = Bitmap.createScaledBitmap(b, 85, 85, false)

            //  BitmapDescriptor d = BitmapDescriptorFactory.fromResource(R.drawable.gps);
            val d = BitmapDescriptorFactory.fromBitmap(smallMarker)
            markerOptions.icon(d)
            markerOptions.draggable(true)
            //  mMap.addMarker(markerOptions);
            val cameraPosition = CameraPosition.Builder()
                .target(pos)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                // .bearing(90)                // Sets the orientation of the camera to east
                //  .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                .build()
            // if(animate)// Creates a CameraPosition from the builder
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            mMap!!.setOnMarkerClickListener { marker ->
                ShowPopup(marker)
                true
            }

            mMap!!.setOnCameraIdleListener {
                val latLngBounds = mMap!!.projection.visibleRegion.latLngBounds
                val params = RequestParams()
                params.put("maxlattitude", latLngBounds.northeast.latitude)
                params.put("maxlongitude", latLngBounds.northeast.longitude)
                params.put("minlattitude", latLngBounds.southwest.latitude)
                params.put("minlongitude", latLngBounds.southwest.longitude)
                if (platLngBounds != null) {
                    if (!platLngBounds!!.contains(LatLng(latLngBounds.northeast.latitude, latLngBounds.southwest.longitude))) {
                        getOrgsByLocation(params)
                    }
                } else {
                    platLngBounds = latLngBounds
                    getOrgsByLocation(params)
                }
            }
        }

        // }
    }


    private fun getOrgsByLocation(params: RequestParams) {
        /* pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();*/
        RestCall.post("storesByPosition", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: kotlin.Array<Header>, responseBody: ByteArray) {

                val jsonString = String(responseBody)
                //toast(new String(responseBody));
                updateUIOnOrganisations(jsonString)
            }

            override fun onFailure(statusCode: Int, headers:  kotlin.Array<Header>, responseBody: ByteArray, error: Throwable) {
                /* if(pd != null)
                    pd.dismiss();*/
                toast(resources.getString(R.string.try_later))
            }
        })
    }

    private fun updateUIOnOrganisations(jsonString: String) {
        try {
            //toast("Success");

            val jsonObject = JSONObject(jsonString)
            Log.i("responseBody", jsonObject.get("data").toString())
            val gson = Gson()
            val type = object : TypeToken<List<Organization>>() {

            }.type
            organizations = gson.fromJson<List<Organization>>(jsonObject.get("data").toString(), type)
            if (organizations != null && !organizations!!.isEmpty()) {
                GeobuyConstants.NEAR_BY_ORGS = organizations
                for (organization in organizations!!) {
                    val markerOptions = MarkerOptions()
                    val latLng = LatLng(organization.orgLat, organization.orgLon)
                    val bitmapdraw = resources.getDrawable(R.drawable.store) as BitmapDrawable
                    val b = bitmapdraw.bitmap
                    val smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false)
                    markerOptions.position(latLng)
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    markerOptions.snippet(organization.orgphoneno)
                    //markerOptions.zIndex(5) .anchor(0.5f, 1);
                    markerOptions.title(organization.orgname)
                    markerOptions.draggable(false)
                    markerOptions.visible(true)
                    mMap!!.addMarker(markerOptions)
                }
            } else {
                toast("No Sellers found in nearby areas")
            }
            // pd.hide();
        } catch (ex: Exception) {
            ex.printStackTrace()
            // pd.hide();
            toast("Failure :: " + ex.message)
        }

    }


    private fun setLatLng(position: LatLng) {
        latLng = position
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<String>, grantResults: IntArray) {

        Log.i("requestCode", "" + requestCode)
        //Checking the request code of our request
        if (requestCode == ACESS_FINE_LOCATION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this.context, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this.context, "Oops you just denied the permission", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun requestToTurnOnGPS() {


        if (ActivityCompat.checkSelfPermission(this.context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap!!.isMyLocationEnabled = true

        val googleApiClient = GoogleApiClient.Builder(this.context!!)
            .addApi(LocationServices.API)
            //.addConnectionCallbacks(this)
            //.addOnConnectionFailedListener(this)
            .build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // locationRequest.setInterval(5 * 1000);
        // locationRequest.setFastestInterval(2 * 1000);
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
                LocationSettingsStatusCodes.SUCCESS -> goToCurrentLocationInMap()
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

    @Throws(IntentSender.SendIntentException::class)
    private fun getS(status: Status) {
        status.startResolutionForResult(this.activity, GeobuyConstants.REQUEST_LOCATION)
    }


    private fun toast(text: String) {
        if (mainActivity.module!!.equals("MAPFRAGMENT", ignoreCase = true)) {
            Snackbar.make(nearby_map, text, Snackbar.LENGTH_SHORT).show()
            // .setAction("Action", null).show();
            // Toast.makeText(this.getContext(), "" + text, Toast.LENGTH_SHORT).show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            /*case GeobuyConstants.REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        requestToTurnOnGPS ();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(getActivity(), "Location not enabled", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;*/
            GeobuyConstants.PLACE_PICKER_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    val place = PlacePicker.getPlace(data!!, this.mainActivity)
                    val latLng = place.latLng
                    updateGeobuyLocation(latLng.latitude, latLng.longitude, place)
                    location_text.text = place.name
                    val cameraPosition = CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(17f)                   // Sets the zoom
                        .build()
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        private val ACESS_FINE_LOCATION_CODE = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}// Required empty public constructor
