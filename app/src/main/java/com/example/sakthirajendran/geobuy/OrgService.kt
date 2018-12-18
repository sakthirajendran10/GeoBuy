package com.example.sakthirajendran.geobuy

import android.content.Context

public class OrgService {

    var context: Context? = null
    lateinit var sessionManager: SessionManager

    fun getCartItems(context: Context): Int {
        var items = 0
        sessionManager = SessionManager(context)
        val userDetails = sessionManager.userDetails
        var products: String? = null
        if (userDetails["cart"] != null && !userDetails["cart"].toString().trim { it <= ' ' }.isEmpty()) {
            products = userDetails["cart"] as String
            items = products.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
        }
        return items
    }

    companion object {

        var organization: Organization? = null
    }

}
