package com.example.workin.domain.model

import android.location.Location
import java.io.Serializable

data class Summary(
    val bio:String,
    val location:String,
    val connectionNumber:Long= 0L,
    val site1:String= "",
    val site2:String= "",
): Serializable {
    constructor() : this("" , "")
}