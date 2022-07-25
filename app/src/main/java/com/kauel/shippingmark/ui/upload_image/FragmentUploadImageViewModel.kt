package com.kauel.shippingmark.ui.upload_image

import androidx.lifecycle.*
import com.kauel.shippingmark.api.uploadImage.DataImagesUploaded
import com.kauel.shippingmark.api.uploadImage.FileName
import com.kauel.shippingmark.api.uploadImage.ResponseFile
import com.kauel.shippingmark.data.dao.DataImagesUploadedDao
import com.kauel.shippingmark.data.repository.Repository
import com.kauel.shippingmark.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FragmentUploadImageViewModel @Inject constructor(
    private val repository: Repository,
    private val dataImagesUploadedDao: DataImagesUploadedDao,
) : ViewModel() {

    private val uploadImageMutableLiveData = MutableLiveData<Resource<List<ResponseFile>>>()
    val uploadImageLiveData: LiveData<Resource<List<ResponseFile>>> = uploadImageMutableLiveData

    fun uploadImage(
        userName: RequestBody,
        list: List<FileName>,
    ) = viewModelScope.launch {
        repository.uploadImageCustom(
            list,
            userName
        ).collect { uploadImageMutableLiveData.value = it }
    }

    fun stopUploadImage() = viewModelScope.launch { repository.stopUpload() }

    fun insertData(data: DataImagesUploaded) {
        viewModelScope.launch {
            repository.insertDataImagesUploaded(data)
        }
    }

    private val dataTask = dataImagesUploadedDao.getAllDataImagesUploaded()
    val liveData = dataTask.asLiveData()

}