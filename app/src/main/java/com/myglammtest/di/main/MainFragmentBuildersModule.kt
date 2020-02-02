package com.myglammtest.di.main


import com.myglammtest.ui.main.RestaurantListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    internal abstract fun contributeResturantListFragment(): RestaurantListFragment

}
