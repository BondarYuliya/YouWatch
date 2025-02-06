package com.groupping.youwatch.screens.common

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class NavigationViewModel @Inject constructor(private val navigationState: NavigationState) :
    ViewModel() {

    fun navigateTo(screen: Screen) {
        navigationState.screenStack.value = navigationState.screenStack.value.orEmpty() + screen
    }

    fun goBack() {
        val currentStack = navigationState.screenStack.value.orEmpty()
        if (currentStack.size > 1) {
            navigationState.screenStack.value = currentStack.dropLast(1)
        }
    }
}