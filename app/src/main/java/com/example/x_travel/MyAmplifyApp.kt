package com.example.x_travel

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify

class MyAmplifyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }



}