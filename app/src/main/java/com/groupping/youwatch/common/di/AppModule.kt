package com.groupping.youwatch.common.di

import android.app.Application
import com.groupping.youwatch.App
import com.groupping.youwatch.business_logic.channels.YouTubeChannelDao
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video_groups.DirectoryDao
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.common.database.AppDataBase
import com.groupping.youwatch.common.network.RetrofitClient
import com.groupping.youwatch.common.network.YouTubeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @AppScope
    fun myApp(application: Application): App{
        return application as App
    }

    @Provides
    @AppScope
    fun appDataBase(app: App): AppDataBase{
        return AppDataBase.getDatabase(app)
    }

    @Provides
    @AppScope
    fun youtubeChannelsDao(appDataBase: AppDataBase): YouTubeChannelDao{
        return appDataBase.youtubeChannelsDao()
    }

    @Provides
    @AppScope
    fun directoryDao(appDataBase: AppDataBase): DirectoryDao{
        return appDataBase.directoryDao()
    }

    @Provides
    @AppScope
    fun videoItemsDao(appDataBase: AppDataBase): VideoItemsDao{
        return appDataBase.videoItemsDao()
    }

    @Provides
    @AppScope
    fun videoWatchingHistoryDao(appDataBase: AppDataBase): VideoWatchHistoryDao{
        return appDataBase.videoWatchHistoryDao()
    }

    @Provides
    @AppScope
    fun navigationState(): NavigationState {
        return NavigationState()
    }

    @Provides
    @AppScope
    fun youtubeAPI(retrofitClient: RetrofitClient): YouTubeApi{
        return retrofitClient.getYoutubeAPI()
    }

    @Provides
    @AppScope
    fun retrofitClient(): RetrofitClient{
        return RetrofitClient()
    }
}