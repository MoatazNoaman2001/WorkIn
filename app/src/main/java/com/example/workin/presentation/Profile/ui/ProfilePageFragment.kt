package com.example.workin.presentation.Profile.ui

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.FragmentProfilePageBinding
import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.Resources
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


private const val TAG = "ProfilePageFragment"

@AndroidEntryPoint
class ProfilePageFragment : Fragment() {
    lateinit var _binding: FragmentProfilePageBinding
    private val profileViewModel: ProfilePageViewModelImpl by viewModels<ProfilePageViewModelImpl>()
    private val binding get() = _binding

    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    @Named(Constant.ImgProfileActivity)
    lateinit var storageReference: StorageReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.inspectUser()
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.user.collectLatest {
                when (it) {
                    is Resources.failed -> {

                    }

                    is Resources.loading -> {

                    }

                    is Resources.success -> {
                        setUpHeader(it.data)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun setUpHeader(user: User) {
        binding.userName.text = user.name
        binding.usersubHead.text = user.subHead
        setUserImage(user.personalImage)
        setUserCoverImage(user.coverImage)
        binding.headEditBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment2_to_editHeadFragment)
        }
    }

    private fun setUserCoverImage(coverImage: Pic) {
        if (coverImage.hasPic)
            Glide.with(requireContext())
                .load(coverImage.picFileUri.ifEmpty{coverImage.picCloudUri}).into(binding.appBarImage)

    }


    private fun setUserImage(personalImage: Pic) {
        if (personalImage.hasPic) {
            setAvatarIcon(personalImage.picFileUri.ifEmpty { personalImage.picCloudUri })
                .error(R.drawable.anonymous).placeholder(R.drawable.anonymous)
                .into(binding.shapeableImageView)
        } else {
            setAvatarIcon(R.drawable.anonymous).into(binding.shapeableImageView)
        }
    }

    private fun setAvatarIcon(url: Any): RequestBuilder<Bitmap> {
        return Glide.with(this)
            .asBitmap()
            .load(url)
            .centerCrop()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
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