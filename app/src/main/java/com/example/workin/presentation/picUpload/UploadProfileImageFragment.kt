package com.example.workin.presentation.picUpload

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.RequestManager
import com.example.workin.MainAppActivity
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.FragmentUploadProfileImageBinding
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.repo.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "UploadProfileImage"

@AndroidEntryPoint
class UploadProfileImageFragment : Fragment() {

    private lateinit var _binding: FragmentUploadProfileImageBinding
    private val binding get() = _binding
    lateinit var controller: NavController

    private val picPictureViewModel: PicPictureViewModelImpl by viewModels()

    @Inject
    lateinit var glImage: RequestManager

    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var userStorageRef: StorageReference

    @Inject
    lateinit var auth: FirebaseAuth

    private var currentUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private var isUploaded = false
    lateinit var mainUser: User
    private var uploadSwitch = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultImg()
        controller = Navigation.findNavController(requireView())
        val picImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                if (!isUploaded && uploadSwitch == 0){
                    uploadPresonalImage(it)
                }else if(!isUploaded && uploadSwitch == 1){
                    uploadPersonalCoverImage(it)
                }
//                currentUploadTask?.cancel()
//                currentUploadTask = uploadPresonalImage(uri = it)
            }
        }
        handleBackBtnPressed()

        CoroutineScope(Dispatchers.Main).launch {
            picPictureViewModel.user.collectLatest {
                when (it) {
                    is Resources.failed -> {

                    }

                    is Resources.loading -> {

                    }

                    is Resources.success -> {
                        mainUser = it.data
                        if (it.data.personalImage.hasPic) {
                            binding.skipBtn.text = getString(R.string.next)
                        } else {
                            binding.skipBtn.text = getString(R.string.skip)
                        }
                        binding.skipBtn.setOnClickListener {
                            requireActivity().startActivity(Intent(requireContext() , MainAppActivity::class.java))
                        }
                        binding.picBtn.setOnClickListener {
                            uploadSwitch = 0
                            picImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        binding.addCoverImg.setOnClickListener {
                            uploadSwitch = 1
                            picImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        binding.skipBtn.setOnClickListener {
                            requireActivity().startActivity(Intent(requireContext() , MainAppActivity::class.java))
                        }
                    }

                    else -> {}
                }
            }
        }


    }

    private fun uploadPersonalCoverImage(uri: Uri) {

        picPictureViewModel.uploadPersonalCoverImage(
            requireContext(),
            uri.toString(),
            binding.coverPic
        )
        isUploaded = !isUploaded
        disablePicCoverBtn()
        CoroutineScope(Dispatchers.IO).launch {
            picPictureViewModel.inspectPersonalCoverImageResult().collectLatest {
                when (it) {
                    is FileResource.failure -> {
                        isUploaded = !isUploaded
                        MainScope().launch {
                            if (uploadSwitch == 1)
                                enablePicCoverBtn()

                        }
                        Log.d(TAG, "onViewCreated: error: ${it.failure}")
                        Constant.displayMessage(
                            requireContext(),
                            "Connection Error",
                            "you seems to be offline and there is a missed data that should be exist , this app in dev mode so make sure of your internet connection",
                            { dialog, _ ->

                            },
                            { dialog, _ ->

                            },
                            { dialog ->

                            }
                        )
                    }

                    is FileResource.loading -> {
                        MainScope().launch {
                            if (uploadSwitch == 1) {
                                binding.coverProgressIndicator.progress = it.progress.toInt()
                                disablePicCoverBtn()
                            }
                        }
                        Log.d(TAG, "onViewCreated: loading upload image")
                    }

                    is FileResource.success -> {
                        MainScope().launch {
                            isUploaded = !isUploaded
                            if (uploadSwitch == 1) {
                                enablePicCoverBtn()
                                Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleBackBtnPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    requireActivity().finish()
                }
            })
    }

    fun uploadPresonalImage(uri: Uri) {
        /*
        Version 0.002

        picPictureViewModel.uploadPersonalImage(
            requireContext(),
            it.toString(),
            mainUser.value!!,
            binding.choosenPic
        )
        isUploaded = !isUploaded
        CoroutineScope(Dispatchers.IO).launch {
            picPictureViewModel.inspectResult().collectLatest {
                when (it) {
                    is FileResource.failure -> {
                        isUploaded = !isUploaded
                        MainScope().launch {
                            if (uploadSwitch == 0)
                                enablePicBtn()

                        }
                        Log.d(TAG, "onViewCreated: error: ${it.failure}")
                        Constant.displayMessage(
                            requireContext(),
                            "Connection Error",
                            "you seems to be offline and there is a missed data that should be exist , this app in dev mode so make sure of your internet connection",
                            { dialog, _ ->

                            },
                            { dialog, _ ->

                            },
                            { dialog ->

                            }
                        )
                    }

                    is FileResource.loading -> {
                        MainScope().launch {
                            if (uploadSwitch == 0) {
                                binding.progressIndicator.progress = it.progress.toInt()
                                disablePicBtn()
                            } else {
                                binding.coverProgressIndicator.progress = it.progress.toInt()
                            }
                        }
                        Log.d(TAG, "onViewCreated: loading upload image")
                    }

                    is FileResource.success -> {
                        MainScope().launch {
                            isUploaded = !isUploaded
                            if (uploadSwitch == 0)
                                enablePicBtn()
                            Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                }
            }
        }*/

        //Version 0.003
        picPictureViewModel.uploadPersonalImage(
            requireContext(),
            uri.toString(),
            binding.choosenPic
        )
        isUploaded = !isUploaded
        CoroutineScope(Dispatchers.IO).launch {
            picPictureViewModel.inspectPersonalImageResult().collectLatest {
                when (it) {
                    is FileResource.failure -> {
                        isUploaded = !isUploaded
                        MainScope().launch {
                            if (uploadSwitch == 0)
                                enablePicBtn()

                        }
                        Log.d(TAG, "onViewCreated: error: ${it.failure}")
                        Constant.displayMessage(
                            requireContext(),
                            "Connection Error",
                            "you seems to be offline and there is a missed data that should be exist , this app in dev mode so make sure of your internet connection",
                            { dialog, _ ->

                            },
                            { dialog, _ ->

                            },
                            { dialog ->

                            }
                        )
                    }

                    is FileResource.loading -> {
                        MainScope().launch {
                            if (uploadSwitch == 0) {
                                binding.progressIndicator.progress = it.progress.toInt()
                                disablePicBtn()
                            }
                        }
                        Log.d(TAG, "onViewCreated: loading upload image")
                    }

                    is FileResource.success -> {
                        MainScope().launch {
                            isUploaded = !isUploaded
                            if (uploadSwitch == 0)
                                enablePicBtn()
                            Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                }
            }
        }
    }

    private fun disablePicBtn() {
        binding.picBtn.isVisible = false
        binding.skipBtn.isVisible = false
        binding.imgUploadLoading.isVisible = true
    }

    private fun enablePicBtn() {
        binding.picBtn.isVisible = true
        binding.skipBtn.isVisible = true
        binding.imgUploadLoading.isVisible = false
    }

    private fun disablePicCoverBtn() {
        binding.addCoverImg.isVisible = false
        binding.skipBtn.isVisible = false
    }

    private fun enablePicCoverBtn() {
        binding.addCoverImg.isVisible = true
        binding.skipBtn.isVisible = true
    }

    private fun defaultImg() {
        glImage.asBitmap().load(R.drawable.anonymous).circleCrop().into(binding.choosenPic)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProfileImageBinding.inflate(layoutInflater)
        return binding.root
    }
}