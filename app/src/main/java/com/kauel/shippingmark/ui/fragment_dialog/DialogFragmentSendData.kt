package com.kauel.shippingmark.ui.fragment_dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.DialogFragmentSendDataBinding
import com.kauel.shippingmark.utils.lifeCycleNavigate

class DialogFragmentSendData : DialogFragment(R.layout.dialog_fragment_send_data) {

    private var binding: DialogFragmentSendDataBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = DialogFragmentSendDataBinding.bind(view)
        binding = fragmentBinding
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setUpView()
    }

    private fun setUpView() {
        binding?.apply {
            var intent = Intent()
            btnNewPalett.setOnClickListener {
                intent = Intent().putExtra("new",true)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
            btnNewRecord.setOnClickListener {
                isNew(true)
                intent = Intent().putExtra("new",false)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
        }
    }

    private fun isNew(value: Boolean) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("NEW", value)
            apply()
        }
    }

}