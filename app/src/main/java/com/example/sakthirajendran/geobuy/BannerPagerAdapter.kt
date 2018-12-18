package com.example.sakthirajendran.geobuy

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import com.bumptech.glide.Glide

//import com.example.sakthirajendran.demonov25.BusinessActivity

//import com.example.sakthirajendran.demonov25.SearchResultActivity

/**
 * Created by user on 06-04-2018.
 */

class BannerPagerAdapter(internal var context: Context, internal var banners: List<Banner>) : PagerAdapter() {
    internal var layoutInflater: LayoutInflater


    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return banners.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.business_image, container, false)

        val imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        //imageView.setImageResource(images[position]);
        val banner = banners[position]

        Log.i("url", banner.image)
        Glide.with(context)
            .load(banner.image)
            .fitCenter()
            .into(imageView)

        container.addView(itemView)
        imageView.setOnClickListener { openProductsByGroup(banner) }

        return itemView
    }

    private fun openProductsByGroup(category: Banner) {
        var linkId: Array<String>? = null
        if (category.isOrg) {
            linkId = category.linkId
//            val intent = Intent(context, BusinessActivity::class.java)
//            intent.putExtra("orgid", linkId!![0])
//            context.startActivity(intent)
        } else if (category.isProducts) {
            linkId = category.linkId
//            val intent = Intent(context, SearchResultActivity::class.java)
//            intent.putExtra("productIds", linkId)
//            context.startActivity(intent)
        }

    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
