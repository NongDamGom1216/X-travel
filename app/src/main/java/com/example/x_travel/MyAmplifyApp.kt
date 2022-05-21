package com.example.x_travel

import android.app.Application
import android.net.Uri
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import java.io.File
import java.io.FileNotFoundException


class MyAmplifyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
//            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
//            uploadFile()

            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

    }

//        val temp_uri =  Uri.parse("src/main/res/mipmap-hdpi/ic_launcher_round.webp")

    fun uploadInputStream(uri: Uri) {
        val stream = contentResolver.openInputStream(uri)

        if (stream != null) {
            try {
                Amplify.Storage.uploadInputStream("ExampleKey", stream,
                    { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
                    { Log.e("MyAmplifyApp", "Upload failed", it) }
                )
            } catch (error: FileNotFoundException) {
                Log.e("MyAmplifyApp", "Could not find file to open for input stream.", error)
            }
        }
    }
    fun uploadFile() {
        try{
            val exampleFile = File(applicationContext.filesDir, "ExampleKey")
            exampleFile.writeText("Example file contents")

            val options = StorageUploadFileOptions.defaultInstance()
            Amplify.Storage.uploadFile("ExampleKey", exampleFile, options,
                { Log.i("MyAmplifyApp", "Fraction completed: ${it.fractionCompleted}") },
                { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
                { Log.e("MyAmplifyApp", "Upload failed", it) }
            )
        }
        catch(e:Exception){
            Log.e("MyAmplifyApp", "Upload failed", e)
        }
    }


}