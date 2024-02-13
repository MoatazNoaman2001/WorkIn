package com.example.workin.domain.model

import com.example.workin.data.remote.RemoteUser
import java.io.Serializable

data class User(var name:String, val email:String, val password:String, val id:String?= "", var token:String, var hasPic: Boolean, var picFileUri:String, var picCloudUri:String, var category: String) : Serializable

fun User.toRemote() = RemoteUser(name, email, id,token, hasPic, picCloudUri , category)