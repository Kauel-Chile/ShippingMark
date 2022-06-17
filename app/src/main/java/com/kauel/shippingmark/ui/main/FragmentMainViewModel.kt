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

//    fun sendData(
//        data: Data,
//    ) =
//        viewModelScope.launch {
//            repository.sendData(
//                data).collect { sendDataMutableLiveData.value = it }
//        }

    fun sendData(
        date: RequestBody,
        idPhone: RequestBody,
        rutOperator: RequestBody,
        barCode: RequestBody,
        idTransport: RequestBody,
        palletType: RequestBody,
        numberLabels1: RequestBody,
        numberBox1: RequestBody,
        manual1: RequestBody,
        numberLabels2: RequestBody,
        numberBox2: RequestBody,
        manual2: RequestBody,
        numberLabels3: RequestBody,
        numberBox3: RequestBody,
        manual3: RequestBody,
        numberLabels4: RequestBody,
        numberBox4: RequestBody,
        manual4: RequestBody,
        image1: MultipartBody.Part,
        image2: MultipartBody.Part,
        image3: MultipartBody.Part,
        image4: MultipartBody.Part,
    ) =
        viewModelScope.launch {
            repository.sendData(
                date,
                idPhone,
                rutOperator,
                barCode,
                idTransport,
                palletType,
                numberLabels1,
                numberBox1,
                manual1,
                numberLabels2,
                numberBox2,
                manual2,
                numberLabels3,
                numberBox3,
                manual3,
                numberLabels4,
                numberBox4,
                manual4,
                image1,
                image2,
                image3,
                image4).collect { sendDataMutableLiveData.value = it }
        }


}