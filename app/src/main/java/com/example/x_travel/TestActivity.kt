package com.example.x_travel

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import com.amplifyframework.core.Amplify
import java.io.FileNotFoundException

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val test_btn = findViewById<Button>(R.id.testButton)//임시 버튼

        test_btn.setOnClickListener {

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
                        uploadInputStream(it)
                    }
                }
            }
        }
    }

    val REQUEST_STORAGE = 1234

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