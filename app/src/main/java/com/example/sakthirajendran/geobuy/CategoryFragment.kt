package com.example.sakthirajendran.geobuy

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.app.ProgressDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.loopj.android.http.RequestParams
import org.json.JSONObject
import org.json.JSONException
import java.util.ArrayList
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header


class CategoryFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    var recyclerView2: RecyclerView? = null
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    internal var pd: ProgressDialog? = null
    lateinit var categoryAdapter: CategoryMasterAdapter


    private var mListener: OnFragmentInteractionListener? = null

    // val cts: List<CategoryMaster>? = null

    var categories: List<Category>? = null

    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mainActivity = this.activity as MainActivity
        mainActivity.module = "CATEGORYFRAGMENT"
        val view = inflater!!.inflate(R.layout.fragment_category, container, false)
        recyclerView = view.findViewById(R.id.category_card_view)
        // gridView.setItemAnimator(new DefaultItemAnimator());
        /* List<Category> cts =  new ArrayList<Category>();
        recyclerView2 = view.findViewById(R.id.fashion_grid_view);*/
        pd = ProgressDialog(this.context)
        pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd!!.isIndeterminate = true
        pd!!.setMessage("Loading")
        pd!!.setCancelable(false)
        pd!!.setCanceledOnTouchOutside(false)
        pd!!.show()
        getCategoryFromDB()
        return view
    }

    private fun getCategoryFromDB() {
        val requestParams = RequestParams()
        //requestParams.put("isBanner", false);
        RestCall.get("categories", requestParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                getCategory(String(responseBody!!))
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                if (pd != null)
                    pd!!.dismiss()
                toast(resources.getString(R.string.try_later))
            }
        })
    }

    private fun toast(msg: String) {
        if (mainActivity.module!!.equals("CATEGORYFRAGMENT", ignoreCase = true))
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun formUiforCategory(categoryJson: String) {
        val gson = Gson()
        val type = object : TypeToken<List<CategoryMaster>>() {

        }.type
        val cts = gson.fromJson<List<CategoryMaster>>(categoryJson, type)
        categoryAdapter = CategoryMasterAdapter(this.context!!, cts, this.categories)
        val llm = LinearLayoutManager(this.context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = categoryAdapter
        categoryAdapter.notifyDataSetChanged()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    private fun getCategory(categoryJson: String) {
        var jsonObject: JSONObject? = null
        //List<Category> categories = null;
        try {
            jsonObject = JSONObject(categoryJson)
            val gson = Gson()
            val type = object : TypeToken<List<Category>>() {

            }.type
            categories = gson.fromJson<List<Category>>(jsonObject.get("data").toString(), type)
        } catch (e: JSONException) {
            e.printStackTrace()
            if (pd != null)
                pd!!.dismiss()
            toast(resources.getString(R.string.try_later))
        }

        categoryAdapter = CategoryMasterAdapter(this.context!!, ArrayList(), this!!.categories!!)
        val llm = LinearLayoutManager(this.context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = categoryAdapter

        RestCall.get("categorymaster", RequestParams(), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // toast(new String(responseBody));
                formUiforCategory(String(responseBody))
                pd!!.dismiss()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (pd != null)
                    pd!!.dismiss()
                toast(resources.getString(R.string.try_later))
            }
        })

    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
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
         * @return A new instance of fragment CategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
