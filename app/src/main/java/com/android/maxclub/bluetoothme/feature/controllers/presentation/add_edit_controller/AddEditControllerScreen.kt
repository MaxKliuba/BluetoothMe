package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.AddNewWidgetItem
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.TitleTextField
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.WidgetItem

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditControllerScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddEditControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    val inputService = LocalTextInputService.current
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    state.let { state ->
                        if (state is AddEditControllerUiState.Success) {
                            TitleTextField(
                                value = state.controllerTitle,
                                onValueChange = viewModel::onTitleChange,
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .onGloballyPositioned {
                                        if (!state.isTitleFocused) {
                                            focusRequester.requestFocus()
                                            inputService?.hideSoftwareKeyboard()
                                            viewModel.onTitleFocused()
                                        }
                                    },
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close_button),
                        )
                    }
                },
                actions = {
                    (state as? AddEditControllerUiState.Success)?.let { state ->
                        FilledIconToggleButton(
                            checked = state.controller.withAccelerometer,
                            onCheckedChange = viewModel::onChangeWithAccelerometer,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = stringResource(R.string.with_accelerometer_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withVoiceInput,
                            onCheckedChange = viewModel::onChangeWithVoiceInput,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = stringResource(R.string.with_voice_input_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withRefresh,
                            onCheckedChange = viewModel::onChangeWithRefresh,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.with_refresh_button),
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.applyChanges()
                    onNavigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.apply_changes_button),
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            state.let { state ->
                when (state) {
                    is AddEditControllerUiState.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = 2),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            items(
                                items = state.widgets,
                                key = { it.id }
                            ) { widget ->
                                WidgetItem(
                                    widget = widget,
                                    onClick = { /*TODO*/ }
                                )
                            }

                            item {
                                AddNewWidgetItem(
                                    onClick = {
                                        viewModel.addWidget(
                                            Widget(
                                                controllerId = state.controller.id,
                                                title = "New Widget",
                                                position = state.widgets.size,
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    is AddEditControllerUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}