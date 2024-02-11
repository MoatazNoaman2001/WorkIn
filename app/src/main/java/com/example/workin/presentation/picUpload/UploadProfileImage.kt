package com.example.workin.presentation.picUpload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.bumptech.glide.RequestManager
import com.example.workin.R
import com.example.workin.commons.Constant
import com.example.workin.databinding.FragmentUploadProfileImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class UploadProfileImage : Fragment() {

    private lateinit var _binding: FragmentUploadProfileImageBinding
    private val binding get() = _binding

    @Inject
    lateinit var glImage: RequestManager

    @Inject
    lateinit var userStorageRef: StorageReference

    @Inject
    lateinit var auth: FirebaseAuth
    private var user = lazy {
        auth.currentUser
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        glImage.asBitmap().load(R.drawable.anonymous).circleCrop().into(binding.choosenPic)
        val picImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                uploadImage(uri = it)
            }
        }

        binding.picBtn.setOnClickListener {
            picImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }

    fun uploadImage(uri: Uri) {
        val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG , 50 , baos)
        val fileDir = File(requireContext().filesDir , "temp_img.jpg")
        baos.writeTo(FileOutputStream(fileDir))

        if (user.value != null)
            userStorageRef.child(user.value!!.uid).child(Constant.ImgProfile).putFile(fileDir.toUri())
                .addOnProgressListener {
                    
                }.addOnSuccessListener {

                }.addOnFailureListener {

                }
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