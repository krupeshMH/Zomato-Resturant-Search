package com.restaurants.di.main


import androidx.lifecycle.ViewModel
import com.restaurants.di.ViewModelKey
import com.restaurants.ui.main.RestaurantSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule
{
    @Binds
    @IntoMap
    @ViewModelKey(RestaurantSearchViewModel::class)
    abstract fun bindPostsViewModel(viewModel: RestaurantSearchViewModel): ViewModel
}