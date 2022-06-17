package com.kauel.shippingmark.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.login.ResponseLogin
import com.kauel.shippingmark.data.repository.Repository
import com.kauel.shippingmark.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentLoginViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val loginMutableLiveData = MutableLiveData<Resource<ResponseLogin>>()
    val loginLiveData: LiveData<Resource<ResponseLogin>> = loginMutableLiveData

    fun login(user: Login) =
        viewModelScope.launch {
            repository.login(user).collect {
                loginMutableLiveData.value = it
            }
        }
}