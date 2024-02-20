package com.example.workin.presentation.Profile.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.GetUserUseCase
import com.example.workin.domain.useCases.UpdateUserUseCase
import com.example.workin.domain.useCases.UploadPersonalCoverImageUseCase
import com.example.workin.domain.useCases.UploadPersonalImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilePageViewModelImpl
@Inject constructor(
    val getUserUseCase: GetUserUseCase,
    val updateUserUseCase: UpdateUserUseCase,
    val uploadPersonalImageUseCase: UploadPersonalImageUseCase,
    val uploadPersonalCoverImageUseCase: UploadPersonalCoverImageUseCase
) : ProfilePageViewModel, ViewModel() {
    var user = getUserUseCase()
    val editedArr: MutableLiveData<Array<Boolean>> = MutableLiveData(arrayOf(false, false, false, false))


    init {
        getUserUseCase.launch()
    }

    override fun inspectUser() {

    }

    override fun updateUser(user: User) {
        updateUserUseCase(user)
    }

    override fun updateCover(context: Context, uri: String, imageview: AppCompatImageView): Flow<FileResource> {
        uploadPersonalCoverImageUseCase.launch(context, uri, imageview)
        return uploadPersonalCoverImageUseCase()
    }

    override fun updatePersonalImage(context: Context, uri: String, imageview: AppCompatImageView) {

    }


}