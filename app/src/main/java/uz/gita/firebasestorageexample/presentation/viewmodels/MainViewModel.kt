package uz.gita.firebasestorageexample.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.firebasestorageexample.domain.repository.AppRepository

class MainViewModel : ViewModel() {

    private val appRepository = AppRepository()

    val imagesData = MutableLiveData<List<Uri>>()
    val errorData = MutableLiveData<String>()

    init {
        getAllData()
    }

    private fun getAllData() {
        appRepository.getImages()
            .onEach { listImgUrl ->
                listImgUrl.onSuccess { imagesData.value = it }
                listImgUrl.onFailure { errorData.value = it.message }
            }.launchIn(viewModelScope)
    }
}