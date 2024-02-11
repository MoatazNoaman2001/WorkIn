package com.example.workin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.workin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
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
            isLoading = !isLoading
            startActivity(Intent(this, MainAppActivity::class.java))
        }
        isLoading = !isLoading
        appBarConfiguration = AppBarConfiguration.Builder(controller.graph).build()
    }
}