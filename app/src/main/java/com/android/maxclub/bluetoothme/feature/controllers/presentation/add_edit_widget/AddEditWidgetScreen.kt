package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.toWidgetType
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.AddEditWidgetTextField
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.WidgetPreviewGrid
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.WidgetTypeDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWidgetScreen(
    onNavigateUp: () -> Unit,
    onDeleteWidget: (Int) -> Unit,
    viewModel: AddEditWidgetViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val verticalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                        )
                    }
                },
                actions = {
                    (state as? AddEditWidgetUiState.Success)?.let {
                        IconButton(
                            onClick = {
                                onDeleteWidget(it.widget.id)
                                onNavigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete_widget_button),
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done_button),
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
                    is AddEditWidgetUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AddEditWidgetUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(verticalScrollState)
                        ) {
                            WidgetPreviewGrid(
                                columnsCount = state.columnsCount,
                                widget = state.widget,
                                onChangeWidgetSize = viewModel::updateWidgetSize,
                                onChangeWidgetEnable = viewModel::updateWidgetEnable,
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 20.dp)
                            ) {
                                Divider()

                                Spacer(modifier = Modifier.height(20.dp))

                                WidgetTypeDropdownMenu(
                                    selectedWidgetType = state.widget.toWidgetType(),
                                    widgetTypes = WidgetType.values(),
                                    onSelectWidgetType = {
                                        viewModel.updateWidgetType(state.widget, it)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (state.widget !is Widget.Empty) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    AddEditWidgetTextField(
                                        value = state.widgetTitle,
                                        onValueChange = viewModel::tryUpdateWidgetTitle,
                                        label = stringResource(R.string.widget_title_label),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    AddEditWidgetTextField(
                                        value = state.widgetTag,
                                        onValueChange = viewModel::tryUpdateWidgetTag,
                                        label = stringResource(R.string.message_tag_label),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // TODO
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}