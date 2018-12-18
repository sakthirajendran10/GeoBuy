package com.example.sakthirajendran.geobuy

class Review(var id: String?, var heading: String?, var review: String?, var time: String?, var ratings: Float, var user: String?) {
    var userName: String? = null

    override fun toString(): String {
        return "Review{" +
                "id=" + id +
                ", heading='" + heading + '\''.toString() +
                ", review='" + review + '\''.toString() +
                ", time='" + time + '\''.toString() +
                ", ratings=" + ratings +
                ", user='" + user + '\''.toString() +
                ", userName='" + userName + '\''.toString() +
                '}'.toString()
    }
}
