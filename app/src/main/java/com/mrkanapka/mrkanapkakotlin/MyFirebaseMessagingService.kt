package com.mrkanapka.mrkanapkakotlin

import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {

        if(p0?.data != null){
            Log.e(TAG, " Data" + p0.data.toString())
        }

        if(p0?.notification != null){
            Log.e(TAG, " Notification" + p0.notification!!.toString())
        }

    }
}