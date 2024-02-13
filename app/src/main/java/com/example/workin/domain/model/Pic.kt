package com.example.workin.domain.model

import java.io.Serializable

data class Pic(var hasPic: Boolean, var picFileUri:String, var picCloudUri:String) :Serializable{
    constructor(): this(false , "" , "")
}