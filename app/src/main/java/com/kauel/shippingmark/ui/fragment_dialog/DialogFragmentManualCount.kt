package com.kauel.shippingmark.ui.fragment_dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.DialogFragmentManualCountBinding
import com.kauel.shippingmark.utils.ERROR_MANUAL_COUNT

class DialogFragmentManualCount : DialogFragment(R.layout.dialog_fragment_manual_count) {

    private var binding: DialogFragmentManualCountBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = DialogFragmentManualCountBinding.bind(view)
        binding = fragmentBinding
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        init()
        setUpView()
    }

    private fun init() {
        binding?.apply {
            edtCount.isFocusable = true
        }
    }

    private fun setUpView() {
        binding?.apply {
            ivCloseDialog.setOnClickListener {
                dismiss()
            }
            btnConfirmCount.setOnClickListener {
                val value = edtCount.text.trim().toString()
                if (value.isEmpty()) {
                    edtCount.error = ERROR_MANUAL_COUNT
                    edtCount.requestFocus()
                    return@setOnClickListener
                } else {
                    val i: Intent = Intent().putExtra("value",value)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
                    edtCount.setText("")
                    dismiss()
                }
            }
        }
    }

}