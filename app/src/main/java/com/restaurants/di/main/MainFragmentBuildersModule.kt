package com.restaurants.di.main


import com.restaurants.ui.main.RestaurantListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    internal abstract fun contributeResturantListFragment(): RestaurantListFragment

}
