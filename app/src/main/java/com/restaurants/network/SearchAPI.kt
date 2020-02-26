package com.restaurants.network

import com.restaurants.models.response.RestaurantsResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface SearchAPI {
    @GET("api/v2.1/search")
    fun search(
        @HeaderMap apiKey:Map<String, String>,
        @Query("q") searchText: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("sort") sortBy: String
    ): Flowable<RestaurantsResponse>

}