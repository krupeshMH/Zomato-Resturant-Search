package com.myglammtest.di.main


import androidx.lifecycle.ViewModel
import com.myglammtest.di.ViewModelKey
import com.myglammtest.ui.main.RestaurantSearchViewModel
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