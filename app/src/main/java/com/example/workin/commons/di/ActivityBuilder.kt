package com.example.workin.commons.di

import android.content.Context
import com.example.workin.commons.Constant
import com.example.workin.commons.Constant.sharedActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Named


@Module
@InstallIn(ActivityComponent::class)
object ActivityBuilder {

    @Provides
    @ActivityScoped
    @Named(sharedActivity)
    fun getMainUserSharedPreference(@ApplicationContext context: Context) = context.getSharedPreferences(Constant.MainUser , Context.MODE_PRIVATE)

    @Provides
    @ActivityScoped
    @Named(Constant.ImgProfileActiviy)
    fun getProfilePicRef(auth:FirebaseAuth) = FirebaseStorage.getInstance().getReference(Constant.userStore).child(auth.currentUser?.uid!!).child(Constant.ImgProfile)
}