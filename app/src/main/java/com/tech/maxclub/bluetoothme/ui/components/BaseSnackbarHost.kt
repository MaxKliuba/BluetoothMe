package com.tech.maxclub.bluetoothme.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun BaseSnackbarHost(
    hostState: SnackbarHostState,
) {
    SnackbarHost(hostState = hostState) { data ->
        Snackbar(
            actionColor = MaterialTheme.colorScheme.primaryContainer,
            snackbarData = data,
        )
    }
}