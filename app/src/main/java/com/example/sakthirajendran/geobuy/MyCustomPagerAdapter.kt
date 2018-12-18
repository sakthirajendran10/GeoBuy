package com.example.sakthirajendran.geobuy

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import com.bumptech.glide.Glide

/**
 * Created by user on 06-04-2018.
 */

class MyCustomPagerAdapter(internal var context: Context, internal var images: Array<String>) : PagerAdapter() {
    internal var layoutInflater: LayoutInflater
    internal var doOpen = true


    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setDoOpen(value: Boolean) {
        doOpen = value
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.business_image, container, false)

        val imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        //imageView.setImageResource(images[position]);


        Log.i("url", images[position])
        Glide.with(context)
            .load(images[position])
            .fitCenter()
            .into(imageView)

        container.addView(itemView)
        /*Glide.with(context)
                .load(images[position])
                .into(holder.imageView);*/
        //listening to image click
        imageView.setOnClickListener { goToImageActivity() }

        return itemView
    }

    private fun goToImageActivity() {
        if (doOpen) {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra("images", images)
            context.startActivity(intent)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
