package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.session.SessionStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StateModule {

    @Provides
    @Singleton
    fun provideSessionStateHolder(): SessionStateHolder = SessionStateHolder()
}
