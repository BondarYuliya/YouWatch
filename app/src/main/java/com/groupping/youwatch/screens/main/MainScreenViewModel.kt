package com.groupping.youwatch.screens.main

import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.screens.common.navigation.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    navigationState: NavigationState,
) : NavigationViewModel(navigationState)