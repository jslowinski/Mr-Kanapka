package com.mrkanapka.mrkanapkakotlin

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.os.Looper



class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {

        if(p0?.data != null){
            Log.e(TAG, " Data" + p0.data.toString())
        }

        if(p0?.notification != null){
            //Toast.makeText(applicationContext, p0.notification!!.body,Toast.LENGTH_LONG).show()
            Log.e(TAG, " Notification" + p0.notification!!.toString())
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable {
                Toast.makeText(applicationContext,  p0.notification!!.body, Toast.LENGTH_SHORT).show()
            })
        }

    }
}