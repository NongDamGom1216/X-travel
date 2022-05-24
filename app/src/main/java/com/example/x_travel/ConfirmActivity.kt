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
import com.amplifyframework.core.Amplify
import com.example.x_travel.databinding.ActivityConfirmBinding



private lateinit var binding: ActivityConfirmBinding

class ConfirmActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            var photoUri: Uri? = getIntent().getParcelableExtra("photo")
            val imageBitmap = photoUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
            binding.confirm.setImageBitmap(imageBitmap?.let { ImageDecoder.decodeBitmap(it) })
            val filename = "photodata/"+ photoUri?.lastPathSegment
            if (photoUri != null) {
                uploadFile(filename, photoUri)
            }
            Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            var Imagedata: Uri? = getIntent().getParcelableExtra("album")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Imagedata)
            binding.confirm.setImageBitmap(bitmap)
            val filename = "Imagedata/"+ Imagedata?.lastPathSegment + ".jpg"
            if (Imagedata != null) {
                uploadFile(filename, Imagedata)
            }


            Toast.makeText(this, Imagedata?.path, Toast.LENGTH_LONG).show()

            //위도, 경도
//            var path = createCopyAndReturnRealPath(Imagedata!!)
//            val exif = ExifInterface(path!!)
//            binding.latitude.text = exif.latLong?.get(0).toString()
//            binding.longitude.text = exif.latLong?.get(1).toString()

        } catch (e: Exception) {
            e.printStackTrace()
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




