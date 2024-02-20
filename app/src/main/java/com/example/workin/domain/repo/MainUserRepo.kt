package com.example.workin.domain.repo

import com.example.workin.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MainUserRepo {
    fun createUser(): Resources<User>
    fun updateUser(user: User): Resources<User>
    fun getUser(user:(Resources<User>)->Unit)
    fun inspectUser()

}


sealed class Resources<T>(data: T? = null, message: String? = null) {
    data class success<T>(val data: T) : Resources<T>(data)
    data class failed<T>(val message: String) : Resources<T>(message = message)
    class loading<T>() : Resources<T>()
}