package com.example.workin.commons.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppBuilder {

    @Provides
    @Singleton
    fun firebaseAuth() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Provides
    @Singleton
    fun firebaseUser(auth: FirebaseAuth) : FirebaseUser?{
        return auth.currentUser
    }

}