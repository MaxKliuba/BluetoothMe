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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.AddNewWidgetItem
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.TitleTextField
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.ButtonWidget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components.widgets.EmptyWidget
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

    val inputService = LocalTextInputService.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is AddEditControllerUiAction.SetFocusToTitleTextField -> {
                    delay(100)
                    focusRequester.requestFocus()
                    @Suppress("DEPRECATION") inputService?.hideSoftwareKeyboard()
                }
            }
        }
    }

    val onControllerTitleChange: (TextFieldValue) -> Unit = remember {
        { viewModel.updateControllerTitle(it) }
    }
    val onControllerWithAccelerometerChange: (Boolean) -> Unit = remember {
        { viewModel.updateControllerWithAccelerometer(it) }
    }
    val onControllerWithVoiceInputChange: (Boolean) -> Unit = remember {
        { viewModel.updateControllerWithVoiceInput(it) }
    }
    val onControllerWithRefreshChange: (Boolean) -> Unit = remember {
        { viewModel.updateControllerWithRefresh(it) }
    }
    val onChangeWidgetSize: (UUID, WidgetSize) -> Unit = remember {
        { widgetId, newSize ->
            viewModel.updateWidgetSize(widgetId, newSize)
        }
    }
    val onChangeWidgetEnable: (UUID, Boolean) -> Unit = remember {
        { widgetId, enabled ->
            viewModel.updateWidgetEnable(widgetId, enabled)
        }
    }
    val onEditWidget: (UUID) -> Unit = remember {
        { widgetId ->
            onNavigateToAddEditWidget(widgetId, false)
        }
    }
    val onAddWidget: () -> Unit = remember {
        {
            (state as? AddEditControllerUiState.Success)?.let {
                onNavigateToAddEditWidget(it.controller.id, true)
            }
        }
    }
    val onChangeControllerColumns: () -> Unit = remember {
        {
            (state as? AddEditControllerUiState.Success)?.let {
                viewModel.updateControllerColumnCount(it.controller.columnsCount.next())
            }
        }
    }
    val onDeleteControllerAndLeave: () -> Unit = remember {
        {
            (state as? AddEditControllerUiState.Success)?.let {
                onDeleteController(it.controller.id)
                onNavigateUp()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    (state as? AddEditControllerUiState.Success)?.let {
                        TitleTextField(
                            value = it.controllerTitle,
                            onValueChange = onControllerTitleChange,
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
                            onCheckedChange = onControllerWithAccelerometerChange,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = stringResource(R.string.with_accelerometer_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withVoiceInput,
                            onCheckedChange = onControllerWithVoiceInputChange,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = stringResource(R.string.with_voice_input_button),
                            )
                        }

                        FilledIconToggleButton(
                            checked = state.controller.withRefresh,
                            onCheckedChange = onControllerWithRefreshChange,
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
                        IconButton(onClick = onDeleteControllerAndLeave) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(id = R.string.delete_controller_button)
                            )
                        }

                        IconButton(onClick = onChangeControllerColumns) {
                            Icon(
                                painter = painterResource(
                                    id = when (it.controller.columnsCount) {
                                        ControllerColumns.TWO -> R.drawable.ic_two_column_24
                                        ControllerColumns.THREE -> R.drawable.ic_three_column_24
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
                                contentDescription = stringResource(R.string.done_button),
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
                    is AddEditControllerUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AddEditControllerUiState.Success -> {
                        val columnsCount = state.controller.columnsCount.count

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = columnsCount),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.widgets,
                                key = { it.id },
                                span = { GridItemSpan(minOf(it.size.span, columnsCount)) },
                            ) { widget ->
                                when (widget) {
                                    is Widget.Empty -> EmptyWidget(
                                        widget = widget,
                                        columnsCount = columnsCount,
                                        onChangeSize = onChangeWidgetSize,
                                        onDelete = onDeleteWidget,
                                        onEdit = onEditWidget,
                                    )

                                    is Widget.Button -> ButtonWidget(
                                        widget = widget,
                                        columnsCount = columnsCount,
                                        onChangeSize = onChangeWidgetSize,
                                        onEnabledChange = onChangeWidgetEnable,
                                        onDelete = onDeleteWidget,
                                        onEdit = onEditWidget,
                                    )
                                }
                            }

                            item {
                                AddNewWidgetItem(onAdd = onAddWidget)
                            }
                        }
                    }
                }
            }
        }
    }
}