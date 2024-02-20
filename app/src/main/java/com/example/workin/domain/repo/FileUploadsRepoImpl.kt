package com.example.workin.domain.repo

import android.util.Log
import androidx.core.net.toUri
import com.example.workin.commons.di.PicCoverStorageViewModeScope
import com.example.workin.commons.di.PicStorageViewModeScope
import com.google.firebase.storage.StorageReference
import java.io.File
import javax.inject.Inject

private const val TAG = "FileUploadsRepoImpl"

class FileUploadsRepoImpl
@Inject constructor(
    @PicStorageViewModeScope val mainUserProfilePic: StorageReference,
    @PicCoverStorageViewModeScope val mainUserProfileCoverPic: StorageReference,
) : FileUploadsRepo {

    private var fileResource: ((FileResource)->Unit)? = null
    override fun uploadPersonalImage(file: File) {
        mainUserProfilePic.putFile(file.toUri()).addOnProgressListener {
            val current = it.bytesTransferred
            val total = it.totalByteCount
            val progress = (current / total) * 100
            Log.d(TAG, "uploadImage: $progress")
            fileResource?.invoke(FileResource.loading(progress))
        }.addOnFailureListener {
            fileResource?.invoke(FileResource.failure(it.message))
        }.addOnSuccessListener {
            mainUserProfilePic.downloadUrl.addOnSuccessListener {
                fileResource?.invoke(FileResource.success(it.toString()))
            }
        }
    }

    override fun uploadPersonalCoverImage(file: File) {
        mainUserProfileCoverPic.putFile(file.toUri()).addOnProgressListener {
            val current = it.bytesTransferred
            val total = it.totalByteCount
            val progress = (current / total) * 100
            Log.d(TAG, "uploadImage: $progress")
            fileResource?.invoke(FileResource.loading(progress))
        }.addOnFailureListener {
            fileResource?.invoke(FileResource.failure(it.message))
        }.addOnSuccessListener {
            mainUserProfileCoverPic.downloadUrl.addOnSuccessListener {
                fileResource?.invoke(FileResource.success(it.toString()))
            }
        }
    }

    override fun getCurrentProgress(fileResource: ((FileResource)->Unit)) {
        this.fileResource = fileResource
    }


}