package com.example.x_travel

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.core.Amplify
import com.example.x_travel.databinding.ActivityConfirmBinding



private lateinit var binding: ActivityConfirmBinding
private lateinit var auth: AWSMobileClient

class ConfirmActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var photoUri : Uri? = null
        lateinit var filename_photo : String

        var Imagedata : Uri? = null
        lateinit var filename_album : String

        auth = AWSMobileClient.getInstance()
        val sub = auth.userSub

        try {
            photoUri= getIntent().getParcelableExtra("photo")
            val imageBitmap = photoUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
            binding.confirm.setImageBitmap(imageBitmap?.let { ImageDecoder.decodeBitmap(it) })
            filename_photo = sub + "/Imagedata/"+ photoUri?.lastPathSegment

//            Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            Imagedata = getIntent().getParcelableExtra("album")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Imagedata)
            binding.confirm.setImageBitmap(bitmap)
            filename_album = sub + "/Imagedata/"+ Imagedata?.lastPathSegment + ".jpg"


            //경로
//            Toast.makeText(this, Imagedata?.path, Toast.LENGTH_LONG).show()

            //위도, 경도
//            var path = createCopyAndReturnRealPath(Imagedata!!)
//            val exif = ExifInterface(path!!)
//            binding.latitude.text = exif.latLong?.get(0).toString()
//            binding.longitude.text = exif.latLong?.get(1).toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.downloadButton.setOnClickListener {
            if (photoUri != null) {
                uploadFile(filename_photo, photoUri)
            }
            if (Imagedata != null) {
                uploadFile(filename_album, Imagedata)
            }
        }

    }

    ////////////////// uploading data to s3
    private fun uploadFile(filename: String, Imagedata: Uri) {
        val inputStream = Imagedata?.let { contentResolver.openInputStream(it) }
        if (inputStream != null) {
            if (filename != null) {
                Amplify.Storage.uploadInputStream(
                    filename,
                    inputStream,
                    { result ->
                        Toast.makeText(
                            this,
                            "file has successfully uploaded",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    { error -> Log.e("myapplicationapp", "upload fail", error) }
                )
            }
        }
        ////////////////////////////////////////
    }
}




