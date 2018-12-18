package com.example.sakthirajendran.geobuy


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

//import apps.codette.geobuy.AddressListAcitivty
//import apps.codette.geobuy.CartActivity
//import apps.codette.geobuy.NotificationActivity
//import apps.codette.geobuy.OrderDetailsActivity

//import apps.codette.geobuy.SearchResultActivity
//import apps.codette.geobuy.WishListActivity

/**
 * Created by user on 08-04-2018.
 */

class UserFragmentItemAdapter(private val mCtx: Context, private val items: List<UserFragmentItem>) : RecyclerView.Adapter<UserFragmentItemAdapter.UserFragmentItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFragmentItemHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.user_list_item, null)
        return UserFragmentItemHolder(view)
    }

    override fun onBindViewHolder(holder: UserFragmentItemHolder, position: Int) {
        val product = items[position]
        holder.textViewTitle.text = product.text
        holder.imageView.setImageDrawable(mCtx.resources.getDrawable(product.drawable))
        holder.relativeLayout.setOnClickListener { openSelected(position + 1) }
    }

    private fun openSelected(position: Int) {
        when (position) {
            1 -> {
//                val intent = Intent(mCtx, CartActivity::class.java)
//                mCtx.startActivity(intent)
            }
            2 -> {
//                val intent = Intent(mCtx, OrderDetailsActivity::class.java)
//                mCtx.startActivity(intent)
            }
            3 -> {
//                val intent = Intent(mCtx, NotificationActivity::class.java)
//                mCtx.startActivity(intent)
            }
            4 -> {
//                val intent = Intent(mCtx, WishListActivity::class.java)
//                mCtx.startActivity(intent)
            }
            5 -> {
//                val intent = Intent(mCtx, AddressListAcitivty::class.java)
//                mCtx.startActivity(intent)
            }
            6 -> {
            }
            7 -> {
            }
        }
    }

    private fun toast(s: String) {
        Toast.makeText(mCtx, "" + s, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class UserFragmentItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewTitle: TextView
        var imageView: ImageView
        var relativeLayout: RelativeLayout

        init {
            textViewTitle = itemView.findViewById(R.id.item_text)
            imageView = itemView.findViewById(R.id.item_image)
            relativeLayout = itemView.findViewById(R.id.item_rl)
        }
    }
}
