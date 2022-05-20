package com.example.x_travel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.ui.AppBarConfiguration
import com.example.x_travel.databinding.ActivityConfirmBinding
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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

            //위도, 경도
            var path = createCopyAndReturnRealPath(photoUri!!)
            val exif = ExifInterface(path!!)
            binding.latitude.text = exif.latLong?.get(0).toString()
            binding.longitude.text = exif.latLong?.get(1).toString()

        } catch (e:Exception) { e.printStackTrace() }

        try {
            var Imagedata: Uri? = getIntent().getParcelableExtra("album")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Imagedata)
            binding.confirm.setImageBitmap(bitmap)
            Toast.makeText(this, Imagedata?.path, Toast.LENGTH_LONG).show()

            //위도, 경도
            var path = createCopyAndReturnRealPath(Imagedata!!)
            val exif = ExifInterface(path!!)
            binding.latitude.text = exif.latLong?.get(0).toString()
            binding.longitude.text = exif.latLong?.get(1).toString()
        } catch (e:Exception) { e.printStackTrace() }

    }

    fun createCopyAndReturnRealPath(uri: Uri) :String? {
        val context = applicationContext
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            /*  절대 경로를 getGps()에 넘겨주기   */
//            getGps(file.getAbsolutePath())
        }
            return file.getAbsolutePath()
    }


}