package com.example.sakthirajendran.geobuy

import java.io.Serializable

/**
 * Created by user on 24-03-2018.
 */

class Organization : Serializable {
    var orgname: String? = null
    var orgphoneno: String? = null
    var orgemail: String? = null
    var orgid: String? = null
    var orgLat: Double = 0.toDouble()
    var orgLon: Double = 0.toDouble()
    var products: List<Product>? = null
    var followers: Array<String>? = null
    var isPrime = false
    var category: String? = null
    var orgaddress: String? = null
    var images: Array<String>? = null
    var logo: String? = null
    var reviews: List<Review>? = null
    var rating: Float = 0.toFloat()

    constructor(orgname: String, images: Array<String>) {
        this.orgname = orgname
        this.images = images
    }

    constructor(orgname: String, orgid: String) {
        this.orgname = orgname
        this.orgid = orgid
    }

    override fun toString(): String {
        return "Organization{" +
                "orgname='" + orgname + '\''.toString() +
                ", orgphoneno='" + orgphoneno + '\''.toString() +
                ", orgid='" + orgid + '\''.toString() +
                ", orgLat=" + orgLat +
                ", orgLon=" + orgLon +
                ", isPrime=" + isPrime +
                ", category='" + category + '\''.toString() +
                '}'.toString()
    }
}
