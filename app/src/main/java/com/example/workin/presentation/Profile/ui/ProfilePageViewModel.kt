package com.example.workin.presentation.Profile.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.useCases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ProfilePageViewModel{

        fun inspectUser();
        fun updateUser(user: User);
        fun updateCover(context: Context, uri: String, imageview: AppCompatImageView): Flow<FileResource>
        fun updatePersonalImage(context: Context, uri: String, imageview: AppCompatImageView)

}
