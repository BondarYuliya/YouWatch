package com.groupping.youwatch.screens.common.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

class NavigationState {
    val screenStack = MutableLiveData<List<Screen>>(listOf(Screen.MainScreen))
    val currentScreen: LiveData<Screen> = screenStack.map { it.last() }
}