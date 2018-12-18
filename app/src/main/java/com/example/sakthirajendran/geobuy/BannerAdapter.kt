package com.example.sakthirajendran.geobuy

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONException
import org.json.JSONObject

//import com.example.sakthirajendran.demonov25.BusinessActivity
//import com.example.sakthirajendran.demonov25.SearchResultActivity
import cz.msebera.android.httpclient.Header

/**
 * Created by user on 25-03-2018.
 */

class BannerAdapter(private val mCtx: Context, private val categories: List<Banner>, private val isLocation: Boolean, private val location: LatLng?) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.banner_item, null)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val category = categories[position]
        Glide.with(mCtx)
            .load(category.image)
            .into(holder.imageView)
        holder.imageView.setOnClickListener { openProductsByGroup(category) }
        if (isLocation) {
            updateDistanceAndTravelDuration(holder, category)
        }

    }

    private fun updateDistanceAndTravelDuration(holder: BannerViewHolder, category: Banner) {
        val query = StringBuffer("getDistance")
        query.append("?lat1=" + location!!.latitude)
        query.append("&lon1=" + location!!.longitude)
        query.append("&lat2=" + category.lat)
        query.append("&lon2=" + category.lon)
        RestCall.get(query.toString(), RequestParams(), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val jsonObject = JSONObject(String(responseBody))
                    holder.distance.text = jsonObject.get("distance").toString()
                    holder.duration.text = jsonObject.get("duration").toString()
                    showView(holder.offer_distance_ll)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                //toast(mCtx.getResources().getString(R.string.try_later));
            }
        })
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


    private fun openProductsByGroup(category: Banner) {
        var linkId: Array<String>? = null
        if (category.isOrg) {
            linkId = category.linkId
//            val intent = Intent(mCtx, BusinessActivity::class.java)
//            intent.putExtra("orgid", linkId!![0])
//            mCtx.startActivity(intent)
        } else if (category.isProducts) {
            linkId = category.linkId
//            val intent = Intent(mCtx, SearchResultActivity::class.java)
//            intent.putExtra("productIds", linkId)
//            mCtx.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    private fun toast(s: String) {
        Toast.makeText(mCtx, s, Toast.LENGTH_SHORT).show()
    }


    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView

        var offer_distance_ll: LinearLayout

        var distance: TextView

        var duration: TextView

        init {
            imageView = itemView.findViewById(R.id.category_image)
            offer_distance_ll = itemView.findViewById(R.id.offer_distance_ll)
            distance = itemView.findViewById(R.id.distance)
            duration = itemView.findViewById(R.id.duration)
        }
    }
}
