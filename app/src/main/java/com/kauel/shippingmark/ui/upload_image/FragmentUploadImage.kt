package com.kauel.shippingmark.ui.upload_image

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kauel.shippingmark.R
import com.kauel.shippingmark.activity.MainActivity
import com.kauel.shippingmark.api.uploadImage.FileName
import com.kauel.shippingmark.api.uploadImage.ResponseFile
import com.kauel.shippingmark.api.uploadImage.UriUpload
import com.kauel.shippingmark.databinding.FragmentUploadImageBinding
import com.kauel.shippingmark.utils.*
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.Sentry
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class FragmentUploadImage : Fragment(R.layout.fragment_upload_image) {

    private var binding: FragmentUploadImageBinding? = null
    private val viewModel: FragmentUploadImageViewModel by viewModels()

    private lateinit var notificationManager: NotificationManager
    private var mBuilder: NotificationCompat.Builder? = null

    private var listFile: ArrayList<UriUpload> = ArrayList()
    private var listFileUpload: ArrayList<FileName> = ArrayList()
    private var listNameImagesSuccess: ArrayList<String> = ArrayList()
    private var play = false
    private var stop = false

    private val codeUpload = 1
    private val codeStop = 3
    private val codeFinish = 4
    private val codeError = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentUploadImageBinding.bind(view)
        binding = fragmentBinding
        init()
        setUpView()
        initObserver()
    }

    private fun setUpView() {
        binding?.apply {
            btnPlay.setOnClickListener {
                if (!play) {
                    play = true
                    stop = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel()
                    }
                    uploadImage()
                }
            }
            btnStop.setOnClickListener {
                if (!stop) {
                    stop = true
                    viewModel.stopUploadImage()
                }
            }
            btnBackMenu.setOnClickListener {
                if (!play) {
                    findNavController().navigate(R.id.action_fragmentUploadImage_to_fragmentMenuPallet)
                } else {
                    view?.makeSnackbar(NOT_BACK, VIEW_ERROR)
                }
            }
        }
    }

    private fun init() {
        notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun initObserver() {
        viewModel.uploadImageLiveData.observeForever { result ->
            when (result) {
                is Resource.Success -> showSuccessView(result.data)
                is Resource.Error -> showErrorView(result.error, result.data)
                is Resource.Loading -> showLoadingView(result.data)
            }
        }
        viewModel.liveData.observe(viewLifecycleOwner) { list->
            if (list.isNotEmpty()) {
                list.map {
                    if (it.status) {
                        listNameImagesSuccess.add(it.name)
                    }
                }
            }
        }
    }

    private fun showSuccessView(data: List<ResponseFile>?) {
        binding?.apply {
            tvNumUpload.gone()
            tvCountImage.gone()
            tvSlash.gone()
            progressBar.gone()
            if (stop) {
                showProgressUpload(codeStop)
                tvNameImage.text = MESSAGE_UPLOAD_IMAGE_STOP
            } else {
                showProgressUpload(codeFinish)
                tvNameImage.text = MESSAGE_UPLOAD_IMAGE_SUCCESS
            }
            play = false
        }
    }

    private fun showLoadingView(data: List<ResponseFile>?) {
        binding?.apply {
            val num = data?.size.toString()
            if (num != "null") {
                showProgressUpload(codeUpload, num)
                tvNameImage.text = data?.last()?.name
                tvNumUpload.text = num
                tvSlash.visible()
                tvCountImage.text = listFileUpload.size.toString()
                tvNumUpload.visible()
                tvCountImage.visible()
            } else {
                tvNameImage.text = MESSAGE_UPLOADING_IMAGE
                tvNumUpload.gone()
                tvCountImage.gone()
                tvSlash.gone()
            }
            lyInfoUpload.visible()
        }
    }

    private fun showErrorView(error: Throwable?, data: List<ResponseFile>?) {
        binding?.apply {
            lyInfoUpload.gone()
        }
        showProgressUpload(codeError)
        play = false
        val message = error?.message.toString()
        if (error != null) {
            Sentry.captureException(error)
        }
        view?.makeSnackbar(message, VIEW_ERROR)
    }

    @SuppressLint("NewApi")
    private fun uploadImage() {
        try {
            listFile()
            if (listFile.size > 0) {
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                val rut = sharedPref.getString("RUT_OPERATOR", "")
                listFileUpload = ArrayList()
                listFile.map { listFile ->
                    var status = true

                    listNameImagesSuccess.forEach { nameImage ->
                        if (listFile.name == nameImage) {
                            status = false
                            return@forEach
                        }
                    }

                    if (status) {
                        val imagePath = getRealPathFromURI(listFile.uri)
                        val name =
                            listFile.name.toRequestBody("text/plain".toMediaTypeOrNull())
                        listFileUpload.add(FileName(File(imagePath!!), listFile.uri, name))
                    }
                }
                viewModel.uploadImage(
                    rut.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    listFileUpload,
                )

            } else {
                binding?.apply {
                    tvNameImage.text = MESSAGE_UPLOAD_IMAGE_EMPTY
                    tvNumUpload.gone()
                    tvCountImage.gone()
                    tvSlash.gone()
                    play = false
                }
            }
        } catch (e: Exception) {
            view?.makeSnackbar(e.message.toString(), VIEW_ERROR)
        }
    }

    private fun showProgressUpload(status: Int, numImage: String? = "") {
        val max = 0
        val progress = 0
        val indeterminate = false
        when (status) {
            //Uploading
            1 -> {
                mBuilder!!.setContentText("Subidas/restantes: $numImage / ${listFile.size}")
                notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
            }
            2 -> {
                mBuilder!!.setContentText(NOTIFICATION_UPLOAD_PAUSE)
                    .setProgress(max, progress, indeterminate)
                    .setOngoing(false)
                notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
            }
            //STOP
            3 -> {
                mBuilder!!.setContentText(NOTIFICATION_UPLOAD_STOP)
                    .setProgress(max, progress, indeterminate)
                    .setOngoing(false)
                notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
            }
            //FINISH
            4 -> {
                mBuilder!!.setContentText(NOTIFICATION_UPLOAD_FINISHED)
                    .setProgress(max, progress, indeterminate)
                    .setOngoing(false)
                notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
            }
            //ERROR
            5 -> {
                mBuilder!!.setContentText(NOTIFICATION_ERROR)
                    .setProgress(max, progress, indeterminate)
                    .setOngoing(false)
                notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
        getNotificationBuilder()
    }

    private fun getNotificationBuilder() {
        mBuilder = context?.let {
            NotificationCompat.Builder(it, NOTIFICATION_CHANNEL_ID)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentIntent(getActivityPendingIntent())
                .setProgress(100, 100, true)
        }
    }

    private fun getActivityPendingIntent() =
        PendingIntent.getActivity(
            context,
            143,
            Intent(context, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun getRealPathFromURI(uri: Uri?): String? {
        val cursor: Cursor? =
            uri?.let { activity?.contentResolver?.query(it, null, null, null, null) }
        cursor?.moveToFirst()
        val idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return idx?.let { cursor.getString(it) }
    }

    private fun requestDeletePermission(uriList: List<Uri>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createDeleteRequest(requireActivity().contentResolver, uriList)
            try {
                startIntentSenderForResult(
                    pi.intentSender, 1, null, 0, 0,
                    0, null
                )
            } catch (e: IntentSender.SendIntentException) {
            }
        } else {
            uriList.map {
                requireActivity().contentResolver.delete(it, null, null)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun listFile() {

        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projections = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            @Suppress("DEPRECATION")
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.TITLE,
            // Deprecated on Android Q - actual file path is no longer used, access file via ID instead
            // MediaStore.Images.ImageColumns.DATA,
            // Complaint of Field Require API Level Q (current min is 17), but still works
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            // Complaint of Field Require API Level Q (current min is 17), but still works
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.RELATIVE_PATH,
            // Complaint of Field Require API Level Q (current min is 17), but still works
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns.SIZE,
            @Suppress("DEPRECATION")
            MediaStore.Images.ImageColumns.LATITUDE, // return value on device below Android Q
            @Suppress("DEPRECATION")
            MediaStore.Images.ImageColumns.LONGITUDE // return value on device below Android Q
        )
        val selection = "${MediaStore.Images.ImageColumns.RELATIVE_PATH} like ?"
        val selectionArgs = arrayOf(
            "Pictures/ShippingMark/"
        )

        val orderBy = "${MediaStore.Images.ImageColumns.TITLE} ASC"

        activity?.contentResolver?.query(contentUri, projections, selection, selectionArgs, orderBy)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    // index
                    val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                    val displayName =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)

                    do {
                        val id = cursor.getLong(idIndex)
                        val filename = cursor.getString(displayName)

                        // uri to access file
                        var uri =
                            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // https://developer.android.com/reference/kotlin/android/provider/MediaStore#setrequireoriginal
                            uri = MediaStore.setRequireOriginal(uri)

                            listFile.add(UriUpload(uri = uri, name = filename.replace(" ", "")))
                        }

                    } while (cursor.moveToNext())
                }
            }

    }

    override fun onDestroyView() {
        binding = null
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroyView()
    }
}