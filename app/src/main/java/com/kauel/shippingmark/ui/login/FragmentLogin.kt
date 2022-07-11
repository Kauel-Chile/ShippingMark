package com.kauel.shippingmark.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kauel.shippingmark.R
import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.databinding.FragmentLoginBinding
import com.kauel.shippingmark.utils.*
import com.kauel.shippingmark.BuildConfig
import com.kauel.shippingmark.api.login.ResponseLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentLogin : Fragment(R.layout.fragment_login) {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: FragmentLoginViewModel by viewModels()

    private var rut: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentLoginBinding.bind(view)
        binding = fragmentBinding
        init()
        setUpView()
        initObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        val versionName = BuildConfig.VERSION_NAME
        binding?.apply {
            tvVersion.text = "$VERSION_NAME $versionName"
        }
    }

    private fun setUpView() {
        binding?.apply {
            btnLogin.setOnClickListener {
                rut = formatRut(edtRut.text.toString())

                if (rut.isEmpty()) {
                    edtRut.error = ERROR_RUT_1
                    edtRut.requestFocus()
                    return@setOnClickListener
                } else {
                    if (validaRut(rut) == false) {
                        edtRut.error = ERROR_RUT_2
                        edtRut.requestFocus()
                        return@setOnClickListener
                    } else {
                        if (isOnline(requireContext())) {
                            login()
                        } else {
                            view?.makeSnackbar(ERROR_NO_INTERNET, VIEW_ERROR)
                            lifecycleScope.launch {
                                delay(3000)
                                saveData()
                                findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMenuPallet)
                            }
                        }
                    }
                }

            }
        }
    }

    private fun initObservers() {
        viewModel.loginLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Error -> showErrorView(result.error, result.data)
                is Resource.Loading -> showLoadingView()
                is Resource.Success -> showSuccessView(result.data)
            }
        }
    }

    private fun login() {
        val user = Login(EMAIL_LOGIN, PASSWORD_LOGIN)
        viewModel.login(user = user)
    }

    private fun showSuccessView(data: ResponseLogin?) {
        binding?.apply {
            progressBar.gone()
        }
        saveData()
        findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMenuPallet)
    }

    private fun showLoadingView() {
        binding?.apply {
            lyMenu.gone()
            progressBar.visible()
        }
    }

    private fun showErrorView(error: Throwable?, data: ResponseLogin?) {
        binding?.apply {
            lyMenu.visible()
            progressBar.gone()
            view?.makeSnackbar("${error?.message.toString()} ${data?.message.toString()}", false)
            lifecycleScope.launch {
                delay(3000)
                saveData()
                findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMenuPallet)
            }
        }
    }

    private fun saveData() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("RUT_OPERATOR", rut)
            putString("BASE", "")
            putString("ID_TRANSPORT", "")
            putString("TYPE", "")
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}