package com.example.sakthirajendran.geobuy

class Address {

    var id: String? = null
    var pincode: String? = null
    var city: String? = null
    var state: String? = null
    var doorno: String? = null
    var street: String? = null
    var name: String? = null
    var mobileno: String? = null
    var alternatemobileno: String? = null
    var addresstype: String? = null
    var isIsdefault: Boolean = false

    override fun toString(): String {
        return "$name, $doorno, $street, $city, $state"
    }
}