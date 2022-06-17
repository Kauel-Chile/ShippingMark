package com.kauel.shippingmark.ui.fragment_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.DialogFragmentInfoBaseBinding

class DialogFragmentInfoBase : DialogFragment(R.layout.dialog_fragment_info_base) {

    private var binding: DialogFragmentInfoBaseBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = DialogFragmentInfoBaseBinding.bind(view)
        binding = fragmentBinding
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        init()
        seuUpView()
    }

    private fun init() {
        var base = ""
        arguments?.let {
            base = it.getString("base").toString()
        }
        binding?.apply {
            var images: List<Int> = ArrayList()
            when (base) {
                "Base 5" -> {
                    tvEtiquette.text = "BASE 5 \n #15 etiquetas"
                    images = listOf(
                        R.drawable.base_5_1,
                        R.drawable.base_5_2,
                        R.drawable.base_5_3,
                        R.drawable.base_5_4)
                }
                "Base 6" -> {
                    tvEtiquette.text = "BASE 6 \n #57 etiquetas"
                    images = listOf(
                        R.drawable.base_6_1,
                        R.drawable.base_6_2)
                }
                else -> {
                    dismiss()
                }
            }
            val adapter = ViewPagerAdapter(images = images)
            viewPager2.adapter = adapter
            wormDotsIndicator.setViewPager2(viewPager2)
        }
    }

    private fun seuUpView() {
        binding?.apply {
            ivCloseDialog.setOnClickListener {
                dismiss()
            }
        }
    }


}