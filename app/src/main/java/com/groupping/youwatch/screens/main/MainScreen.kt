package com.groupping.youwatch.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.screens.common.Screen

@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = viewModel()
    MainScreenMain(
        onDirectoryClicked = { viewModel.navigateTo(Screen.DirectoryScreen) },
        onChannelsClicked = { viewModel.navigateTo(Screen.ChannelsScreen) },
    )
    BackHandler {
        viewModel.goBack()
    }
}

@Composable
fun MainScreenMain(onDirectoryClicked: () -> Unit, onChannelsClicked: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.LightGray)
                .clickable { onDirectoryClicked() },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "Directories",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.DarkGray)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .clickable { onChannelsClicked() },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "Channels",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
