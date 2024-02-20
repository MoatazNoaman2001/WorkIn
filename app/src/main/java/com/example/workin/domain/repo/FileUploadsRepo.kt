package com.example.workin.domain.repo

import java.io.File

interface FileUploadsRepo {
    fun uploadPersonalImage(file: File)

    fun uploadPersonalCoverImage(file: File)

    fun getCurrentProgress(fileResource: ((FileResource)->Unit))
}

sealed class FileResource(progress:Long= 0 , success:String?=null, failure:String?=null){
    data class success(val success: String?) : FileResource(success = success)
    data class failure(val failure: String?) : FileResource(failure = failure)
    data class loading(val progress: Long) : FileResource(progress = progress)
}