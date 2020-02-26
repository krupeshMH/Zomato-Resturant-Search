package com.restaurants.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.restaurants.component.LocationLiveData
import com.restaurants.network.SearchAPI
import com.restaurants.ui.Resource
import com.restaurants.util.Constants
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import io.reactivex.disposables.Disposable
import io.reactivex.android.schedulers.AndroidSchedulers
import com.restaurants.BuildConfig
import com.restaurants.models.response.RestaurantsResponse


class RestaurantSearchViewModel @Inject constructor(application: Application, api: SearchAPI) :
    ViewModel() {


    private val locationData = LocationLiveData(application)

    private val searchApi = api

    fun getLocationData() = locationData


    var listRestaurant: MutableLiveData<Resource<RestaurantsResponse>> = MutableLiveData()
    private var searchDisposable: Disposable? = null


    fun callApi(
        text: String,
        latitude: Double,
        longitude: Double
    ) {
        val map = mapOf("user-key" to BuildConfig.API_KEY)
        listRestaurant.setValue(Resource.loading(null))
        searchDisposable = searchApi.search(map,
            text,
            latitude, longitude,
            Constants.SORT_TYPE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ searchResponse ->
                if (searchResponse != null) {
                    listRestaurant.setValue(Resource.success(searchResponse))
                }
            }, { throwable ->
                listRestaurant.setValue(Resource.error("Something went wrong!!!", null))

            })

    }

    override fun onCleared() {
        super.onCleared()
        searchDisposable?.dispose()
    }
}