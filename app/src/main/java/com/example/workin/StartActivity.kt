package com.example.workin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.workin.commons.Constant
import com.example.workin.commons.Constant.displayMessage
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.ActivityMainBinding
import com.example.workin.domain.repo.Resources
import com.example.workin.presentation.picUpload.PicPictureViewModelImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    private var isLoading = true

    private val startActivityViewModel: StartActivityViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen().setKeepOnScreenCondition { isLoading }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment

        controller = navHostFragment.navController
        controller.setGraph(R.navigation.start_nav_graph)
        if (auth.currentUser != null && auth.currentUser?.isEmailVerified!!) {
            try {
                lifecycleScope.launch(Dispatchers.IO) {
                    startActivityViewModel.user.collectLatest {
                        when (it) {
                            is Resources.failed -> {
                                MainScope().launch {
                                    Log.d(TAG, "onCreate: Error")
                                    isLoading = !isLoading
                                    displayMessage(
                                        this@StartActivity,
                                        "Connection Error",
                                        it.message,
                                        DialogInterface.OnClickListener { dialog, _ ->
                                                                        dialog.dismiss()
                                        },
                                        DialogInterface.OnClickListener { _, _ -> },
                                        DialogInterface.OnDismissListener{ _ ->

                                        }
                                    )
                                }
                            }

                            is Resources.loading -> {
                                Log.d(TAG, "onCreate: Loading")
                            }

                            is Resources.success -> {
                                val user = it.data
                                if (user.category.isNotEmpty() && user.phoneNumber.isNotEmpty() && user.subHead.isNotEmpty())
                                    MainScope().launch {
                                        isLoading = !isLoading
                                        startActivity(Intent(this@StartActivity, MainAppActivity::class.java))
                                        finish()
                                    }
                                else {
                                    isLoading = !isLoading
                                }
                            }

                            null -> {
                                Log.d(TAG, "onCreate: Null!")
                            }
                        }
                    }

                }

            } catch (e: Exception) {
                isLoading = !isLoading
                displayMessage(
                    this@StartActivity,
                    "Connection Error",
                    "you seems to be offline and there is a missed data that should be exist , this app in dev mode so make sure of your internet connection",
                    DialogInterface.OnClickListener { _, _ -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
                    DialogInterface.OnClickListener { _, _ -> },
                    DialogInterface.OnDismissListener{ _ -> this@StartActivity.finish() }
                )
            }
        } else
            isLoading = !isLoading
        appBarConfiguration = AppBarConfiguration.Builder(controller.graph).build()
    }


}