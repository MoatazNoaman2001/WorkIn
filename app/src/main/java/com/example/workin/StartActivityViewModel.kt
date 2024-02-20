package com.example.workin

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.workin.commons.Constant
import com.example.workin.databinding.ActivityMainBinding
import com.example.workin.domain.repo.Resources
import com.example.workin.domain.useCases.GetUserUseCase
import com.example.workin.presentation.picUpload.PicPictureViewModelImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartActivityViewModel @Inject constructor(getUserUseCase: GetUserUseCase): ViewModel(){
    val user = getUserUseCase()
    init {
        getUserUseCase.launch()
    }
}