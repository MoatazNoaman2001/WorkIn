package com.example.workin.commons.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.workin.commons.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun imageLoader(@ApplicationContext context: Context): RequestManager{
        return Glide.with(context)
    }

    @Provides
    @Singleton
    fun usersStorageProvider() = FirebaseStorage.getInstance().getReference(Constant.userStore)

    @Singleton
    @Provides
    fun usersStore() : CollectionReference = FirebaseFirestore.getInstance().collection(Constant.userStore)

    @Provides
    @Singleton
    fun firebaseUser(auth: FirebaseAuth) : FirebaseUser?{
        return auth.currentUser
    }

}