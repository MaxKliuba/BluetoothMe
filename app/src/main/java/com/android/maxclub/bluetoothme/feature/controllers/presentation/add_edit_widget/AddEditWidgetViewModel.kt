package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
) : ViewModel() {
    private val id: Int = savedStateHandle[Screen.AddEditWidget.ARG_ID]
        ?: Screen.AddEditWidget.DEFAULT_ID
    private val isNew: Boolean = savedStateHandle[Screen.AddEditWidget.ARG_IS_NEW]
        ?: Screen.AddEditWidget.DEFAULT_IS_NEW

    private val _uiState = mutableStateOf<AddEditWidgetUiState>(AddEditWidgetUiState.Loading)
    val uiState: State<AddEditWidgetUiState> = _uiState

    private val uiActionChannel = Channel<AddEditWidgetUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getWidgetsJob: Job? = null

    init {
        (_uiState.value as? AddEditWidgetUiState.Loading)?.let {
            getWidget()
        }
    }

    private fun getWidget() {
        getWidgetsJob?.cancel()
        getWidgetsJob = viewModelScope.launch {
            val widgetId = if (isNew) {
                val widget = Widget.Switch(
                    controllerId = id,
                    messageTag = "led",
                    title = "Led",
                    icon = WidgetIcon.HIGHLIGHT,
                )
                controllerRepository.addWidget(widget)
            } else {
                id
            }

            controllerRepository.getWidgetById(widgetId)
                .onStart {
                    _uiState.update { AddEditWidgetUiState.Loading }
                }
                .onEach { widget ->
                    _uiState.update { AddEditWidgetUiState.Success(widget = widget) }
                }
                .catch { it.printStackTrace() }
                .launchIn(this)
        }
    }
}