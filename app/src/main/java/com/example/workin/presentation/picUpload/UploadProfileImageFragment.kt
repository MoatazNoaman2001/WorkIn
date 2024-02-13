package com.example.workin.presentation.picUpload

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.databinding.FragmentUploadProfileImageBinding
import com.example.workin.domain.model.Pic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "UploadProfileImage"

@AndroidEntryPoint
class UploadProfileImageFragment : Fragment() {

    private lateinit var _binding: FragmentUploadProfileImageBinding
    private val binding get() = _binding
    lateinit var controller: NavController

    @Inject
    lateinit var glImage: RequestManager

    @Inject
    @Named(Constant.sharedFragment)
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var userStorageRef: StorageReference

    @Inject
    lateinit var auth: FirebaseAuth

    private var currentUploadTask : StorageTask<UploadTask.TaskSnapshot>? = null
    private var isUploaded = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultImg()
        controller = Navigation.findNavController(requireView())
        val picImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                currentUploadTask?.cancel()
                currentUploadTask = uploadImage(uri = it)
            }
        }
        handleBackBtnPressed()

        binding.picBtn.setOnClickListener {
            picImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.skipBtn.setOnClickListener {
            controller.navigate(R.id.action_uploadProfileImage_to_usersCategoryFragment)
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

    fun uploadImage(uri: Uri): StorageTask<UploadTask.TaskSnapshot> {
        val bitmap =
            BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val fileDir = File(requireContext().filesDir, "temp_profile_img.jpg")
        baos.writeTo(FileOutputStream(fileDir))
        Glide.with(requireContext()).load(fileDir).circleCrop().into(binding.choosenPic)
        disablePicBtn()
        return userStorageRef.child(auth.currentUser?.uid!!).child(Constant.ImgProfile).putFile(fileDir.toUri())
            .addOnProgressListener {
                val current = it.bytesTransferred
                val total = it.totalByteCount
                val progress = (current / total) * 100
                Log.d(TAG, "uploadImage: $progress")
                binding.progressIndicator.progress = progress.toInt()
            }.addOnSuccessListener {
                MainScope().launch {
                    enablePicBtn()
                    Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
                    val user = SigningLogistic.checkCurrentUser(preferences)
                    binding.skipBtn.text = "Next"
                    user?.personalImage = Pic(true , fileDir.toString() , userStorageRef.child(user?.id!!).child(Constant.ImgProfile).downloadUrl.await().toString())
                    SigningLogistic.storeUserCloud(user, preferences)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "error happened, please try again",
                    Toast.LENGTH_SHORT
                ).show()
                enablePicBtn()
                Glide.with(requireView())
                defaultImg()
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