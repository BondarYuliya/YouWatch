package com.groupping.youwatch.common.di

import com.groupping.youwatch.common.network.YouTubeApi
import com.groupping.youwatch.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @AppScope
    fun provideApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val newUrl = original.url.newBuilder()
                .addQueryParameter("key", BuildConfig.YOUTUBE_API_KEY)
                .build()
            val newRequest = original.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(
        apiKeyInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @AppScope
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @AppScope
    fun provideYouTubeApi(retrofit: Retrofit): YouTubeApi {
        return retrofit.create(YouTubeApi::class.java)
    }
}
