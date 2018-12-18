package com.example.sakthirajendran.geobuy

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

import me.relex.circleindicator.CircleIndicator

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        val images = intent.getStringArrayExtra("images")
        val product_viewPager = findViewById<ViewPager>(R.id.product_viewPager)
        val indicator = findViewById<View>(R.id.indicator) as CircleIndicator
        val pagerAdapter = MyCustomPagerAdapter(this, images)
        pagerAdapter.setDoOpen(false)
        product_viewPager.adapter = pagerAdapter
        indicator.setViewPager(product_viewPager)
    }

}