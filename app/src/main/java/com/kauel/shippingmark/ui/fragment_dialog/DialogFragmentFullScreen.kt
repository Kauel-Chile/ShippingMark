package com.kauel.shippingmark.ui.fragment_dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.DialogFragmentFullscreenBinding
import com.kauel.shippingmark.ui.main.PrePostProcessor
import com.kauel.shippingmark.ui.main.ResultView
import com.kauel.shippingmark.utils.ASSETS_NAME
import com.kauel.shippingmark.utils.FILE_NAME_ASSETS
import com.kauel.shippingmark.utils.gone
import com.kauel.shippingmark.utils.visible
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.*

class DialogFragmentFullScreen : DialogFragment(R.layout.dialog_fragment_fullscreen) {

    private var binding: DialogFragmentFullscreenBinding? = null

    private var imageUri: Uri? = null
    private lateinit var intentSenderRequest: ActivityResultLauncher<IntentSenderRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = DialogFragmentFullscreenBinding.bind(view)
        binding = fragmentBinding
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arguments?.let { it ->
            imageUri = it.getString("image")?.toUri()
            binding?.imgFullScreen?.setImageURI(imageUri)
        }
        init()
        setUpView()
    }

    private fun init() {
        intentSenderRequest =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val i: Intent = Intent().putExtra("delete", true)
                    targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, i);
                    dismiss()
                }
            }
    }

    private fun setUpView() {
        binding?.apply {
            ivCloseDialog.setOnClickListener {
                dismiss()
            }
            ivDelete.setOnClickListener {
                imageUri?.let { it1 -> deletePhoto(it1) }
            }
        }
    }

    private fun deletePhoto(uri: Uri) {
        try {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    activity?.contentResolver?.let {
                        MediaStore.createDeleteRequest(it,
                            listOf(uri)).intentSender
                    }
                }
                else -> null
            }
            intentSender?.let {
                intentSenderRequest.launch(IntentSenderRequest.Builder(it).build())
            }
        } catch (e: SecurityException) {
            Log.e("SecurityException", e.message.toString())
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}