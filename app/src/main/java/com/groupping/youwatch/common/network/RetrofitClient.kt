package com.groupping.youwatch.common.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

class RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getYoutubeAPI(): YouTubeApi{
        return retrofit.create(YouTubeApi::class.java)
    }
}