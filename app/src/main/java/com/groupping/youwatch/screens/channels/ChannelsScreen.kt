package com.groupping.youwatch.screens.channels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.business_logic.channels.DatabaseChannelDetails
import com.groupping.youwatch.screens.common.AddingButton
import com.groupping.youwatch.screens.common.Screen

@Composable
fun ChannelsScreen() {
    val viewModel: ChannelsScreenViewModel = viewModel()
    val channels by viewModel.channels.observeAsState(arrayListOf())
    val isDialogShown by viewModel.isDialogShown.observeAsState(false)
    val channelId by viewModel.channelId.observeAsState(TextFieldValue(""))

    ChannelsScreenMain(
        channels = channels,
        onAddButtonClicked = { viewModel.setDialogVisibility(true) },
        onChannelItemClicked = { clickedChannel ->
            viewModel.navigateTo(
                Screen.VideoListScreen(
                    clickedChannel.databaseChannelId,
                    clickedChannel.channelDetails.channelId
                )
            )
        })

    if (isDialogShown) {
        AddingChannelDialog(
            channelId = channelId,
            onDialogDismissed = { viewModel.setDialogVisibility(false) },
            onAddChannel = { channelId -> viewModel.onAddChannelConfirmed(channelId) },
            onValueChanged = { channelId -> viewModel.onChannelIdChanged(channelId) }
        )
    }

    BackHandler {
        viewModel.goBack()
    }
}

@Composable
fun ChannelsScreenMain(
    channels: List<DatabaseChannelDetails>,
    onAddButtonClicked: () -> Unit,
    onChannelItemClicked: (clickedChannel: DatabaseChannelDetails) -> Unit
) {
    AddingButton(
        text = "Add Channel",
        onAddButtonClicked = { onAddButtonClicked() },
        content = { padding ->
            Box(Modifier.padding(padding)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(channels.size) { index ->
                        val channel = channels[index]
                        ChannelItem(channel) { clickedChannel -> onChannelItemClicked(clickedChannel) }
                    }
                }
            }
        }
    )
}

@Composable
fun AddingChannelDialog(
    channelId: TextFieldValue,
    onDialogDismissed: () -> Unit,
    onAddChannel: (String) -> Unit,
    onValueChanged: (TextFieldValue) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDialogDismissed() },
        title = { Text("Add New Channel") },
        text = {
            Column {
                Text("Enter YouTube Channel ID:")
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = channelId,
                    onValueChange = { onValueChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (channelId.text.isEmpty()) {
                            Text("Channel ID", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddChannel(channelId.text)
                    onDialogDismissed()
                    onValueChanged(TextFieldValue(""))
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDialogDismissed() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChannelItem(channel: DatabaseChannelDetails, onItemClicked: (channel: DatabaseChannelDetails) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClicked(channel) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(channel.channelDetails.thumbnailUrl),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        ) {
            Text(channel.channelDetails.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

