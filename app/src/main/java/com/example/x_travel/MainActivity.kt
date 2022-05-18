package com.example.x_travel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val signOut_button = findViewById<Button>(R.id.signOut_button) // 로그아웃 버튼

        val temp_btn = findViewById<Button>(R.id.tempButton)//임시 버튼

        // 로그아웃 버튼
        signOut_button.setOnClickListener {
            AWSMobileClient.getInstance().initialize(
                applicationContext,
                object : Callback<UserStateDetails?> {
                    override fun onResult(userStateDetails: UserStateDetails?) {
                        // 로그아웃 후 로그인 창으로 이동
                        AWSMobileClient.getInstance().signOut()
                        val i = Intent(this@MainActivity, AuthActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                    override fun onError(e: Exception) {}
                })
        }

        // s3 업로드 테스트용 임시버튼

        temp_btn.setOnClickListener {

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

    // 뒤로가기 2번 눌러야 종료
    private val FINISH_INTERVAL_TIME: Long = 2500
    private var backPressedTime: Long = 0
    private var toast: Toast? = null
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime


        // 뒤로 가기 할 경우 홈 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed()
            // 뒤로가기 토스트 종료
            toast!!.cancel()
            finish()
        } else {
            backPressedTime = tempTime
            toast = Toast.makeText(applicationContext, "'뒤로'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
        }
    }
}