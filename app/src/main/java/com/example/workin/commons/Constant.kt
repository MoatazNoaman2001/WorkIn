package com.example.workin.commons

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object Constant {
    val MainUser = "Main_User"
    val Sumamry = "summary"
    val userStore = "Users"
    val ImgProfile= "profileImage"
    val ProfileCoverImg= "profileCoverImage"
    val email = "email"

    val birthDateFormate = "dd/MM/yyyy"

    const val sharedActivity = "SharedPreferenceForActivity"
    const val sharedFragment = "SharedPreferenceForFragment"
    const val ImgProfileActivity = "image_profile_ref_activity"
    const val ImgProfileFragment = "image_profile_ref_fragment"

    fun displayMessage(context: Context, title :String, message: String, positiveAction: DialogInterface.OnClickListener?
    , negativeAction:DialogInterface.OnClickListener? , dismissAction: DialogInterface.OnDismissListener){
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", positiveAction)
            .setNegativeButton("cancle" , negativeAction)
            .setOnDismissListener(dismissAction)
            .create()

        dialog.show()
    }
}