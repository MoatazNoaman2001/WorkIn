package com.example.workin.data.remote

import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import java.io.Serializable
import java.util.Calendar
import java.util.Date

data class RemoteUser(
    var name: String,
    val email: String,
    val BirthDay: Date?,
    val phoneNumber: String,
    val id: String? = "",
    val token: String,
    val personalImage:Pic,
    val coverPic:Pic,
    val category: String,
    var subHead:String="",
): Serializable{
    constructor() : this("" , "", Date(),"", "","" , Pic() , Pic(), "")
}

fun RemoteUser.toUser() = User(name , email, "", BirthDay,phoneNumber,id,token, personalImage, category,subHead, coverPic)