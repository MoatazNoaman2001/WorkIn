package com.example.workin

import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.DrawableCompat.wrap
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.workin.commons.Constant
import com.example.workin.commons.Constant.sharedActivity
import com.example.workin.databinding.ActivityMainAppBinding
import com.example.workin.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "MainAppActivity"

@AndroidEntryPoint
class MainAppActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    @Inject
    @Named(sharedActivity)
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @Named(Constant.ImgProfileActiviy)
    lateinit var storageReference: StorageReference

    @Inject
    lateinit var gl_loader: RequestManager
//    private val binding by viewBinding(ActivityMainAppBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //make search view action bar
        setSupportActionBar(binding.searchBar)
        //bind search view with search bar
        binding.searchView.setupWithSearchBar(binding.searchBar)
        //inflate menu because of un known error
        binding.searchBar.inflateMenu(R.menu.search_bar_avatar_menu)

        val navController = findNavController(R.id.nav_host_fragment_content_main_app)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).setOpenableLayout(binding.drawerLayout)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupWithNavController(binding.drawerBook, navController)
        setupWithNavController(binding.bottomNavView , navController)
        //set avatar image
        checkUserStatus()


        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if (destination.id != R.id.FirstFragment) {
//                    binding.toolbar.navigationIcon = tintDrawable(ResourcesCompat.getDrawable(resources , R.drawable.baseline_arrow_back_ios_new_24, theme)!!)
                }
            }

            private fun tintDrawable(drawable: Drawable): Drawable {
                var drawable = wrap(drawable)
                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                    DrawableCompat.setTint(
                        drawable,
                        ResourcesCompat.getColor(resources, R.color.white, theme)
                    )
                else
                    DrawableCompat.setTint(
                        drawable,
                        ResourcesCompat.getColor(resources, R.color.black, theme)
                    )
                return drawable
            }

        })

    }

    private fun checkUserStatus() {
        val user = loadUser()
        Log.d(TAG, "checkUserStatus: user : $user")
        if (user.picCloudUri.isEmpty() && user.picFileUri.isEmpty())
            storageReference.downloadUrl.addOnSuccessListener {
                Log.d(TAG, "checkUserStatus: img uri: $it")
                if (it != null)
                    setAvatarIcon(it.toString())
                else
                    setAvatarIcon(R.drawable.anonymous)
            }.addOnFailureListener {
                setAvatarIcon(R.drawable.anonymous)
            }
        else
            setAvatarIcon(user.picFileUri.ifEmpty { user.picCloudUri })
    }

    private fun setAvatarIcon(url: Any) {
        Glide.with(this)
            .asDrawable()
            .load(url)
            .centerCrop()
            .circleCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "onLoadFailed: Failed loading image")
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    renderProfile(resource)
                    return true
                }

                private fun renderProfile(resource: Drawable) {
                    Log.d(TAG, "renderProfile: ${binding.searchBar.menu.findItem(R.id.profile_avatar)}")
                    binding.searchBar.menu.findItem(R.id.profile_avatar).setIcon(resource)
                }

            }).submit()
    }

    private fun loadUser(): User {
        return Gson().fromJson(preferences.getString(Constant.MainUser, null), User::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_app)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}