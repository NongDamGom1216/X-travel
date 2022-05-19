package com.example.x_travel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails


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


            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, REQUEST_STORAGE)
//
//            val exampleFile = File(applicationContext.filesDir, "ExampleKey")
//            exampleFile.writeText("Example file contents")

//            Amplify.Storage.uploadFile(
//                System.currentTimeMillis().toString(),
//                exampleFile,
//                { result -> Log.d("MyAmplifyApp", "Successfully uploaded: " + result) },
//                { error -> Log.d("MyAmplifyApp", "Upload failed", error) }
//            )

//            MyAmplifyApp.uploadFile()

            val PERMISSION_Album = 101 // 앨범 권한 처리

//            requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_Album)

            val intent1 = Intent(Intent.ACTION_PICK)
            intent1.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent1, REQUEST_STORAGE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {

                REQUEST_STORAGE -> {
                    data?.data?.let {
//                            uri ->
//                        imageView.setImageURI(uri)
                        Log.i("result", it.toString())
                    }
                }
            }
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

    val REQUEST_STORAGE = 1234


//    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
//        Log.d("requirePermissions","권한 요청")
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            permissionGranted(requestCode)
//        } else {
//            // isAllPermissionsGranted : 권한이 모두 승인 되었는지 여부 저장
//            // all 메서드를 사용하면 배열 속에 들어 있는 모든 값을 체크할 수 있다.
//            val isAllPermissionsGranted =
//                permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
//            if (isAllPermissionsGranted) {
//                permissionGranted(requestCode)
//            } else {
//                // 사용자에 권한 승인 요청
//                ActivityCompat.requestPermissions(this, permissions, requestCode)
//            }
//        }
//    }
//    private fun permissionGranted(requestCode: Int) {
//        when (requestCode) {
//            PERMISSION_CAMERA -> openCamera()
//        }
//    }
//
//    private fun permissionDenied(requestCode: Int) {
//        when (requestCode) {
//            PERMISSION_CAMERA -> Toast.makeText(
//                this,
//                "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }


}