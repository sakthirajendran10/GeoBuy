package com.example.sakthirajendran.geobuy

class Banner {
    var id: String? = null
    var subtittle: String? = null
    var tittle: String? = null
    var image: String? = null
    var category: String? = null
    var isBanner: Boolean = false
    var isOrg: Boolean = false
    var isProducts: Boolean = false
    var linkId: Array<String>? = null
    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()

    override fun toString(): String {
        return "Category{" +
                "id=" + id +
                ", subtittle='" + subtittle + '\''.toString() +
                ", tittle='" + tittle + '\''.toString() +
                '}'.toString()
    }
}