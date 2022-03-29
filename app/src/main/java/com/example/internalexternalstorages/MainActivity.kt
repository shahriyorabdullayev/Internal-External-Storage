package com.example.internalexternalstorages

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.internalexternalstorages.databinding.ActivityMainBinding
import java.io.*
import java.nio.charset.Charset
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isInternal = true
    private var isPersistent = false
    var readPermissionGranted = false
    var writePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        requestPermisssons()
        checkStoragePaths()

    }

    // Initialize View
    private fun initViews() {
        //Internal Storage
        binding.apply {
            btnCreateInternal.setOnClickListener {
                createInternalFile()
            }
            btnSaveInternal.setOnClickListener {
                saveInternalFile("Abdullayev Shahriyor")
            }
            btnReadInternal.setOnClickListener {
                readInternalFile()
            }
            btnDeleteInternal.setOnClickListener {
                deleteInternalFile()
            }
        }


        binding.apply {
            //External Storage
            btnCreateExternal.setOnClickListener {
                createExternalFile()
            }
            btnSaveExternal.setOnClickListener {
                saveExternalFile("Abdullayev Diyorbek")
            }
            btnReadExternal.setOnClickListener {
                readExternalFile()
            }
            btnDeleteExternal.setOnClickListener {
                deleteExternalFile()
            }
        }

        binding.btnSavePhotoInternal.setOnClickListener {
            takePhoto.launch()
        }

        binding.btnSavePhotoExternal.setOnClickListener {

        }

        binding.btnOpenImages.setOnClickListener {
            startActivity(Intent(this, ImagesActivity::class.java))
        }

        binding.btnOpenSettings.setOnClickListener {
            openSettings()
        }

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }


    //Check Storage Path
    private fun checkStoragePaths() {
        val internal_m1 = getDir("custom", 0)
        val internal_m2 = filesDir

        val external_m1 = getExternalFilesDir(null)
        val external_m2 = externalCacheDir
        val external_m3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d("MainActivity", internal_m1.absolutePath)
        Log.d("MainActivity", internal_m2.absolutePath)
        Log.d("MainActivity", external_m1!!.absolutePath)
        Log.d("MainActivity", external_m2!!.absolutePath)
        Log.d("MainActivity", external_m3!!.absolutePath)
    }


    //Permission============================================================================================================
    private fun requestPermisssons() {
        val hasReadPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissonsToRequest = mutableListOf<String>()
        if (!readPermissionGranted)
            permissonsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!writePermissionGranted)
            permissonsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissonsToRequest.isNotEmpty())
            permissionLauncher.launch(permissonsToRequest.toTypedArray())
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
        writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

        if (readPermissionGranted) Toast.makeText(this, "READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
        if (writePermissionGranted) Toast.makeText(this, "WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()

    }

    //Internal Storage===============================
    private fun createInternalFile() {
        val fileName = "pdp_internal.txt"
        val file: File

        file = if (isPersistent) {
            File(filesDir, fileName)
        }else {
            File(cacheDir, fileName)
        }

        if (!file.exists()){
            try {
                file.createNewFile()
                Toast.makeText(this, String.format("File %s has been created internal file", fileName), Toast.LENGTH_SHORT).show()
            }catch (e: Exception) {
                Toast.makeText(this, String.format("File %s creation failed internal file", fileName), Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(this, String.format("File %s already exists internal file", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        try {
            val fileOutputStream : FileOutputStream
            fileOutputStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            }else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, String.format("Wtite to %s successfull internal file", fileName), Toast.LENGTH_SHORT).show()
        }catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, String.format("Wtite to file %s failed internal file", fileName), Toast.LENGTH_SHORT).show()

        }
    }

    private fun readInternalFile() {
        val fileName = "pdp_internal.txt"
        try {
            val fileInputStream: FileInputStream
            fileInputStream = if (isPersistent) {
                openFileInput(fileName)
            }else{
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Toast.makeText(this, readText, Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", readText)
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, String.format("Read to file %s failed internal file", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteInternalFile() {
        val fileName = "pdp_internal.txt"
        val file: File
        file = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (file.exists()) {
            file.delete()
            Toast.makeText(this, String.format("File %s has been deleted internal file", fileName), Toast.LENGTH_SHORT).show()
            
        } else {
            Toast.makeText(this, "File %s doesn't exist internal file", Toast.LENGTH_SHORT).show()
        }
    }

    //External Storage===============================

    private fun createExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        Log.d("@@@", "absolutePath: " + file.absolutePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this, String.format("File %s has been created external file", fileName), Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                Toast.makeText(this, String.format("File %s creation failed external file", fileName), Toast.LENGTH_SHORT).show()

            }
        } else {
            Toast.makeText(this, String.format("File %s already exists external file", fileName), Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveExternalFile(data: String) {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, String.format("Wtite to %s successfull external file", fileName), Toast.LENGTH_SHORT).show()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, String.format("Wtite to file %s failed external file", fileName), Toast.LENGTH_SHORT).show()

        }
    }

    private fun readExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent)
            File(getExternalFilesDir(null), fileName)
        else
            File(externalCacheDir, fileName)

        Log.d("@@@",file.absolutePath)

        try {
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = java.util.ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Log.d("StorageActivity", readText)
            Toast.makeText(this, readText, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, String.format("Read to file %s failed external file", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        if (file.exists()) {
            file.delete()
            Toast.makeText(this,
                String.format("File %s has been deleted external file", fileName),
                Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this,
                String.format("File %s has been deleted external file", fileName),
                Toast.LENGTH_SHORT).show()

        }
    }


    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }


    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){bitmap ->
        val fileName = UUID.randomUUID().toString()

        val isPhotoSaved = if (isInternal) {
            savePhotoInternalStorage(fileName, bitmap!!)
        }else {
            if (writePermissionGranted) {
                savePhotoExternalStorage(fileName, bitmap!!)
            }else {
                false
            }
        }

    }

    private fun savePhotoExternalStorage(fileName: String, bmp: Bitmap): Boolean {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }
        return try {
            contentResolver.insert(collection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use {  outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream))
                        throw IOException("Couldn't save bitmap")
                }
            }
            true
        }catch (e: IOException){
            e.printStackTrace()
            false
        }

    }

    private fun savePhotoInternalStorage(fileName: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$fileName.jpg", MODE_PRIVATE).use {stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)){
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        }catch (e : IOException) {
            e.printStackTrace()
            false
        }

    }

    fun loadPhotosFromInternalStorage(): List<Bitmap> {
        val files = filesDir.listFiles()
        return files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
            val bytes = it.readBytes()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bmp
        } ?: listOf()
    }


    fun loadPhotosFromExternalStorage(): List<Uri> {

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )
        val photos = mutableListOf<Uri>()
        return contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos.add(contentUri)
            }
            photos.toList()
        } ?: listOf()
    }


}