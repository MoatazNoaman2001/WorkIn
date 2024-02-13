package com.example.workin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    private var isLoading = true

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
                    val user = SigningLogistic.checkCurrentUser(
                        getSharedPreferences(
                            Constant.MainUser,
                            MODE_PRIVATE
                        ),
                        auth.currentUser
                    )
                    if (user != null && user.category.isNotEmpty())
                        MainScope().launch {
                            isLoading = !isLoading
                            startActivity(Intent(this@StartActivity, MainAppActivity::class.java))
                            finish()
                        }
                    else{
                        isLoading = !isLoading
                    }
                }

            } catch (e: Exception) {
                isLoading = !isLoading
                Log.d(TAG, "onCreate: error: ${e.message}")
                val dialog = AlertDialog.Builder(this@StartActivity)
                    .setTitle("Connection Error")
                    .setMessage("you seems to be offline and there is a missed data that should be exist , this app in dev mode so make sure of your internet connection")
                    .setPositiveButton("OK") { dialog, which ->
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }.setOnDismissListener {
                        this@StartActivity.finish()
                    }.create()

                dialog.show()
            }
        } else
            isLoading = !isLoading
        appBarConfiguration = AppBarConfiguration.Builder(controller.graph).build()
    }
}