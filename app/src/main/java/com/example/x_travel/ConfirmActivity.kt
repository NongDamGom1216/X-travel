package com.example.x_travel

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.ui.AppBarConfiguration
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
            Toast.makeText(this, photoUri?.path, Toast.LENGTH_LONG).show()
        } catch (e:Exception) { e.printStackTrace() }

        try {
            var Imagedata: Uri? = getIntent().getParcelableExtra("album")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Imagedata)
            binding.confirm.setImageBitmap(bitmap)
            Toast.makeText(this, Imagedata?.path, Toast.LENGTH_LONG).show()
        } catch (e:Exception) { e.printStackTrace() }
    }


}