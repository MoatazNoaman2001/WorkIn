package com.example.workin.data.remote

import com.example.workin.domain.model.User
import java.io.Serializable

data class RemoteUser(
    var name: String,
    val email: String,
    val id: String? = "",
    val token: String,
    val hasPic: Boolean,
    val picCloudUri: String,
    val category: String
): Serializable{
    constructor() : this("" , "", "", "" ,false , "" , "")
}

fun RemoteUser.toUser() = User(name , email, "", id,token, hasPic, "" , picCloudUri, category)