package com.restaurants.di.main

import com.restaurants.network.SearchAPI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    internal fun provideMainApi(retrofit: Retrofit): SearchAPI {
        return retrofit.create<SearchAPI>(SearchAPI::class.java!!)
    }
}