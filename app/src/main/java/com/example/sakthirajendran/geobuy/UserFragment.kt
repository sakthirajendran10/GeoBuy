package com.example.sakthirajendran.geobuy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import java.util.ArrayList

import cz.msebera.android.httpclient.Header


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//MainActivity mainActivity;

class UserFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    private val mAuth: FirebaseAuth? = null
    internal var personId: String? = null
    private val RC_SIGN_IN = 9001
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var userName: String? = null
    private var userEmail: String? = null
    private var userImg: String? = null
    private val userPhoneNumber: String? = null
    private var userId: String? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    lateinit var sessionManager: SessionManager

    lateinit var googleButton: Button

    internal var pd: ProgressDialog? = null

    lateinit var layout: LinearLayout
    lateinit var accounLatout: LinearLayout

    private var doClose: Boolean = false
    internal var userFragmentItems: MutableList<UserFragmentItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //mainActivity = (MainActivity) this.getActivity();
        //mainActivity.setModule("USERFRAGMENT");
        // Inflate the layout for this fragment
        userFragmentItems = ArrayList()
        userFragmentItems!!.add(UserFragmentItem(R.drawable.cart_black, "My Cart"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.basketdarkbg, "My Orders"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.ic_notifications_black_24dp, "Notifications"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.favorite_black, "Wish list"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.ic_subject_black_24dp, "My Address"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.supervisor_black, "Support"))
        userFragmentItems!!.add(UserFragmentItem(R.drawable.copyright_black, "Legal"))
        val view = inflater!!.inflate(R.layout.fragment_user, container, false)
        // mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager(this.context!!)
        layout = view.findViewById(R.id.signin_layout)
        accounLatout = view.findViewById(R.id.user_account_view)
        val userDetails = sessionManager.userDetails
        if (userDetails != null && !userDetails.isEmpty() && userDetails["useremail"] != null) {
            hideView(layout)
            showView(accounLatout)

            val user_name = this.activity!!.findViewById<TextView>(R.id.user_name)
            val user_email = this.activity!!.findViewById<TextView>(R.id.user_email)
            val userImage = this.activity!!.findViewById<ImageView>(R.id.user_image)
            user_name.text = userDetails["username"] as String
            user_email.text = userDetails["useremail"] as String
            if (userDetails["image"] != null && !userDetails["image"].toString().equals("null", ignoreCase = true))
                Glide.with(this.context).load(userDetails["image"] as String).into(userImage)

            val recyclerView = view.findViewById<RecyclerView>(R.id.user_item)
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            recyclerView.adapter = UserFragmentItemAdapter(this.context!!, userFragmentItems as ArrayList<UserFragmentItem>)
        } else {
            hideView(accounLatout)
            showView(layout)

            initializeGooglePlusSettings()
            googleButton = view.findViewById(R.id.google_signin)
            googleButton.setOnClickListener { googleSignIn() }
        }

        return view
    }
    private fun googleSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount

                userName = account!!.displayName //this is the name gotten from the Google Account, you can choose to store this in a Shared pref and use in all activities or whatever
                userEmail = account.email
                userImg = account.photoUrl!!.toString()
                userId = account.id
                syncwithGeobuyUser()

            } else {
                toast(this.resources.getString(R.string.try_later))
            }
        }
    }

    private fun syncwithGeobuyUser() {
        pd = ProgressDialog(this.context)
        pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd!!.setCancelable(false)
        pd!!.setCanceledOnTouchOutside(false)
        pd!!.isIndeterminate = true
        pd!!.setMessage("Loading")
        pd!!.show()

        val requestParams = RequestParams()
        requestParams.put("userid", userId)
        requestParams.put("username", userName)
        requestParams.put("useremail", userEmail)
        requestParams.put("image", userImg)

        RestCall.post("syncUser", requestParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                if (pd != null)
                    pd!!.dismiss()
                signedInwithGeobuy(String(responseBody))

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (pd != null)
                    pd!!.dismiss()
                toast(resources.getString(R.string.try_later))
            }
        })
    }

    private fun signedInwithGeobuy(responseBody: String) {
        val type = object : TypeToken<User>() {

        }.type
        val user = Gson().fromJson<User>(responseBody, type)

        val user_name = this.activity!!.findViewById<TextView>(R.id.user_name)
        val user_email = this.activity!!.findViewById<TextView>(R.id.user_email)
        val userImage = this.activity!!.findViewById<ImageView>(R.id.user_image)
        val editor = sessionManager.editor
        editor.putString("username", userName)
        editor.putString("useremail", userEmail)
        editor.putString("image", userImg)
        editor.putString("userid", userId)
        val stringBuilder = StringBuilder()
        if (user.cart != null && user.cart!!.size > 0) {
            val products = user.cart
            for (product in products!!)
                stringBuilder.append("," + product.id!!)

            editor.putString("cart", stringBuilder.substring(1))
        }
        sessionManager.put(editor)

        hideView(layout)
        showView(accounLatout)
        user_name.text = userName
        user_email.text = userEmail
        val recyclerView = this.activity!!.findViewById<RecyclerView>(R.id.user_item)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = UserFragmentItemAdapter(this.context!!, this!!.userFragmentItems!!)
        /*RelativeLayout user_cart = accounLatout.findViewById(R.id.user_cart);
        user_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCart();
            }
        });
        user_cart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                moveToCart();
               return false;
            }
        });*/

        if (userImg != null)
            Glide.with(this.context).load(userImg).into(userImage)

        if (doClose)
            this.activity!!.finish()


    }

    private fun moveToCart() {
//        val intent = Intent(this.activity, CartActivity::class.java)
//        startActivity(intent)
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //here we take the account that was passed to this method when the authentication with Gmail was successful, and then use that to perform
        //a firebase authentication
        //initialize my Firebase Auth (get an instance of it)
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this.activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser


                    //you can add an intent of the new activity where you want the user to go to next when the authentication is successful
                    //verifyWithCodette(user);
                    toast("Success:Success " + userName!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                    toast("signInWithCredential:failure")
                }

                // ...
            }

    }

    private fun initializeGooglePlusSettings() {

        //intialize the google sign in
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        //get an instance of the google sign in
//        mGoogleApiClient = GoogleApiClient.Builder(this.context!!)
//            .enableAutoManage(this.activity!!, this)
//            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//            .build()
    }

    private fun toast(s: String) {
        // if(mainActivity.getModule().equalsIgnoreCase("USERFRAGMENT"))
        Toast.makeText(this.activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        toast("Connection failed")
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.stopAutoManage(this!!.activity!!)
            mGoogleApiClient!!.disconnect()
        }

    }


    fun doCloseActivity(doClose: Boolean) {
        this.doClose = doClose
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        private val TAG = "UserFragment"


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): UserFragment {
            val fragment = UserFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
