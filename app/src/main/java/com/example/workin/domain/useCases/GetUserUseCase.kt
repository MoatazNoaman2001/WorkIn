package com.example.workin.domain.useCases

import android.net.http.HttpException
import com.example.workin.domain.model.User
import com.example.workin.domain.repo.MainUserRepoImpl
import com.example.workin.domain.repo.Resources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import javax.inject.Inject

class GetUserUseCase @Inject constructor(val mainUserRepoImpl: MainUserRepoImpl) {
    private val _user = MutableStateFlow<Resources<User>>(Resources.loading())
    fun launch(){
        mainUserRepoImpl.inspectUser()
        mainUserRepoImpl.getUser {user->
            CoroutineScope(Dispatchers.IO).launch {
                _user.emit(user)
            }
        }
    }
    operator fun invoke() = _user
}