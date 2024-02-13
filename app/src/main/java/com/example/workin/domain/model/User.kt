package com.example.workin.domain.model

import com.example.workin.data.remote.RemoteUser
import java.io.Serializable

data class User(var name:String, val email:String, val password:String, val id:String?= "", var token:String,var personalImage:Pic=Pic() ,var category: String, var subHead:String?="" ,var coverImage:Pic?=null) : Serializable

fun User.toRemote() = RemoteUser(name, email, id,token, personalImage.copy(picFileUri = ""), coverImage?.copy(picFileUri = "") ?: Pic(), category, subHead)