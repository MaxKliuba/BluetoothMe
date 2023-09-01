package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditWidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
) : ViewModel() {
    private val id: UUID = savedStateHandle.get<String>(Screen.AddEditWidget.ARG_ID)?.let {
        UUID.fromString(it)
    } ?: UUID.randomUUID()
    private val isNew: Boolean = savedStateHandle[Screen.AddEditWidget.ARG_IS_NEW] ?: true

    private val _uiState = mutableStateOf<AddEditWidgetUiState>(AddEditWidgetUiState.Loading)
    val uiState: State<AddEditWidgetUiState> = _uiState

    private val uiActionChannel = Channel<AddEditWidgetUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    init {
        (_uiState.value as? AddEditWidgetUiState.Loading)?.let {
            getWidget()
        }
    }

    private fun getWidget() {
        viewModelScope.launch {
            val widgetId = if (isNew) {
                val widget = Widget.Empty(controllerId = id)
                controllerRepository.addWidget(widget)
                widget.id
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