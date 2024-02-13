package com.example.workin.presentation.Profile.ui

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.databinding.FragmentProfilePageBinding
import com.example.workin.domain.model.User
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


private const val TAG = "ProfilePageFragment"
@AndroidEntryPoint
class ProfilePageFragment : Fragment() {
    lateinit var _binding :FragmentProfilePageBinding
    private val binding get() = _binding
    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    @Named(Constant.ImgProfileActiviy)
    lateinit var storageReference: StorageReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
    }

    private fun setUpHeader() {
        val user = Gson().fromJson(preferences.getString(Constant.MainUser , null), User::class.java)
        binding.userName.text = user.name
        setUserImage()


    }


    private fun setUserImage() {
        val user = loadUser()
        Log.d(TAG, "checkUserStatus: user : $user")
        if (user.personalImage.picCloudUri.isEmpty() && user.personalImage.picFileUri.isEmpty())
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
            setAvatarIcon(user.personalImage.picFileUri.ifEmpty { user.personalImage.picCloudUri })
    }

    private fun setAvatarIcon(url: Any) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .centerCrop()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.shapeableImageView)
    }

    private fun loadUser(): User {
        return Gson().fromJson(preferences.getString(Constant.MainUser, null), User::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePageBinding.inflate(layoutInflater)
        return binding.root
    }
}