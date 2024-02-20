package com.example.workin.domain.model

import android.os.Build
import com.example.workin.data.remote.RemoteUser
import java.io.Serializable
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.TemporalAccessor
import java.util.Calendar
import java.util.Date

data class User(
    var name: String,
    val email: String,
    val password: String,
    val BirthDay: Date?,
    val phoneNumber: String,
    val id: String? = "",
    var token: String,
    var personalImage: Pic = Pic(),
    var category: String,
    var subHead: String = "",
    var coverImage: Pic = Pic()
) : Serializable {
    val age: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Period.between(
                BirthDay?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                LocalDate.now()
            ).years
        else Date(BirthDay?.time?.minus(Calendar.getInstance().time.time) ?: 0).year
    constructor() : this("", "", "", Date(), "", "", "", Pic(), "")
}

fun User.toRemote() = RemoteUser(
    name,
    email,
    BirthDay,
    phoneNumber,
    id,
    token,
    personalImage.copy(picFileUri = ""),
    coverImage.copy(picFileUri = ""),
    category,
    subHead
)