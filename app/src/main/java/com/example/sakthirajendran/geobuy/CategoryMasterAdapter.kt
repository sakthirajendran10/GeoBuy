package com.example.sakthirajendran.geobuy

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class CategoryMasterAdapter(internal var ctx: Context,
                            internal var categoriesMaster: kotlin.collections.List<CategoryMaster>,
                            internal var categories: kotlin.collections.List<Category>?
) : RecyclerView.Adapter<CategoryMasterAdapter.CategoryMasterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMasterViewHolder {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.category_set, null)
        return CategoryMasterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryMasterViewHolder, position: Int) {
        // Log.i("onBindViewHolder","categories.get(position).getName()" +categoriesMaster.get(position).getName());
        val categoryMaster = categoriesMaster[position]
        holder.textViewTitle.text = categoryMaster.name
        val key = categoryMaster.key
        formUiforCategory(categories!!, holder.recyclerView, key)
        /* RestCall.get("categories", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                formUiforCategory(new String(responseBody), holder.recyclerView);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
               // pd.dismiss();
            }
        });*/

    }


    private fun formUiforCategory(cts: kotlin.collections.List<Category>, recyclerView: RecyclerView, key: String?) {
        try {
            val fileterdCategory = ArrayList<Category>()
            for (category in cts) {
                Log.i(key, category.category)
                if (category.category!!.equals(key!!, ignoreCase = true)) {
                    fileterdCategory.add(category)
                }
            }
            val categoryAdapter = CategoryAdapter(ctx, fileterdCategory)
            recyclerView.layoutManager = GridLayoutManager(ctx, 3)
            recyclerView.adapter = categoryAdapter
        } catch (e: Exception) {

        }

    }

    override fun getItemCount(): Int {
        return categoriesMaster.size
    }

    inner class CategoryMasterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewTitle: TextView
        var recyclerView: RecyclerView

        init {

            textViewTitle = itemView.findViewById(R.id.category_head)
            recyclerView = itemView.findViewById(R.id.category_grid_view)
        }
    }
}
