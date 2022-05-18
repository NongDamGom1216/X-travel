package com.example.x_travel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.amplifyframework.core.Amplify
import java.io.File

class dummyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        val btn = findViewById<Button>(R.id.uploadButton)

        btn.setOnClickListener {

            val exampleFile = File(applicationContext.filesDir, "ExampleKey")
            exampleFile.writeText("Example file contents")

            Amplify.Storage.uploadFile(
                System.currentTimeMillis().toString(),
                exampleFile,
                { result -> Log.d("MyAmplifyApp", "Successfully uploaded: " + result) },
                { error -> Log.d("MyAmplifyApp", "Upload failed", error) }
            )
        }
    }
}