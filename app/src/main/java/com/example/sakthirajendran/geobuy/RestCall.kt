package com.example.sakthirajendran.geobuy

import android.util.Log

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.SyncHttpClient

object RestCall {
    //private static final String BASE_URL = "http://server-dot-pingme-191816.appspot.com/";
    private val BASE_URL = "https://geobuy-viki19nesh.c9users.io/"
    private val client = AsyncHttpClient()

    private val sclient = SyncHttpClient()

    operator fun get(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        // client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler)
    }

    fun sGet(url: String, params: RequestParams, responseHandler: JsonHttpResponseHandler) {
        //sclient.addHeader("Accept", "application/json");
        sclient.get(getAbsoluteUrl(url), params, responseHandler)
    }

    operator fun get(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler, `object`: Any) {
        client.addHeader("Accept", "application/json")
        client.get(getAbsoluteUrl(url), params, responseHandler)
    }

    fun post(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        Log.i(url, params.toString())
        client.post(getAbsoluteUrl(url), params, responseHandler)
    }

    fun getByUrl(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        Log.i(url, params.toString())
        client.get(url, params, responseHandler)
    }

    fun postByUrl(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        Log.i(url, params.toString())
        client.post(url, params, responseHandler)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        Log.i("relativeUrl", BASE_URL + relativeUrl)
        return BASE_URL + relativeUrl
    }
}