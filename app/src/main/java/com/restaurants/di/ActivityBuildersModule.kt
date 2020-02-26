package com.restaurants.di

import com.restaurants.di.main.MainFragmentBuildersModule
import com.restaurants.di.main.MainModule
import com.restaurants.di.main.MainScope
import com.restaurants.di.main.MainViewModelsModule
import com.restaurants.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [MainModule::class, MainViewModelsModule::class, MainFragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}