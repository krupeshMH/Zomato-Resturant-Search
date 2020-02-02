package com.myglammtest.di

import com.myglammtest.di.main.MainFragmentBuildersModule
import com.myglammtest.di.main.MainModule
import com.myglammtest.di.main.MainScope
import com.myglammtest.di.main.MainViewModelsModule
import com.myglammtest.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [MainModule::class, MainViewModelsModule::class, MainFragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}