package com.example.sakthirajendran.geobuy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log

class SessionManager// Constructor
    (// Context
    internal var _context: Context
) {
    // Shared Preferences
    internal var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor
        internal set

    // Shared pref mode
    internal var PRIVATE_MODE = 0


    /**
     * Get stored session data
     */
    val userDetails: Map<String, *>
        get() = pref.all

    /**
     * Quick check for login
     */
    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun put(editor: SharedPreferences.Editor) {
        this.editor = editor
        this.editor.commit()
    }

    /**
     * Create login session
     */
    fun createLoginSession(email: String) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true)
        Log.e("createLoginSession", email)
        // Storing name in pref
        editor.commit()
        // Storing email in pref
        editor.putString(KEY_EMAIL, email)

        // commit changes
        editor.commit()
    }

    fun verifyLogin(): Boolean {
        return this.isLoggedIn
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    fun checkLogin(): Boolean {
        Log.e("checkLogin", "checkLogin")
        val status = this.isLoggedIn
        Log.e("status>>>>>>>>>>>>>", status.toString() + "")
        return status
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, MainActivity::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        _context.startActivity(i)
    }

    companion object {

        // Sharedpref file name
        private val PREF_NAME = "GeobuyPref"

        // All Shared Preferences Keys
        private val IS_LOGIN = "IsLoggedIn"

        // User name (make variable public to access from outside)
        val KEY_NAME = "name"

        // Email address (make variable public to access from outside)
        val KEY_EMAIL = "email"
    }
}
