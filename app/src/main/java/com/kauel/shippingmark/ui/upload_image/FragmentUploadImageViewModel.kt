package com.kauel.shippingmark.ui.upload_image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.api.uploadImage.FileName
import com.kauel.shippingmark.api.uploadImage.ResponseFile
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import com.kauel.shippingmark.data.repository.Repository
import com.kauel.shippingmark.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FragmentUploadImageViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val uploadImageMutableLiveData = MutableLiveData<Resource<List<ResponseFile>>>()
    val uploadImageLiveData: LiveData<Resource<List<ResponseFile>>> = uploadImageMutableLiveData

    fun uploadImage(
        userName: RequestBody,
        list: List<FileName>
    ) = viewModelScope.launch {
            repository.uploadImageCustom(
                list,
                userName
            ).collect { uploadImageMutableLiveData.value = it }
        }

    fun stopUploadImage() = viewModelScope.launch { repository.stopUpload() }

//    private val uploadImageMutableLiveData = MutableLiveData<Resource<ResponseUpload>>()
//    val uploadImageLiveData: LiveData<Resource<ResponseUpload>> = uploadImageMutableLiveData

//    fun uploadImage(
//        nameImage: RequestBody,
//        userName: RequestBody,
//        infoDate: RequestBody,
//        image: MultipartBody.Part,
//    ) =
//        viewModelScope.launch {
//            repository.uploadImage(
//                nameImage,
//                userName,
//                infoDate,
//                image).collect { uploadImageMutableLiveData.value = it }
//        }

}