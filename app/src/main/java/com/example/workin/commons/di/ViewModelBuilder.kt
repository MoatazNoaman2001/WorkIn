package com.example.workin.commons.di

import android.content.Context
import com.example.workin.commons.Constant
import com.example.workin.commons.Constant.sharedActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceViewModelScope
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceSummaryViewModelScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PicStorageViewModeScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PicCoverStorageViewModeScope
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SummaryFireStoreViewModeScope
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelBuilder {

    @Provides
    @ViewModelScoped
    @PreferenceViewModelScope
    fun getMainUserSharedPreference(@ApplicationContext context: Context) = context.getSharedPreferences(Constant.MainUser , Context.MODE_PRIVATE)
    @Provides
    @ViewModelScoped
    @PreferenceSummaryViewModelScope
    fun getSummarySharedPreference(@ApplicationContext context: Context) = context.getSharedPreferences(Constant.Sumamry , Context.MODE_PRIVATE)

    @Provides
    @ViewModelScoped
    @PicStorageViewModeScope
    fun getProfilePicRef(auth:FirebaseAuth) :StorageReference = FirebaseStorage.getInstance().getReference(Constant.userStore).child(auth.currentUser?.uid?:"null").child(Constant.ImgProfile)

    @Provides
    @ViewModelScoped
    @SummaryFireStoreViewModeScope
    fun getSummaryRef(auth:FirebaseAuth) :DocumentReference = FirebaseFirestore.getInstance().collection(Constant.userStore).document(auth.currentUser?.uid?:"null").collection(Constant.Sumamry).document(Constant.Sumamry)

    @Provides
    @ViewModelScoped
    @PicCoverStorageViewModeScope
    fun getProfileCoverPicRef(auth:FirebaseAuth) :StorageReference = FirebaseStorage.getInstance().getReference(Constant.userStore).child(auth.currentUser?.uid?:"null").child(Constant.ProfileCoverImg)
}