package com.kauel.shippingmark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.data.repository.Repository
import com.kauel.shippingmark.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FragmentMainViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val sendDataMutableLiveData = MutableLiveData<Resource<ResponseSendData>>()
    val sendDataLiveData: LiveData<Resource<ResponseSendData>> = sendDataMutableLiveData

    fun sendData(
        data: Data,
    ) =
        viewModelScope.launch {
            repository.sendData(
                data).collect { sendDataMutableLiveData.value = it }
        }

}