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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.toWidgetType
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.AddEditWidgetTextField
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.AddEditWidgetTopBar
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.SliderWidgetParamsSection
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.WidgetIconList
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.WidgetPreviewGrid
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components.WidgetTypeDropdownMenu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWidgetScreen(
    onNavigateUp: () -> Unit,
    onDeleteWidget: (Int) -> Unit,
    viewModel: AddEditWidgetViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val isConfigVisible by remember {
        derivedStateOf { (state as? AddEditWidgetUiState.Success)?.widget !is Widget.Empty }
    }

    val scope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AddEditWidgetTopBar(
                onDeleteWidget = (state as? AddEditWidgetUiState.Success)?.let {
                    {
                        scope.launch {
                            onDeleteWidget(it.widget.id)
                            delay(150)
                            onNavigateUp()
                        }
                    }
                },
                onNavigateUp = onNavigateUp,
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
                                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
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

                                if (isConfigVisible) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    AddEditWidgetTextField(
                                        value = state.widgetTitle,
                                        onValueChange = viewModel::tryUpdateWidgetTitle,
                                        label = stringResource(R.string.widget_title_label),
                                        capitalization = KeyboardCapitalization.Sentences,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    AddEditWidgetTextField(
                                        value = state.widgetTag,
                                        onValueChange = viewModel::tryUpdateWidgetTag,
                                        label = stringResource(R.string.message_tag_label),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            if (isConfigVisible) {
                                WidgetIconList(
                                    selectedWidgetIcon = state.widget.icon,
                                    widgetIcons = WidgetIcon.values(),
                                    onSelectWidgetIcon = {
                                        viewModel.updateWidgetIcon(state.widget, it)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (state.widget is Widget.Slider) {
                                SliderWidgetParamsSection(
                                    sliderWidget = state.widget,
                                    onRangeValueChange = viewModel::updateSliderWidgetRange,
                                    onStepValueChange = viewModel::updateSliderWidgetStep,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}