package com.example.sakthirajendran.geobuy

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import sun.applet.AppletResourceLoader.getImage
import java.util.ArrayList


class CategoryAdapter(private val mCtx: Context, private val categories: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.category, null)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        Glide.with(mCtx)
            .load(category.image)
            .into(holder.imageView)
        holder.imageView.setOnClickListener {
            //openProductsForCategory(category.getId());
            val gson = Gson()
            val type = object : TypeToken<Category>() {

            }.type
            val json = gson.toJson(category, type)
            openSubCategory(json)
        }

        holder.textViewTitle.text = category.tittle
    }

    private fun openSubCategory(json: String) {
        val intent = Intent(mCtx, SubCategoryActivty::class.java)
        intent.putExtra("category", json)
        mCtx.startActivity(intent)
    }

    private fun openProductsForCategory(categoryId: String) {
//        val intent = Intent(mCtx, SearchResultActivity::class.java)
//        intent.putExtra("categoryId", categoryId)
//        mCtx.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun getItemId(i: Int): Long {
        return 0
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewTitle: TextView
        var imageView: ImageView

        init {

            textViewTitle = itemView.findViewById(R.id.category_text)
            imageView = itemView.findViewById(R.id.category_image)
        }
    }
}
