package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ControllersScreen(
    onOpenNavigationDrawer: () -> Unit,
    viewModel: ControllersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    LazyColumn {
        items(
            items = state.controllersWithWidgetCount
        ) { controllerWithWidgetCount ->
            Text(text = "${controllerWithWidgetCount.controller.title} #${controllerWithWidgetCount.controller.id} (${controllerWithWidgetCount.widgetCount})")
        }
    }
}