package com.example.workin

import androidx.lifecycle.ViewModel
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.GetUserUseCase
import com.example.workin.domain.useCases.UpdateUserUseCase
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface SummaryViewModel {
    fun updateUser(subTitle:String)
    fun updateSummary(subTitle:String)
}
