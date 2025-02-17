package com.groupping.youwatch.screens.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleObserving(lifecycleOwner: LifecycleOwner, eventsList: List<Pair<Lifecycle.Event, () -> Unit>>) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            eventsList.forEach { (expectedEvent, action) ->
                if (event == expectedEvent) {
                    action()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}