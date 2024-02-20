package com.example.workin.domain.useCases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.FileResource
import com.example.workin.domain.repo.FileUploadsRepoImpl
import com.example.workin.domain.repo.MainUserRepoImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UploadPersonalImageUseCase @Inject constructor(
    val mainUserRepoImpl: MainUserRepoImpl,
    val fileRepoImpl: FileUploadsRepoImpl
) {
    private val _res = MutableStateFlow<FileResource>(FileResource.loading(0))

    fun launch(context: Context, uri: String, imageview: AppCompatImageView) {
        //pre process
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(Uri.parse(uri)))
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val fileDir = File(context.filesDir, "temp_profile_img.jpg")
        baos.writeTo(FileOutputStream(fileDir))
        Glide.with(context).load(fileDir).circleCrop().into(imageview)
        //upload
        fileRepoImpl.uploadPersonalImage(fileDir)
        //result
        fileRepoImpl.getCurrentProgress {
            runBlocking {
                when(it){
                    is FileResource.failure -> {

                    }
                    is FileResource.loading -> {

                    }
                    is FileResource.success -> {
                        val user= mainUserRepoImpl.getUserFromPreference()
                        mainUserRepoImpl.updateUser(user?.copy(personalImage = Pic(true , fileDir.toString(), fileRepoImpl.mainUserProfilePic.downloadUrl.await().toString()))!!)

                    }
                }
                _res.emit(it)
            }
        }
    }

    operator fun invoke(): Flow<FileResource> = _res
}