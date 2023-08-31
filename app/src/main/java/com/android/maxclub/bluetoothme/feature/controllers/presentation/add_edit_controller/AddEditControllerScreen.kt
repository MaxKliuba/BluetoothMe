package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.AddNewWidgetItem
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.TitleTextField
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.EmptyWidget
import kotlinx.coroutines.delay
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditControllerScreen(
    onNavigateUp: () -> Unit,
    onDeleteController: (UUID) -> Unit,
    viewModel: AddEditControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    val inputService = LocalTextInputService.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        delay(100) // TODO
        focusRequester.requestFocus()
        inputService?.hideSoftwareKeyboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    (state as? AddEditControllerUiState.Success)?.let {
                        TitleTextField(
                            value = it.controllerTitle,
                            onValueChange = viewModel::updateControllerTitle,
                            modifier = Modifier.focusRequester(focusRequester),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                        )
                    }
                },
                actions = {
                    (state as? AddEditControllerUiState.Success)?.let { state ->
                        FilledIconToggleButton(
                            checked = state.controller.withAccelerometer,
                            onCheckedChange = viewModel::updateControllerWithAccelerometer,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = stringResource(R.string.with_accelerometer_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withVoiceInput,
                            onCheckedChange = viewModel::updateControllerWithVoiceInput,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = stringResource(R.string.with_voice_input_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withRefresh,
                            onCheckedChange = viewModel::updateControllerWithRefresh,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_get_refresh_state_24),
                                contentDescription = stringResource(R.string.with_refresh_button),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            (state as? AddEditControllerUiState.Success)?.let {
                BottomAppBar(
                    actions = {
                        IconButton(
                            onClick = {
                                onDeleteController(it.controller.id)
                                onNavigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(id = R.string.delete_controller_button)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.updateControllerColumnCount(
                                    (it.controller.columnsCount + 1) % 2 + 2
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (it.controller.columnsCount) {
                                        2 -> R.drawable.ic_two_column_24
                                        else -> R.drawable.ic_three_column_24
                                    }
                                ),
                                contentDescription = stringResource(R.string.columns_count_button)
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.apply_changes_button),
                            )
                        }
                    }
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
                    is AddEditControllerUiState.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = state.controller.columnsCount),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.widgets,
                                key = { it.id },
                                span = {
                                    GridItemSpan(
                                        minOf(
                                            it.size.span,
                                            state.controller.columnsCount
                                        )
                                    )
                                },
                            ) { widget ->
                                when (widget) {
                                    is Widget.Empty -> EmptyWidget(
                                        widget = widget,
                                        columnsCount = state.controller.columnsCount,
                                        onChangeSize = viewModel::updateWidgetSize,
                                        onDelete = viewModel::deleteWidget,
                                        onEdit = { /*TODO*/ },
                                    )

                                    is Widget.Button -> ButtonWidget(
                                        widget = widget,
                                        columnsCount = state.controller.columnsCount,
                                        onChangeSize = viewModel::updateWidgetSize,
                                        onChangeReadOnly = viewModel::updateWidgetReadOnly,
                                        onDelete = viewModel::deleteWidget,
                                        onEdit = { /*TODO*/ },
                                    )
                                }
                            }

                            item {
                                AddNewWidgetItem(
                                    onClick = {
                                        /* TODO */
                                        viewModel.addWidget(
                                            Widget.Button(
                                                controllerId = state.controller.id,
                                                messageTag = "led",
                                                title = "Test",
                                                size = WidgetSize.SMALL,
                                                readOnly = false,
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