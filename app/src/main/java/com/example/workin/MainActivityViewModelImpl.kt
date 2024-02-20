package com.example.workin

import androidx.lifecycle.ViewModel
import com.example.workin.domain.useCases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModelImpl @Inject constructor(val getUserUseCase: GetUserUseCase) : MainActivityViewModel,
    ViewModel() {
        val user = getUserUseCase()
    override fun checkAuth() {

    }
}