package com.restaurants.di

import androidx.lifecycle.ViewModelProvider

import com.restaurants.viewmodels.ViewModelProviderFactory

import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory

}
