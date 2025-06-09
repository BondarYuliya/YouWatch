package com.groupping.youwatch.common.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryItem

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromVideoWatchHistoryItems(items: List<VideoWatchHistoryItem>): String {
        println("Observing history: ${gson.toJson(items)}")
        return gson.toJson(items)
    }

    @TypeConverter
    fun toVideoWatchHistoryItems(data: String): List<VideoWatchHistoryItem> {
        val listType = object : TypeToken<List<VideoWatchHistoryItem>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toLongList(data: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIntList(data: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(data, listType)
    }
}