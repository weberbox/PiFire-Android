package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.landing.data.api.LandingApi
import com.weberbox.pifire.setup.data.api.SetupApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideLandingApi(retrofit: Retrofit): LandingApi =
        retrofit.create(LandingApi::class.java)

    @Provides
    @Singleton
    fun providesSetupApi(retrofit: Retrofit): SetupApi =
        retrofit.create(SetupApi::class.java)
}