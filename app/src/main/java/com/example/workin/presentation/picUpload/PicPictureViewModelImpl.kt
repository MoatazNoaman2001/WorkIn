package com.example.workin.presentation.picUpload

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.MainUserRepoImpl
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.UpdateUserUseCase
import com.example.workin.domain.useCases.UploadPersonalCoverImageUseCase
import com.example.workin.domain.useCases.UploadPersonalImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PicPictureViewModelImpl @Inject constructor(
    val repo: MainUserRepoImpl,
    private val updateUserUseCase: UpdateUserUseCase,
    private val uploadPersonalImageUseCase: UploadPersonalImageUseCase,
    private val uploadPersonalCoverImageUseCase: UploadPersonalCoverImageUseCase
) : PicPictureViewModel,
    ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _user = MutableStateFlow<Resources<User>?>(null)

    init {
        inspectUser()
        getUser()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var user = _user.asStateFlow()
        private set

    override fun createUser(): Resources<User> = repo.createUser()
    override fun updateUser(user: User): Resources<User> = updateUserUseCase(user)!!
    override fun getUser()
    {
        repo.getUser { res ->
            viewModelScope.launch {
                _user.emit(res)
            }
        }
    }

    override fun uploadPersonalImage(context: Context, uri: String, imageview: AppCompatImageView) {
        uploadPersonalImageUseCase.launch(
            context, uri, imageview
        )
    }

    override fun uploadPersonalCoverImage(
        context: Context,
        uri: String,
        imageview: AppCompatImageView
    ) {
        uploadPersonalCoverImageUseCase.launch(context, uri, imageview)
    }

    override fun inspectPersonalImageResult() = uploadPersonalImageUseCase()
    override fun inspectPersonalCoverImageResult() = uploadPersonalCoverImageUseCase()
    override fun inspectUser() = repo.inspectUser()


}