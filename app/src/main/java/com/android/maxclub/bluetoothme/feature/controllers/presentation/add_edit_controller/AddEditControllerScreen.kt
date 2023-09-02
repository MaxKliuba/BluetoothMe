package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalTextInputService
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.AddEditControllerBottomBar
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.AddEditControllerTopBar
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.WidgetList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditControllerScreen(
    onNavigateUp: () -> Unit,
    onNavigateToAddEditWidget: (id: UUID, isNew: Boolean) -> Unit,
    onDeleteWidget: (UUID) -> Unit,
    onDeleteController: (UUID) -> Unit,
    viewModel: AddEditControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val controller by remember {
        derivedStateOf { (state as? AddEditControllerUiState.Success)?.controller }
    }

    val inputService = LocalTextInputService.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is AddEditControllerUiAction.SetFocusToTitleTextField -> {
                    delay(100)
                    try {
                        focusRequester.requestFocus()
                        @Suppress("DEPRECATION") inputService?.hideSoftwareKeyboard()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            (state as? AddEditControllerUiState.Success)?.let { state ->
                AddEditControllerTopBar(
                    title = state.controllerTitle,
                    onTitleChange = viewModel::updateControllerTitle,
                    focusRequester = focusRequester,
                    withAccelerometer = state.controller.withAccelerometer,
                    onWithAccelerometerChange = viewModel::updateControllerWithAccelerometer,
                    withVoiceInput = state.controller.withVoiceInput,
                    onWithVoiceInputChange = viewModel::updateControllerWithVoiceInput,
                    withRefresh = state.controller.withRefresh,
                    onWithRefreshChange = viewModel::updateControllerWithRefresh,
                    onNavigateUp = onNavigateUp,
                )
            }
        },
        bottomBar = {
            (state as? AddEditControllerUiState.Success)?.let { state ->
                AddEditControllerBottomBar(
                    columnsCount = state.controller.columnsCount,
                    onColumnCountChange = viewModel::updateControllerColumnCount,
                    onDeleteController = {
                        controller?.let { onDeleteController(it.id) }
                        onNavigateUp()
                    },
                    onNavigateUp = onNavigateUp
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.let { state ->
                when (state) {
                    is AddEditControllerUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AddEditControllerUiState.Success -> {
                        WidgetList(
                            columnsCount = state.controller.columnsCount.count,
                            widgets = state.widgets,
                            onAddWidget = {
                                controller?.let { onNavigateToAddEditWidget(it.id, true) }
                            },
                            onEditWidget = { onNavigateToAddEditWidget(it, false) },
                            onChangeWidgetSize = viewModel::updateWidgetSize,
                            onChangeWidgetEnable = viewModel::updateWidgetEnable,
                            onDeleteWidget = onDeleteWidget
                        )
                    }
                }
            }
        }
    }
}