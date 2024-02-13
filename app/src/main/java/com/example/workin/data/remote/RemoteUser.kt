package com.example.workin.data.remote

import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import java.io.Serializable

data class RemoteUser(
    var name: String,
    val email: String,
    val id: String? = "",
    val token: String,
    val personalImage:Pic,
    val coverPic:Pic,
    val category: String,
    var subHead:String?="",
): Serializable{
    constructor() : this("" , "", "", "" , Pic() , Pic(), "")
}

fun RemoteUser.toUser() = User(name , email, "", id,token, personalImage, category,subHead, coverPic)