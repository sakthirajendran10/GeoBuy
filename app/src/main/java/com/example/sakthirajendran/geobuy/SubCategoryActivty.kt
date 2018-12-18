package com.example.sakthirajendran.geobuy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sub_category.*
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

class SubCategoryActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)

        setSupportActionBar(this.findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val appBar = supportActionBar
        appBar!!.title = "Movies List with RecyclerView"

        toolbar.setNavigationOnClickListener() {
            finish()
        }


        val intent = intent
        val categoryJson = intent.getStringExtra("category")
        val gson = Gson()
//        val type = object : TypeToken<Category>() { }.type

        val category = gson.fromJson<Category>(categoryJson)
        toolbar.setTitle(category.tittle)
        setTitle(category.tittle)

        if (category.subcategory != null && !category.subcategory!!.isEmpty()) {
//            val subCategoryAdapter = SubCategoryAdapter(this, category.subcategory)
            val recyclerView = findViewById<RecyclerView>(R.id.category_recycler_view)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
//            recyclerView.adapter = subCategoryAdapter
        }
    }

    inline fun <reified T> Gson.fromJson(categoryJson: String) = this.fromJson<T>(categoryJson, object: TypeToken<T>() {}.type)

}
