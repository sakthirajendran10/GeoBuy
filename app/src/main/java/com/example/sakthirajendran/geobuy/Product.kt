package com.example.sakthirajendran.geobuy

import java.util.Arrays

class Product {
    var id: String? = null
    var orgname: String? = null
    var orgid: String? = null
    var title: String? = null
    var shortdesc: String? = null
    var code: String? = null
    var image: Array<String>? = null
    var reviews: List<Review>? = null
    var searchkey: String? = null
    var offer: Int = 0
    var brand: String? = null
    var highlights: List<String>? = null
    var ratings: List<Rating>? = null
    var price: Int = 0
    var rating: Float = 0.toFloat()
    var productDetails: List<Product>? = null
    var masterid: String? = null
    var quanity = 1
    var amountToBePaid: Float = 0.toFloat()
    var gpriority: Int = 0
    var orderStatus = "O"

    constructor(id: String, quanity: Int) {
        this.id = id
        this.quanity = quanity
    }

    constructor() {}

    override fun toString(): String {
        return "Product{" +
                "id=" + id +
                ", orgid='" + orgid + '\''.toString() +
                ", title='" + title + '\''.toString() +
                ", shortdesc='" + shortdesc + '\''.toString() +
                ", code='" + code + '\''.toString() +
                ", image=" + Arrays.toString(image) +
                ", reviews=" + reviews +
                ", searchkey='" + searchkey + '\''.toString() +
                ", offer=" + offer +
                ", brand='" + brand + '\''.toString() +
                ", highLights='" + highlights + '\''.toString() +
                '}'.toString()
    }

    fun setHighLights(highlights: List<String>) {
        this.highlights = highlights
    }
}
