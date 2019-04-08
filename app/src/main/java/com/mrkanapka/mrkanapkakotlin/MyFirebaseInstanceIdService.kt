package com.mrkanapka.mrkanapkakotlin

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

const val TAG = "MyFirebase"

class MyFirebaseInstanceIdService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Token perangkat ini: ${token}")
    }
}