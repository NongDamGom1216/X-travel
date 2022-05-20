package com.example.x_travel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.ui.AppBarConfiguration
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.example.x_travel.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.views.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    private var selectedIndex: Int = 0;
    val PERMISSIONS_REQUEST = 100

    // Request Code
    private val CAMERA = 100
    private val GALLERY = 200
    private val PROFILE = 300


    private var photoUri: Uri? = null

    // Permisisons
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)

        val motionLayout = findViewById<MotionLayout>(R.id.motion_container)

        val v1 = findViewById<View>(R.id.v1)
        val v2 = findViewById<View>(R.id.v2)
        val v3 = findViewById<View>(R.id.v3)


        // 로그인이 되어있는지 확인
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())

                    // 로그인이 안되어있으면 AuthActivity 로 이동
                    if (userStateDetails.userState != UserState.SIGNED_IN) {
                        val i = Intent(this@MainActivity, AuthActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })


        v1.setOnClickListener {
            if (selectedIndex == 0) {

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoFile = File(
                    File("${filesDir}/image").apply{
                        if(!this.exists()){
                            this.mkdirs()
                        }
                    },
                    newJpgFileName()
                )
                photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.x_travel.fileprovider",
                    photoFile
                )
                takePictureIntent.resolveActivity(packageManager)?.also{
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CAMERA)
                }

            }
            if (selectedIndex == 1) {
                motionLayout.setTransition(R.id.s2, R.id.s1) //orange to blue transition
                motionLayout.transitionToEnd()
            }
            selectedIndex = 0;
        }
        v2.setOnClickListener {
            if (selectedIndex == 1) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent,GALLERY)
            }

            if (selectedIndex == 2) {
                motionLayout.setTransition(R.id.s3, R.id.s2)  //red to orange transition
            }
            if (selectedIndex == 0){
                motionLayout.setTransition(R.id.s1, R.id.s2) //blue to orange transition
            }
            motionLayout.transitionToEnd()
            selectedIndex = 1;
        }
        v3.setOnClickListener {
            if (selectedIndex == 2) return@setOnClickListener

            motionLayout.setTransition(R.id.s2, R.id.s3) //orange to red transition
            motionLayout.transitionToEnd()
            selectedIndex = 2;
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA -> {
                    val i = Intent(this@MainActivity, ConfirmActivity::class.java)
                    i.putExtra("photo", photoUri)
                    startActivity(i)
                }
                GALLERY -> {
                    var Imagedata: Uri? = data?.data
                    try {
                        val i = Intent(this@MainActivity, ConfirmActivity::class.java)
                        i.putExtra("album", Imagedata)
                        startActivity(i)
                    } catch (e:Exception) { e.printStackTrace() }

                }
            }
        }
    }

    private fun newJpgFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.jpg"
    }
    private fun saveBitmapAsJPGFile(bitmap: Bitmap) {
        val path = File(filesDir, "image")
        if(!path.exists()){
            path.mkdirs()
        }
        val file = File(path, newJpgFileName())
        var imageFile: OutputStream? = null
        try{
            file.createNewFile()
            imageFile = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageFile)
            imageFile.close()
            Toast.makeText(this, file.absolutePath, Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            null
        }
    }

    private fun checkPermissions(permissions: Array<String>, permissionsRequest: Int): Boolean {
        val permissionList: MutableList<String> = mutableListOf()
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(), PERMISSIONS_REQUEST)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 승인 부탁드립니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.signOut_button -> {
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
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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