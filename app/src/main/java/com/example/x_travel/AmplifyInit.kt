package com.example.x_travel

import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Amplify.configure
import com.amplifyframework.storage.s3.AWSS3StoragePlugin

class AmplifyInit (){

    public fun intializeAmplify(Context: Context){
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            configure(Context)
            Log.d("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
}