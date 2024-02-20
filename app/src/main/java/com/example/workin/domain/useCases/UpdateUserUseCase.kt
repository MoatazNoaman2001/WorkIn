package com.example.workin.domain.useCases

import com.example.workin.domain.model.User
import com.example.workin.domain.repo.MainUserRepoImpl
import com.example.workin.domain.repo.Resources
import javax.inject.Inject

class   UpdateUserUseCase @Inject constructor(val mainUserRepoImpl: MainUserRepoImpl) {
    operator fun invoke(user: User): Resources<User>? {
        return try {
            mainUserRepoImpl.updateUser(user)
        }catch (e:Exception){
            null
        }
    }
}