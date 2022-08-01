package com.kauel.shippingmark.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.kauel.shippingmark.R
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.databinding.FragmentMainBinding
import com.kauel.shippingmark.ui.fragment_dialog.*
import com.kauel.shippingmark.utils.*
import dagger.hilt.android.AndroidEntryPoint
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.*
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class FragmentMain : Fragment(R.layout.fragment_main) {

    private var binding: FragmentMainBinding? = null
    private val viewModel: FragmentMainViewModel by viewModels()

    private lateinit var customProgressDialog: Dialog

    private lateinit var notificationManager: NotificationManager

    private var idPhone: String? = null
    private var idTransport: String? = null
    private var rutOperator: String? = null

    private val REQUEST_IMAGE_CAPTURE_1 = 1
    private val REQUEST_IMAGE_CAPTURE_2 = 2
    private val REQUEST_IMAGE_CAPTURE_3 = 3
    private val REQUEST_IMAGE_CAPTURE_4 = 4
    private val REQUEST_DIALOGFRAGMENT_1 = 5
    private val REQUEST_DIALOGFRAGMENT_2 = 6
    private val REQUEST_DIALOGFRAGMENT_3 = 7
    private val REQUEST_DIALOGFRAGMENT_4 = 8
    private val REQUEST_FULLSCREEN_1 = 9
    private val REQUEST_FULLSCREEN_2 = 10
    private val REQUEST_FULLSCREEN_3 = 11
    private val REQUEST_FULLSCREEN_4 = 12
    private val REQUEST_SENDDATA = 13

    private var typePallet: String? = null
    private var base: String? = null

    private var state1 = STATE_STANDARD
    private var state2 = STATE_STANDARD
    private var state3 = STATE_STANDARD
    private var state4 = STATE_STANDARD

    private var stateImage1 = false
    private var stateImage2 = false
    private var stateImage3 = false
    private var stateImage4 = false

    private var showImage1: Boolean = true
    private var showImage2: Boolean = true
    private var showImage3: Boolean = true
    private var showImage4: Boolean = true

    private var imageUri1: Uri? = null
    private var imageUri2: Uri? = null
    private var imageUri3: Uri? = null
    private var imageUri4: Uri? = null

    private var fileTemp1: String = ""
    private var fileTemp2: String = ""
    private var fileTemp3: String = ""
    private var fileTemp4: String = ""

    private var listImage: ArrayList<Uri> = ArrayList()
    private val codePermission = 2106

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentMainBinding.bind(view)
        binding = fragmentBinding
        init()
        setUpView()
        initObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        base = sharedPref.getString("BASE", "")
        idTransport = sharedPref.getString("ID_TRANSPORT", "")
        typePallet = sharedPref.getString("TYPE", "")
        rutOperator = sharedPref.getString("RUT_OPERATOR", "")
        binding?.apply {
            tvBase.text = base
            tvId.text = "ID: $idTransport"
            //edtCodeUMP.setText("100000000025123625")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("HardwareIds")
            idPhone =
                Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
        }
        totalCount()
        customProgressDialog = Dialog(requireContext())
        notificationManager =
            requireActivity().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUpView() {
        val dialog = DialogFragmentManualCount()
        val dialogFullScreen = DialogFragmentFullScreen()
        binding?.apply {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ShippingMark")
            img1.setOnClickListener {
                val permission = arrayListOf(
                    Manifest.permission.CAMERA
                )
                if (validatePermission(permission.toTypedArray())) {
                    try {
                        if (showImage1) {
                            val current = LocalDateTime.now()
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                                "$rutOperator-$current")
                            imageUri1 = activity?.contentResolver?.insert(
                                Images.Media.EXTERNAL_CONTENT_URI, values
                            )
                            takePicture(REQUEST_IMAGE_CAPTURE_1, imageUri1!!)
                        } else {
                            if (!dialogFullScreen.isAdded) {
                                dialogFullScreen.setTargetFragment(this@FragmentMain,
                                    REQUEST_FULLSCREEN_1)
                                val args = Bundle()
                                args.putString("image", imageUri1.toString())
                                args.putString("imageTemp", fileTemp1)
                                dialogFullScreen.arguments = args
                                dialogFullScreen.show(this@FragmentMain.requireFragmentManager(),
                                    CUSTOM_DIALOG)
                            }
                        }
                    } catch (ex: Exception) {
                        view?.makeSnackbar(ex.message.toString(), false)
                    }
                } else {
                    solicitedPermission(permission.toTypedArray())
                }
            }
            img2.setOnClickListener {
                val permission = arrayListOf(
                    Manifest.permission.CAMERA
                )
                if (validatePermission(permission.toTypedArray())) {
                    try {
                        if (showImage2) {
                            val current = LocalDateTime.now()
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                                "$rutOperator-$current")
                            imageUri2 = activity?.contentResolver?.insert(
                                Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                            takePicture(REQUEST_IMAGE_CAPTURE_2, imageUri2!!)
                        } else {
                            if (!dialogFullScreen.isAdded) {
                                dialogFullScreen.setTargetFragment(this@FragmentMain,
                                    REQUEST_FULLSCREEN_2)
                                val args = Bundle()
                                args.putString("image", imageUri2.toString())
                                args.putString("imageTemp", fileTemp2)
                                dialogFullScreen.arguments = args
                                dialogFullScreen.show(this@FragmentMain.requireFragmentManager(),
                                    CUSTOM_DIALOG)
                            }
                        }
                    } catch (ex: Exception) {
                        view?.makeSnackbar(ex.message.toString(), false)
                    }
                } else {
                    solicitedPermission(permission.toTypedArray())
                }
            }
            img3.setOnClickListener {
                val permission = arrayListOf(
                    Manifest.permission.CAMERA
                )
                if (validatePermission(permission.toTypedArray())) {
                    try {
                        if (showImage3) {
                            val current = LocalDateTime.now()
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                                "$rutOperator-$current")
                            imageUri3 = activity?.contentResolver?.insert(
                                Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                            takePicture(REQUEST_IMAGE_CAPTURE_3, imageUri3!!)
                        } else {
                            if (!dialogFullScreen.isAdded) {
                                dialogFullScreen.setTargetFragment(this@FragmentMain,
                                    REQUEST_FULLSCREEN_3)
                                val args = Bundle()
                                args.putString("image", imageUri3.toString())
                                args.putString("imageTemp", fileTemp3)
                                dialogFullScreen.arguments = args
                                dialogFullScreen.show(this@FragmentMain.requireFragmentManager(),
                                    CUSTOM_DIALOG)
                            }
                        }
                    } catch (ex: Exception) {
                        view?.makeSnackbar(ex.message.toString(), false)
                    }
                } else {
                    solicitedPermission(permission.toTypedArray())
                }
            }
            img4.setOnClickListener {
                val permission = arrayListOf(
                    Manifest.permission.CAMERA
                )
                if (validatePermission(permission.toTypedArray())) {
                    try {
                        if (showImage4) {
                            val current = LocalDateTime.now()
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                                "$rutOperator-$current")
                            imageUri4 = activity?.contentResolver?.insert(
                                Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                            takePicture(REQUEST_IMAGE_CAPTURE_4, imageUri4!!)
                        } else {
                            if (!dialogFullScreen.isAdded) {
                                dialogFullScreen.setTargetFragment(this@FragmentMain,
                                    REQUEST_FULLSCREEN_4)
                                val args = Bundle()
                                args.putString("image", imageUri4.toString())
                                args.putString("imageTemp", fileTemp4)
                                dialogFullScreen.arguments = args
                                dialogFullScreen.show(this@FragmentMain.requireFragmentManager(),
                                    CUSTOM_DIALOG)
                            }
                        }
                    } catch (ex: Exception) {
                        view?.makeSnackbar(ex.message.toString(), false)
                    }
                } else {
                    solicitedPermission(permission.toTypedArray())
                }
            }
            imgScanner.setOnClickListener {
                val permission = arrayListOf(
                    Manifest.permission.CAMERA
                )
                if (validatePermission(permission.toTypedArray())) {
                    try {
                        val options = ScanOptions()
                        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
                        options.setPrompt("Scan a barcode")
                        options.setCameraId(0) // Use a specific camera of the device
                        options.setBeepEnabled(true)
                        options.setBarcodeImageEnabled(true)
                        barcodeLauncher.launch(options)
                    } catch (ex: Exception) {
                        view?.makeSnackbar(ex.message.toString(), false)
                    }
                } else {
                    solicitedPermission(permission.toTypedArray())
                }
            }
            imbInfo.setOnClickListener {
                showDialogFragmentInfo()
            }
            btnValidate.setOnClickListener {
                if (validateFields() && validateProcessImage()) {
                    sendData()
                }
            }
            tvManual1.setOnClickListener {
                if (stateImage1) {
                    if (!dialog.isAdded) {
                        dialog.setTargetFragment(this@FragmentMain, REQUEST_DIALOGFRAGMENT_1)
                        dialog.show(this@FragmentMain.requireFragmentManager(), CUSTOM_DIALOG)
                    }
                }
            }
            tvManual2.setOnClickListener {
                if (stateImage2) {
                    if (!dialog.isAdded) {
                        dialog.setTargetFragment(this@FragmentMain, REQUEST_DIALOGFRAGMENT_2)
                        dialog.show(this@FragmentMain.requireFragmentManager(), CUSTOM_DIALOG)
                    }
                }
            }
            tvManual3.setOnClickListener {
                if (stateImage3) {
                    if (!dialog.isAdded) {
                        dialog.setTargetFragment(this@FragmentMain, REQUEST_DIALOGFRAGMENT_3)
                        dialog.show(this@FragmentMain.requireFragmentManager(), CUSTOM_DIALOG)
                    }
                }
            }
            tvManual4.setOnClickListener {
                if (stateImage4) {
                    if (!dialog.isAdded) {
                        dialog.setTargetFragment(this@FragmentMain, REQUEST_DIALOGFRAGMENT_4)
                        dialog.show(this@FragmentMain.requireFragmentManager(), CUSTOM_DIALOG)
                    }
                }
            }
        }
    }

    private fun solicitedPermission(permission: Array<String>) {
        requestPermissions(
            permission,
            codePermission + 1
        )
    }

    private fun validatePermission(permission: Array<String>): Boolean {
        return permission.all {
            return ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun sendData() {
        val list = listMultipartImage()
        val current = LocalDateTime.now()
        binding?.apply {
            val numberLabels1 =
                if (tvCountImage1.text == "") 0 else tvCountImage1.text.toString().toInt()
            val numberLabels2 =
                if (tvCountImage2.text == "") 0 else tvCountImage2.text.toString().toInt()
            val numberLabels3 =
                if (tvCountImage3.text == "") 0 else tvCountImage3.text.toString().toInt()
            val numberLabels4 =
                if (tvCountImage4.text == "") 0 else tvCountImage4.text.toString().toInt()
            val numberBox1 =
                if (tvCountBox1.text == "") 0 else tvCountBox1.text.toString().toInt()
            val numberBox2 =
                if (tvCountBox2.text == "") 0 else tvCountBox2.text.toString().toInt()
            val numberBox3 =
                if (tvCountBox3.text == "") 0 else tvCountBox3.text.toString().toInt()
            val numberBox4 =
                if (tvCountBox4.text == "") 0 else tvCountBox4.text.toString().toInt()

            val data = Data(
                id_phone = idPhone.toString(),
                bar_code = edtCodeUMP.text.toString(),
                date = current.toString(),
                id_transporte = idTransport.toString(),
                pallet_type = base.toString(),
                rut_operador = rutOperator.toString(),
                name_image1 = list[0],
                number_labels1 = numberLabels1,
                number_box1 = numberBox1,
                manual1 = state1 == STATE_MANUAL,
                name_image2 = list[1],
                number_labels2 = numberLabels2,
                number_box2 = numberBox2,
                manual2 = state2 == STATE_MANUAL,
                name_image3 = list[2],
                number_labels3 = numberLabels3,
                number_box3 = numberBox3,
                manual3 = state3 == STATE_MANUAL,
                name_image4 = list[3],
                number_labels4 = numberLabels4,
                number_box4 = numberBox4,
                manual4 = state4 == STATE_MANUAL,
            )
            viewModel.sendData(data)
        }
    }

    private fun listMultipartImage(): List<String> {
        val list = ArrayList<String>()
        if (imageUri1 != null) {
            val imagePath = getRealPathFromURI(imageUri1, requireActivity())
            val image1 = imagePath?.let { File(it).name }
            if (image1 != null) {
                list.add(image1)
            }
        }
        if (imageUri2 != null) {
            val imagePath = getRealPathFromURI(imageUri2, requireActivity())
            val image2 = imagePath?.let { File(it).name }
            if (image2 != null) {
                list.add(image2)
            }
        }
        if (imageUri3 != null) {
            val imagePath = getRealPathFromURI(imageUri3, requireActivity())
            val image3 = imagePath?.let { File(it).name }
            if (image3 != null) {
                list.add(image3)
            }
        }
        if (imageUri4 != null) {
            val imagePath = getRealPathFromURI(imageUri4, requireActivity())
            val image4 = imagePath?.let { File(it).name }
            if (image4 != null) {
                list.add(image4)
            }
        }
        return list
    }

    private fun initObserver() {
        viewModel.sendDataLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> showSuccessView(result.data)
                is Resource.Error -> showErrorView(result.error, result.data)
                is Resource.Loading -> showLoadingView()
            }
        }
    }

    private fun showSuccessView(data: ResponseSendData?) {
        customProgressDialog.dismiss()
        if (!customProgressDialog.isShowing) {
            val dialogFragment = DialogFragmentSendData()
            dialogFragment.setTargetFragment(this@FragmentMain, REQUEST_SENDDATA)
            dialogFragment.isCancelable = false
            dialogFragment.show(this@FragmentMain.requireFragmentManager(), CUSTOM_DIALOG_SEND)
        }
    }

    private fun showLoadingView() {
        customProgressDialog.setContentView(R.layout.dialog_custom_loading)
        customProgressDialog.setCancelable(false)
        customProgressDialog.show()
    }

    private fun showErrorView(error: Throwable?, data: ResponseSendData?) {
        customProgressDialog.dismiss()
        view?.makeSnackbar("${error?.message.toString()} ${data?.message.toString()}", false)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            //view?.makeSnackbar("CANCELED", VIEW_ERROR)
        } else {
            if (validateCodeUMP(result.contents)) {
                binding?.edtCodeUMP?.setText(result.contents)
            } else {
                view?.makeSnackbar(ERROR_CODE_UMP, VIEW_ERROR)
            }
        }
    }

    private fun validateCodeUMP(code: String): Boolean {

        if (code.length == 18) {
            val number = code.substring(0, 10)
            if (number != "1000000000") return false
        } else {
            return false
        }

        return true
    }

    private fun showDialogFragmentInfo() {
        val dialog = DialogFragmentInfoBase()
        val args = Bundle()
        when (base + typePallet) {
            BASE_5 + TYPE_1 -> {
                args.putString("base", "Base 5")
            }
            BASE_6 + TYPE_1 -> {
                args.putString("base", "Base 6")
            }
        }
        if (!dialog.isAdded)
            dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, CUSTOM_DIALOG)
    }

    private fun totalCount() {
        var total1 = 0
        var total2 = 0
        var total3 = 0
        var total4 = 0
        when (base + typePallet) {
            BASE_5 + TYPE_1 -> {
                total1 = 18
                total2 = 11
                total3 = 15
                total4 = 11
            }
            BASE_6 + TYPE_1 -> {
                total1 = 57
                total2 = 0
                total3 = 57
                total4 = 0
            }
        }
        binding?.apply {
            tvCountTotalImage1.text = total1.toString()
            tvCountTotalImage2.text = total2.toString()
            tvCountTotalImage3.text = total3.toString()
            tvCountTotalImage4.text = total4.toString()
        }
    }

    private fun takePicture(code: Int, uri: Uri) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(takePictureIntent, code)
        } catch (e: ActivityNotFoundException) {
            view?.makeSnackbar(e.message.toString(), VIEW_ERROR)
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun getCameraPhotoOrientation(
        context: Context,
        imageUri: Uri?,
        imagePath: String?,
    ): Int {
        var rotate = 0
        try {
            context.contentResolver.notifyChange(imageUri!!, null)
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            try {
                binding?.apply {
                    when (requestCode) {
                        REQUEST_IMAGE_CAPTURE_1 -> {
                            try {
                                imageUri1.let { imageUri1 ->
                                    listImage.add(imageUri1!!)
                                    val bitmap =
                                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                                            imageUri1)
                                    val path = getRealPathFromURI(imageUri1, requireActivity())
                                    val orientation = getCameraPhotoOrientation(requireContext(),
                                        imageUri1, path)
                                    val rotated = rotateBitmap(bitmap, orientation.toFloat())
                                    val fileTemp = File(requireContext().cacheDir, "temp1")
                                    processIaImage(
                                        mBitmap = rotated!!,
                                        imageView = img1,
                                        progressBar = pbImage1,
                                        resultView = resultView1,
                                        imageViewResult = ivResultPhoto1,
                                        textView = tvCountImage1,
                                        textViewTotal = tvCountTotalImage1,
                                        relativeLayout = rl1,
                                        numberImage = 1,
                                        textViewBox = tvCountBox1,
                                        file = fileTemp
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Object Detection", "Error execute model", e)
                            }
                        }
                        REQUEST_IMAGE_CAPTURE_2 -> {
                            try {
                                imageUri2?.let { imageUri2 ->
                                    listImage.add(imageUri2)
                                    val bitmap =
                                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                                            imageUri2)
                                    val path = getRealPathFromURI(imageUri2, requireActivity())
                                    val orientation =
                                        getCameraPhotoOrientation(requireContext(),
                                            imageUri2, path)
                                    val rotated = rotateBitmap(bitmap, orientation.toFloat())
                                    val fileTemp = File(requireContext().cacheDir, "temp2")
                                    processIaImage(
                                        mBitmap = rotated!!,
                                        imageView = img2,
                                        progressBar = pbImage2,
                                        resultView = resultView2,
                                        imageViewResult = ivResultPhoto2,
                                        textView = tvCountImage2,
                                        textViewTotal = tvCountTotalImage2,
                                        relativeLayout = rl2,
                                        numberImage = 2,
                                        textViewBox = tvCountBox2,
                                        file = fileTemp
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Object Detection", "Error execute model", e)
                            }
                        }
                        REQUEST_IMAGE_CAPTURE_3 -> {
                            try {
                                imageUri3?.let { imageUri3 ->
                                    listImage.add(imageUri3)
                                    val bitmap =
                                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                                            imageUri3)
                                    val path = getRealPathFromURI(imageUri3, requireActivity())
                                    val orientation =
                                        getCameraPhotoOrientation(requireContext(),
                                            imageUri3, path)
                                    val rotated = rotateBitmap(bitmap, orientation.toFloat())
                                    val fileTemp = File(requireContext().cacheDir, "temp3")
                                    processIaImage(
                                        mBitmap = rotated!!,
                                        imageView = img3,
                                        progressBar = pbImage3,
                                        resultView = resultView3,
                                        imageViewResult = ivResultPhoto3,
                                        textView = tvCountImage3,
                                        textViewTotal = tvCountTotalImage3,
                                        relativeLayout = rl3,
                                        numberImage = 3,
                                        textViewBox = tvCountBox3,
                                        file = fileTemp
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Object Detection", "Error execute model", e)
                            }
                        }
                        REQUEST_IMAGE_CAPTURE_4 -> {
                            try {
                                imageUri4?.let { imageUri4 ->
                                    listImage.add(imageUri4)
                                    val bitmap =
                                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                                            imageUri4)
                                    val path = getRealPathFromURI(imageUri4, requireActivity())
                                    val orientation =
                                        getCameraPhotoOrientation(requireContext(),
                                            imageUri4, path)
                                    val rotated = rotateBitmap(bitmap, orientation.toFloat())
                                    val fileTemp = File(requireContext().cacheDir, "temp4")
                                    processIaImage(
                                        mBitmap = rotated!!,
                                        imageView = img4,
                                        progressBar = pbImage4,
                                        resultView = resultView4,
                                        imageViewResult = ivResultPhoto4,
                                        textView = tvCountImage4,
                                        textViewTotal = tvCountTotalImage4,
                                        relativeLayout = rl4,
                                        numberImage = 4,
                                        textViewBox = tvCountBox4,
                                        file = fileTemp
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Object Detection", "Error execute model", e)
                            }
                        }
                        REQUEST_DIALOGFRAGMENT_1 -> {
                            val value = data?.extras?.getString("value", "0")
                            if (value != null && value.toInt() > 0) {
                                manualData(textView = tvCountImage1,
                                    relativeLayout = rl1,
                                    imageView = ivResultPhoto1,
                                    value = value,
                                    numberImage = 1)
                            } else {
                                view?.makeSnackbar(ERROR_MANUAL_COUNT_2, VIEW_ERROR)
                            }
                        }
                        REQUEST_DIALOGFRAGMENT_2 -> {
                            val value = data?.extras?.getString("value", "0")
                            if (value != null && value.toInt() > 0) {
                                manualData(textView = tvCountImage2,
                                    relativeLayout = rl2,
                                    imageView = ivResultPhoto2,
                                    value = value,
                                    numberImage = 2)
                            } else {
                                view?.makeSnackbar(ERROR_MANUAL_COUNT_2, VIEW_ERROR)
                            }
                        }
                        REQUEST_DIALOGFRAGMENT_3 -> {
                            val value = data?.extras?.getString("value", "0")
                            if (value != null && value.toInt() > 0) {
                                manualData(textView = tvCountImage3,
                                    relativeLayout = rl3,
                                    imageView = ivResultPhoto3,
                                    value = value,
                                    numberImage = 3)
                            } else {
                                view?.makeSnackbar(ERROR_MANUAL_COUNT_2, VIEW_ERROR)
                            }
                        }
                        REQUEST_DIALOGFRAGMENT_4 -> {
                            val value = data?.extras?.getString("value", "0")
                            if (value != null && value.toInt() > 0) {
                                manualData(textView = tvCountImage4,
                                    relativeLayout = rl4,
                                    imageView = ivResultPhoto4,
                                    value = value,
                                    numberImage = 4)
                            } else {
                                view?.makeSnackbar(ERROR_MANUAL_COUNT_2, VIEW_ERROR)
                            }
                        }
                        REQUEST_FULLSCREEN_1 -> {
                            val status = data?.extras?.getBoolean("delete", false)
                            if (status == true) {
                                showImage1 = true
                                stateImage1 = false
                                clearImage(image = img1,
                                    relativeLayout = rl1,
                                    imageView = ivResultPhoto1,
                                    textView = tvCountImage1,
                                    resultView = resultView1)
                            }
                        }
                        REQUEST_FULLSCREEN_2 -> {
                            val status = data?.extras?.getBoolean("delete", false)
                            if (status == true) {
                                showImage2 = true
                                stateImage2 = false
                                clearImage(image = img2,
                                    relativeLayout = rl2,
                                    imageView = ivResultPhoto2,
                                    textView = tvCountImage2,
                                    resultView = resultView2)
                            }
                        }
                        REQUEST_FULLSCREEN_3 -> {
                            val status = data?.extras?.getBoolean("delete", false)
                            if (status == true) {
                                showImage3 = true
                                stateImage3 = false
                                clearImage(image = img3,
                                    relativeLayout = rl3,
                                    imageView = ivResultPhoto3,
                                    textView = tvCountImage3,
                                    resultView = resultView3)
                            }
                        }
                        REQUEST_FULLSCREEN_4 -> {
                            val status = data?.extras?.getBoolean("delete", false)
                            if (status == true) {
                                showImage4 = true
                                stateImage4 = false
                                clearImage(image = img4,
                                    relativeLayout = rl4,
                                    imageView = ivResultPhoto4,
                                    textView = tvCountImage4,
                                    resultView = resultView4)
                            }
                        }
                        REQUEST_SENDDATA -> {
                            val status = data?.extras?.getBoolean("new", false)
                            if (status == true) {
                                clearAll()
                            }
                        }
                        else -> {}//view?.makeSnackbar("CANCELED", VIEW_ERROR)
                    }
                }
            } catch (e: Exception) {
                Log.e("Activity Result", "Error execute any action", e)
            }
        }
    }

    private fun processIaImage(
        mBitmap: Bitmap,
        imageView: ImageView,
        progressBar: ProgressBar,
        resultView: ResultView,
        imageViewResult: ImageView,
        textView: TextView,
        textViewTotal: TextView,
        relativeLayout: RelativeLayout,
        numberImage: Int,
        textViewBox: TextView,
        file: File,
    ) {

        var mModule: Module? = null

        try {
            mModule = LiteModuleLoader.load(assetFilePath(requireContext(), ASSETS_NAME))
            val br =
                BufferedReader(InputStreamReader(requireActivity().assets.open(FILE_NAME_ASSETS)))
            var classes: MutableList<String> = ArrayList()
            br.readLines().map { line ->
                classes.add(line)
            }
            PrePostProcessor.mClasses = classes.toTypedArray()
            classes = PrePostProcessor.mClasses.toMutableList()
        } catch (e: IOException) {
            Log.e("Object Detection", "Error reading assets", e)
        }

        relativeLayout.setBackgroundResource(R.drawable.img_standard)
        imageViewResult.gone()
        resultView.gone()
        imageView.setImageBitmap(mBitmap)
        progressBar.visible()

        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        val canvas = Canvas(mBitmap)

        val mImgScaleX = mBitmap.width.toFloat() / PrePostProcessor.mInputWidth
        val mImgScaleY = mBitmap.height.toFloat() / PrePostProcessor.mInputHeight

        val mIvScaleX =
            if (mBitmap.width > mBitmap.height) canvas.width.toFloat() / mBitmap.width else canvas.height.toFloat() / mBitmap.height
        val mIvScaleY =
            if (mBitmap.height > mBitmap.width) canvas.height.toFloat() / mBitmap.height else canvas.width.toFloat() / mBitmap.width

        val mStartX = (canvas.width - mIvScaleX * mBitmap.width) / 2
        val mStartY = (canvas.height - mIvScaleY * mBitmap.height) / 2

        val timer: Thread = object : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                val resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                    PrePostProcessor.mInputWidth,
                    PrePostProcessor.mInputHeight,
                    true)
                val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap,
                    PrePostProcessor.NO_MEAN_RGB,
                    PrePostProcessor.NO_STD_RGB)
                val outputTuple = mModule!!.forward(IValue.from(inputTensor)).toTuple()
                val outputTensor = outputTuple[0].toTensor()
                val outputs = outputTensor.dataAsFloatArray
                val results = PrePostProcessor.outputsToNMSPredictions(outputs,
                    mImgScaleX,
                    mImgScaleY,
                    mIvScaleX,
                    mIvScaleY,
                    mStartX,
                    mStartY)

                val resultsLabel = ArrayList<Result>()
                val resultsBox = ArrayList<Result>()
                for (res in results) {
                    if (res.classIndex == 2) {
                        resultsLabel.add(res)
                    } else {
                        resultsBox.add(res)
                    }
                }

                requireActivity().runOnUiThread(Runnable {
                    val total = textViewTotal.text.toString().toInt()
                    val mPaintRectangle = Paint()

                    textViewBox.text = resultsBox.size.toString()
                    textView.text = resultsLabel.size.toString()
                    for (result in results) {
                        mPaintRectangle.strokeWidth = 40f
                        mPaintRectangle.style = Paint.Style.STROKE
                        when (result.classIndex) {
                            1 -> {
                                mPaintRectangle.color = Color.GREEN
                            }
                            2 -> {
                                mPaintRectangle.color = Color.RED
                            }
                            3 -> {
                                mPaintRectangle.color = Color.YELLOW
                            }
                        }
                        canvas.drawRect(result.rect, mPaintRectangle)
                    }
                    val file = bitmapToFile(mBitmap, file.path)
                    progressBar.gone()
                    var state = false
                    if (resultsLabel.size == total) {
                        relativeLayout.setBackgroundResource(R.drawable.img_success)
                        imageViewResult.setImageResource(R.drawable.success)
                        state = true
                    } else {
                        relativeLayout.setBackgroundResource(R.drawable.img_error)
                        imageViewResult.setImageResource(R.drawable.error)
                    }
                    when (numberImage) {
                        1 -> {
                            state1 = if (state) {
                                STATE_SUCCESS
                            } else {
                                STATE_ERROR
                            }
                            if (file != null) {
                                fileTemp1 = file.path
                            }
                            showImage1 = false
                            stateImage1 = true
                        }
                        2 -> {
                            state2 = if (state) {
                                STATE_SUCCESS
                            } else {
                                STATE_ERROR
                            }
                            if (file != null) {
                                fileTemp2 = file.path
                            }
                            showImage2 = false
                            stateImage2 = true
                        }
                        3 -> {
                            state3 = if (state) {
                                STATE_SUCCESS
                            } else {
                                STATE_ERROR
                            }
                            if (file != null) {
                                fileTemp3 = file.path
                            }
                            showImage3 = false
                            stateImage3 = true
                        }
                        4 -> {
                            state4 = if (state) {
                                STATE_SUCCESS
                            } else {
                                STATE_ERROR
                            }
                            if (file != null) {
                                fileTemp4 = file.path
                            }
                            showImage4 = false
                            stateImage4 = true
                        }
                    }
                    imageViewResult.visible()

                })
            }
        }
        timer.start()
    }

    private fun bitmapToFile(
        bitmap: Bitmap,
        path: String,
    ): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(path)
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    @Throws(IOException::class)
    private fun assetFilePath(context: Context, assetName: String?): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName!!).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }

    @Throws(IOException::class)
    private fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap? {
        val ei = ExifInterface(image_absolute_path!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, true)
            else -> bitmap
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap? {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1.toFloat()) as Float,
            (if (vertical) -1 else 1.toFloat()) as Float)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun manualData(
        textView: TextView,
        relativeLayout: RelativeLayout,
        imageView: ImageView,
        value: String,
        numberImage: Int,
    ) {
        textView.text = value
        relativeLayout.setBackgroundResource(R.drawable.img_manual)
        imageView.gone()
        when (numberImage) {
            1 -> state1 = STATE_MANUAL
            2 -> state2 = STATE_MANUAL
            3 -> state3 = STATE_MANUAL
            4 -> state4 = STATE_MANUAL
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap, title: String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            Images.Media.insertImage(inContext.contentResolver, inImage, title, null)
        return Uri.parse(path)
    }

//    private fun getRealPathFromURI(uri: Uri?): String? {
//        val cursor: Cursor? =
//            uri?.let { activity?.contentResolver?.query(it, null, null, null, null) }
//        cursor?.moveToFirst()
//        val idx: Int? = cursor?.getColumnIndex(Images.ImageColumns.DATA)
//        return idx?.let { cursor.getString(it) }
//    }

    private fun validateFields(): Boolean {
        binding?.apply {
            if (edtCodeUMP.text.trim().isEmpty()) {
                view?.makeSnackbar(ERROR_CODE_UMP, VIEW_ERROR)
                return false
            }
        }
        return true
    }

    private fun validateProcessImage(): Boolean {
        if (state1 == STATE_SUCCESS || state1 == STATE_MANUAL &&
            state2 == STATE_SUCCESS || state2 == STATE_MANUAL &&
            state3 == STATE_SUCCESS || state3 == STATE_MANUAL &&
            state4 == STATE_SUCCESS || state4 == STATE_MANUAL
        ) {
            return true
        }
        view?.makeSnackbar(ERROR_PENDING_IMAGE, VIEW_ERROR)
        return false
    }

    private fun clearImage(
        image: ImageView,
        relativeLayout: RelativeLayout,
        imageView: ImageView,
        textView: TextView,
        resultView: ResultView,
    ) {
        image.setImageResource(R.drawable.camera)
        relativeLayout.setBackgroundResource(R.drawable.img_standard)
        imageView.gone()
        textView.text = "0"
        resultView.gone()
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

    private fun clearAll() {
        binding?.apply {
            edtCodeUMP.setText("")
            img1.setImageResource(R.drawable.camera)
            img2.setImageResource(R.drawable.camera)
            img3.setImageResource(R.drawable.camera)
            img4.setImageResource(R.drawable.camera)
            rl1.setBackgroundResource(R.drawable.img_standard)
            rl2.setBackgroundResource(R.drawable.img_standard)
            rl3.setBackgroundResource(R.drawable.img_standard)
            rl4.setBackgroundResource(R.drawable.img_standard)
            ivResultPhoto1.gone()
            ivResultPhoto2.gone()
            ivResultPhoto3.gone()
            ivResultPhoto4.gone()
            tvCountImage1.text = "0"
            tvCountImage2.text = "0"
            tvCountImage3.text = "0"
            tvCountImage4.text = "0"
            showImage1 = true
            showImage2 = true
            showImage3 = true
            showImage4 = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}