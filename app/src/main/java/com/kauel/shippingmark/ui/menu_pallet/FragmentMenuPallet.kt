package com.kauel.shippingmark.ui.menu_pallet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.FragmentMenuPalletTypeBinding
import com.kauel.shippingmark.utils.*

class FragmentMenuPallet : Fragment(R.layout.fragment_menu_pallet_type) {

    private var binding: FragmentMenuPalletTypeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentMenuPalletTypeBinding.bind(view)
        binding = fragmentBinding
        init()
        setUpView()
    }

    private fun init() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val new = sharedPref.getBoolean("NEW", true)
        val id = sharedPref.getString("ID_TRANSPORT", "")
        binding?.apply {
            if (!new) {
                edtIdTransport.setText(id)
            }
        }
    }

    private fun setUpView() {
        binding?.apply {
            btnBase5.setOnClickListener {
                selectedBase(BASE_5, TYPE_1)
            }
            btnBase6.setOnClickListener {
                selectedBase(BASE_6, TYPE_1)
            }
            btnUploadImage.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentManuPallet_to_fragmentUploadImage)
            }
        }
    }

    private fun selectedBase(base: String, type: String) {
        val id = binding?.edtIdTransport?.text?.trim().toString()
        if (id.isNotEmpty()) {
            saveData(base, id, type)
            findNavController().navigate(R.id.action_fragmentMenuPallet_to_fragmentMain)
        } else {
            view?.makeSnackbar(ERROR_ID_TRANSPORT, VIEW_ERROR)
        }
    }

    private fun saveData(base: String, id_transport: String, type: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("BASE", base)
            putString("ID_TRANSPORT", id_transport)
            putString("TYPE", type)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}