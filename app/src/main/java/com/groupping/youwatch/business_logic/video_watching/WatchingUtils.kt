package com.groupping.youwatch.business_logic.video_watching

import javax.inject.Inject

class WatchingUtils @Inject constructor() {
    fun getWatchedPercent(history: VideoWatchHistory, duration: Int): Double? {
        val allWatchedSeconds: Set<Int> = history.videoWatchHistoryItems
            .flatMap { it.watchedSeconds }
            .toSet()
        if (duration != 0) {
                return allWatchedSeconds.size.toDouble() / duration
        }
        return null
    }
}