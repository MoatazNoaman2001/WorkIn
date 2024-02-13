package com.example.workin.commons

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.workin.R
import com.example.workin.data.remote.RemoteUser
import com.example.workin.data.remote.toUser
import com.example.workin.domain.model.Pic
import com.example.workin.domain.model.User
import com.example.workin.domain.model.toRemote
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

private const val TAG = "SigningLogistic"
object SigningLogistic {
    val emailRegex =
        "^[\\w!#\$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#\$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}\$"
    val passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%]).{8,20}\$"

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(emailRegex).matcher(email).matches()
    }

    fun isPasswordValid(email: String): Boolean {
        return Pattern.compile(passwordRegex).matcher(email).matches()
    }

    val exp_finder = mapOf(
        "ERROR_INVALID_CUSTOM_TOKEN" to R.string.error_login_custom_token,
        "ERROR_CUSTOM_TOKEN_MISMATCH" to R.string.error_login_custom_token_mismatch,
        "ERROR_INVALID_CREDENTIAL" to R.string.error_login_credential_malformed_or_expired,
        "ERROR_INVALID_EMAIL" to R.string.error_login_invalid_email,
        "ERROR_WRONG_PASSWORD" to R.string.error_login_wrong_password,
        "ERROR_USER_MISMATCH" to R.string.error_login_user_mismatch,
        "ERROR_REQUIRES_RECENT_LOGIN" to R.string.error_login_requires_recent_login,
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" to R.string.error_login_accounts_exits_with_different_credential,
        "ERROR_EMAIL_ALREADY_IN_USE" to R.string.error_login_email_already_in_use,
        "ERROR_CREDENTIAL_ALREADY_IN_USE" to R.string.error_login_credential_already_in_use,
        "ERROR_USER_DISABLED" to R.string.error_login_user_disabled,
        "ERROR_USER_TOKEN_EXPIRED" to R.string.error_login_user_token_expired,
        "ERROR_USER_NOT_FOUND" to R.string.error_login_user_not_found,
        "ERROR_INVALID_USER_TOKEN" to R.string.error_login_invalid_user_token,
        "ERROR_OPERATION_NOT_ALLOWED" to R.string.error_login_operation_not_allowed,
        "ERROR_WEAK_PASSWORD" to R.string.error_login_password_is_weak
    )

    fun FB_ExcpetionsHandler(e: Throwable?, context: Context) {
        when (e) {
            is IllegalArgumentException -> {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

            is FirebaseNetworkException -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.network_excep),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is SecurityException -> {
                Toast.makeText(context, "user is not verified", Toast.LENGTH_SHORT).show()
            }

            is FirebaseAuthException -> {
                val msg = exp_finder[(e.errorCode)]
                if (msg != null)
                    Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun createNewUser(
        name: String,
        email: String,
        password: String,
        preferences: SharedPreferences
    ): Result<String> {
        if (!isEmailValid(email)) return Result.failure(IllegalArgumentException("email not valid"))
        if (!isPasswordValid(password)) return Result.failure(IllegalArgumentException("password not valid"))
        return try {
            val user = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .await().user
            user?.apply {
                sendEmailVerification()
                val request = userProfileChangeRequest { displayName = name }
                user.updateProfile(request)
            }
            val mu = User(name, email, password, user?.uid , "", Pic() , "" , "")
            storeUserCloud(mu , preferences)
            Result.success("user created successfully")
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }

    suspend fun signInUser(
        email: String,
        password: String,
        preferences: SharedPreferences
    ): Result<User> {
        return try {
            val FBuser = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await().user
            Log.d(TAG, "signInUser: ${FBuser?.email}")
            if (FBuser?.isEmailVerified!!) {
                Log.d(TAG, "signInUser: Mail Verified")
                val user = doUser(FBuser , preferences)
                Result.success(user)
            } else {
                Result.failure(SecurityException("user is not verified"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /*i have here three cases to handle
    * 1. if user is creating new account for first time
    * 2. if user login from diff device
    * 3. if user delete application and install again
    * */
    suspend fun doUser(FBuser: FirebaseUser, preferences: SharedPreferences): User {
        var isExist = true
        var user = checkCurrentUser(preferences, FBuser)
        if (user == null || (user.email != FBuser.email)) isExist = false
        if (user == null) user = User(FBuser.displayName!! , FBuser.email!! , "" , FBuser.uid , token ="", Pic() , "" , "" )
        else { storeInPreference(preferences, user); isExist = true }
        if (!isExist) storeUserCloud(user, preferences)
        return user
    }


    suspend fun checkCurrentUser(preferences: SharedPreferences, fb_user: FirebaseUser? = null): User? {
        if (fb_user == null)
            return getUserFromPreference(preferences)
        return getUserCould(fb_user) ?: getUserFromPreference(preferences)
    }
    fun storeUserCloud(user: User, preferences: SharedPreferences) {
        FirebaseFirestore.getInstance().collection(Constant.userStore).document(user.id!!).set(user.toRemote()).addOnSuccessListener {
            storeInPreference(preferences , user)
        }
    }

    fun storeInPreference(preferences: SharedPreferences, user: User){
        preferences.edit().putString(Constant.MainUser , Gson().toJson(user)).apply()
    }

    private suspend fun getUserCould(fb_user: FirebaseUser): User? {
        return try {
            FirebaseFirestore.getInstance().collection(Constant.userStore).document(fb_user.uid)
                .get().await().toObject(RemoteUser::class.java)?.toUser()
        }catch (e:Exception){
            Log.d(TAG, "GetUserCould: error while read user from could: ${e.message}")
            null
        }
    }
    fun getUserFromPreference(preferences: SharedPreferences): User? {
        return Gson().fromJson(preferences.getString(Constant.MainUser, null), User::class.java)
    }

}