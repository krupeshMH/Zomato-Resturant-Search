package com.restaurants.di

import android.app.Application
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.restaurants.BuildConfig
import com.restaurants.R
import okhttp3.Cache
import okhttp3.OkHttpClient
import io.reactivex.schedulers.Schedulers
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter


@Module
class AppModule {
    @Singleton
    @Provides
    internal fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

   @Provides
   @Singleton
   fun provideHttpCache(application: Application): Cache {
       val cacheSize = 10 * 1024 * 1024
       return Cache(application.cacheDir, cacheSize.toLong())
   }


    @Singleton
    @Provides
    internal fun provideRetrofitInstance(cache: Cache,gson: Gson): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val original = chain.request()

                // Customize the request
                val request = original.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .removeHeader("Pragma")
                    .header("Cache-Control", String.format("max-age=%s", BuildConfig.CACHETIME))
                    .build()

                val response = chain.proceed(request)
                response.cacheResponse()
                // Customize or return the response
                Log.d("API", "HomePresenter: $response")
                response
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(provideRxJavaCallAdapterFactory())
            .build()

    }

    /*@Provides
    @Singleton
    fun provideGSonConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }*/

    @Provides
    @Singleton
    fun provideRxJavaCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }


    @Singleton
    @Provides
    internal fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.placeholder)
            .error(R.drawable.placeholder)
    }

    @Singleton
    @Provides
    internal fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}