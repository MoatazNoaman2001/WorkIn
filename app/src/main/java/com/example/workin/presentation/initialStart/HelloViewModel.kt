package com.example.workin.presentation.initialStart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.MainUserRepo
import com.example.workin.domain.repo.MainUserRepoImpl
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HelloViewModel @Inject constructor(getUserUseCase: GetUserUseCase): ViewModel() {
    var user = getUserUseCase()

    init {
        getUserUseCase.launch()
    }
}