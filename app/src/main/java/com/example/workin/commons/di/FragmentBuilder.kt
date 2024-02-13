package com.example.workin.commons.di

import android.content.Context
import com.example.workin.commons.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Named


@Module
@InstallIn(FragmentComponent::class)
object FragmentBuilder {

    @Provides
    @FragmentScoped
    @Named(Constant.sharedFragment)
    fun getMainUserSharedPreference(@ApplicationContext context: Context) = context.getSharedPreferences(Constant.MainUser , Context.MODE_PRIVATE)
}