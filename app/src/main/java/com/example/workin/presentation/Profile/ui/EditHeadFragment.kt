package com.example.workin.presentation.Profile.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.workin.R
import com.example.workin.databinding.FragmentEditHeadBinding
import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.repo.Resources
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "EditHeadFragment"

@AndroidEntryPoint
class EditHeadFragment : Fragment(), View.OnClickListener {
    private lateinit var _binding: FragmentEditHeadBinding
    private val profilePageViewModelImpl: ProfilePageViewModelImpl by viewModels()
    private val binding get() = _binding
    private var uris = arrayOfNulls<Uri>(2)
    private var imgedtMd = 0
    lateinit var user: User
    lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpImgPicker()
        binding.headEditBtn.setOnClickListener(this)
        binding.editCoverImg.setOnClickListener(this)
        //preview user changes
        lifecycleScope.launch {
            profilePageViewModelImpl.user.collectLatest {
                when (it) {
                    is Resources.failed -> {

                    }

                    is Resources.loading -> {
                        binding.root.children.map { it.isVisible = false }
                        binding.loadingUser.isVisible = true
                    }

                    is Resources.success -> {
                        binding.root.children.map { it.isVisible = true }
                        binding.loadingUser.isVisible = false
                        user = it.data
                        uris = arrayOf(
                            Uri.parse(user.personalImage.picFileUri),
                            Uri.parse(user.coverImage.picFileUri)
                        )
                        binding.NameTextInputLayout.editText?.setText(user.name)
                        binding.headerTextInputLayout.editText?.setText(user.subHead)
                        setUserImage(user.personalImage)
                        setUserCoverImage(user.coverImage)
                    }
                }
            }
        }

        profilePageViewModelImpl.editedArr.observe(viewLifecycleOwner) { editedArr->
            if (editedArr.any{ it }) {
                Log.d(TAG, "onViewCreated: changes detected")
                binding.submitBtn.isVisible = true
                binding.submitBtn.setOnClickListener {
                    if (editedArr[0])
                        profilePageViewModelImpl.updatePersonalImage(
                            requireContext(),
                            uris[0].toString(),
                            binding.editCoverImg
                        )
                    if (editedArr[1])
                        lifecycleScope.launch {

                            profilePageViewModelImpl.updateCover(requireContext(), uris[1].toString(), binding.editCoverImg).collectLatest {
                                when(it){
                                    is FileResource.failure -> {

                                    }
                                    is FileResource.loading -> {
                                        //we could make it progress notification and use foreground services , but i think that to much work to done

                                    }
                                    is FileResource.success -> {
                                        Log.d(TAG, "onViewCreated: Done")
                                        Toast.makeText(requireContext() , it.success, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    if (editedArr[2] || editedArr[3]) {
                        user.name = binding.NameTextInputLayout.editText?.text.toString()
                        user.subHead = binding.headerTextInputLayout.editText?.text.toString()
                        profilePageViewModelImpl.updateUser(user)
                    }
                }
            }else{
                binding.submitBtn.isVisible = false
            }
        }

        binding.NameTextInputLayout.editText?.addTextChangedListener(
            onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                profilePageViewModelImpl.editedArr.value = profilePageViewModelImpl.editedArr.value?.apply {  set(2, user.name != text.toString())}
            }
        )
        binding.headerTextInputLayout.editText?.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                profilePageViewModelImpl.editedArr.value = profilePageViewModelImpl.editedArr.value?.apply {  set(3, user.subHead != text.toString())}
            }
        )
    }

    private fun setUserCoverImage(coverImage: Pic) {
        if (coverImage.hasPic) {
            setCoverIcon(coverImage.picFileUri.ifEmpty { coverImage.picCloudUri }).into(binding.appBarImage)
        }
    }

    private fun setUserImage(personalImage: Pic) {
        if (personalImage.hasPic) {
            setAvatarIcon(personalImage.picFileUri.ifEmpty { personalImage.picCloudUri })
                .circleCrop()
                .centerCrop()
                .into(binding.shapeableImageView)
        } else {
            setAvatarIcon(R.drawable.anonymous).circleCrop().into(binding.shapeableImageView)
        }
    }

    private fun setUpImgPicker() {
        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                if (imgedtMd == 0) {
                    storeAndSetPersonalImage(it)
                } else if (imgedtMd == 1) {
                    storeAndSetCoverImage(it)
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.headEditBtn || v.id == R.id.editCoverImg)
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        if (v.id == R.id.headEditBtn) {
            imgedtMd = 0
        } else if (v.id == R.id.editCoverImg) {
            imgedtMd = 1
        }
    }

    private fun storeAndSetCoverImage(it: Uri) {
        setCoverIcon(it).into(binding.appBarImage)
        profilePageViewModelImpl.editedArr.value = profilePageViewModelImpl.editedArr.value?.apply {  set(1,true)}
        uris[1] = it
    }

    private fun storeAndSetPersonalImage(it: Uri) {
        setAvatarIcon(it).into(binding.shapeableImageView)
        profilePageViewModelImpl.editedArr.value = profilePageViewModelImpl.editedArr.value?.apply {  set(0, true)}
        uris[0] = it
    }

    private fun setAvatarIcon(url: Any): RequestBuilder<Bitmap> {
        return Glide.with(this)
            .asBitmap()
            .load(url)
            .centerCrop()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    private fun setCoverIcon(url: Any): RequestBuilder<Bitmap> {
        return Glide.with(this)
            .asBitmap()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditHeadBinding.inflate(layoutInflater)
        return binding.root
    }


}