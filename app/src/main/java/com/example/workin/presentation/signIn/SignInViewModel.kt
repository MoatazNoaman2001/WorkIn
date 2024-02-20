package com.example.workin.presentation.signIn

import androidx.lifecycle.ViewModel
import com.example.workin.domain.useCases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(getUserUseCase: GetUserUseCase) : ViewModel(){
    var email:String = ""
    var password:String =""

    val user = getUserUseCase()

    init {
        getUserUseCase.launch()
    }
}