package com.kauel.shippingmark.activity

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.kauel.shippingmark.R
import com.kauel.shippingmark.utils.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ShippingMark)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                isReadPermissionGranted =
                    it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
                isWritePermissionGranted =
                    it[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
//                isLocationPermissionGranted =
//                    it[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
//                isCameraPermissionGranted =
//                    it[Manifest.permission.CAMERA] ?: isCameraPermissionGranted

            }

        requestPermission()
    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        isCameraPermissionGranted = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isReadPermissionGranted) permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!isWritePermissionGranted) permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (!isLocationPermissionGranted) permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        if (!isCameraPermissionGranted) permissionRequest.add(Manifest.permission.CAMERA)

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

}