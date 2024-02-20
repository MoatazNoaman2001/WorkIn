package com.example.workin.domain.repo

import android.content.SharedPreferences
import android.util.Log
import com.example.workin.commons.Constant
import com.example.workin.commons.SigningLogistic
import com.example.workin.commons.di.PicStorageViewModeScope
import com.example.workin.commons.di.PreferenceViewModelScope
import com.example.workin.data.remote.RemoteUser
import com.example.workin.data.remote.toUser
import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import com.example.workin.domain.model.toRemote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainUserRepoImpl"

class MainUserRepoImpl @Inject constructor(
    @PreferenceViewModelScope private val sharedPreferences: SharedPreferences,
    @PicStorageViewModeScope private val storageReference: StorageReference,
    private val auth: FirebaseAuth,
    private val firestore: CollectionReference
) : MainUserRepo {
    private var user = lazy {
        auth.currentUser
    }
    private var currentUser: ((Resources<User>) -> Unit)? = null

    override fun createUser(): Resources<User> {
        return try {
            val user = User()
            SigningLogistic.storeUserCloud(user, preferences = sharedPreferences)
            Resources.success(user)
        } catch (e: Exception) {
            Resources.failed(e.message!!)
        }
    }

    override fun updateUser(user: User): Resources<User> {
        storeUserCloud(user, firestore)
        val request = userProfileChangeRequest {
            displayName = user.name
        }
        auth.currentUser?.updateProfile(request)
        return Resources.success(user)
    }

    override fun getUser(user: (Resources<User>) -> Unit) {
        this@MainUserRepoImpl.currentUser = user
    }

    override fun inspectUser() {
        val auth_user = auth.currentUser
        if (auth_user != null) {
            val pre_user = getUserFromPreference()
            if (pre_user == null)
                currentUser?.invoke(Resources.loading())
            else
                currentUser?.invoke(Resources.success(pre_user))
            firestore.document(auth_user.uid).addSnapshotListener { value, error ->
                if (error == null) {
                    if (value?.exists()!!){
                        val user = value.toObject(RemoteUser::class.java)?.toUser()!!
                        storeInPreference(user)
                        Log.d(TAG, "inspectUser: $user")
                        currentUser?.invoke(Resources.success(user))
                    }else{
                        currentUser?.invoke(Resources.success(User()))
                    }
                } else {
                    currentUser?.invoke(Resources.failed(message = error.message!!))
                }
            }
        }
    }

    private fun storeUserCloud(
        user: User,
        firestore: CollectionReference
    ) {
        firestore.document(user.id!!).set(user.toRemote()).addOnSuccessListener {
            storeInPreference( user)
        }
    }

    private fun storeInPreference(user: User) {
        sharedPreferences.edit().putString(Constant.MainUser, Gson().toJson(user)).apply()
    }

    fun getUserFromPreference(): User? {
        return Gson().fromJson(sharedPreferences.getString(Constant.MainUser, null), User::class.java)
    }


}