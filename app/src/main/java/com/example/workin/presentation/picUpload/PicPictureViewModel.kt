package com.example.workin.presentation.picUpload

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.repo.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PicPictureViewModel{
    fun createUser(): Resources<User>
    fun updateUser(user: User): Resources<User>
    fun getUser()
    fun uploadPersonalImage(context: Context, uri: String,imageview: AppCompatImageView)
    fun uploadPersonalCoverImage(context: Context, uri: String,imageview: AppCompatImageView)
    fun inspectPersonalImageResult(): Flow<FileResource>
    fun inspectPersonalCoverImageResult(): Flow<FileResource>
    fun inspectUser()
}