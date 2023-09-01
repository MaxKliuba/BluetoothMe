package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.core.util.debounce
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllerWithWidgetsById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
class AddEditControllerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
    private val getControllerWithWidgetsUseCase: GetControllerWithWidgetsById,
) : ViewModel() {
    private var controllerId: UUID? =
        savedStateHandle.get<String>(Screen.AddEditController.ARG_CONTROLLER_ID)?.let {
            UUID.fromString(it)
        }

    private val _uiState = mutableStateOf<AddEditControllerUiState>(
        AddEditControllerUiState.Loading(controllerId ?: UUID.randomUUID())
    )
    val uiState: State<AddEditControllerUiState> = _uiState

    private val uiActionChannel = Channel<AddEditControllerUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getControllerWithWidgetsJob: Job? = null

    private val onUpdateControllerTitle: (String) -> Unit =
        viewModelScope.debounce { title ->
            (_uiState.value as? AddEditControllerUiState.Success)?.let {
                viewModelScope.launch {
                    controllerRepository.updateController(it.controller.copy(title = title))
                }
            }
        }

    init {
        (_uiState.value as? AddEditControllerUiState.Loading)?.let {
            getControllerWithWidgetsById(it.controllerId)

            uiActionChannel.sendIn(
                AddEditControllerUiAction.SetFocusToTitleTextField,
                viewModelScope
            )
        }
    }

    private fun getControllerWithWidgetsById(controllerId: UUID) {
        getControllerWithWidgetsJob?.cancel()
        getControllerWithWidgetsJob = viewModelScope.launch {
            if (this@AddEditControllerViewModel.controllerId == null) {
                controllerRepository.addController(
                    Controller(
                        id = controllerId,
                        title = "",
                    )
                )

                this@AddEditControllerViewModel.controllerId = controllerId
            }

            getControllerWithWidgetsUseCase(controllerId)
                .onStart {
                    _uiState.update { AddEditControllerUiState.Loading(controllerId) }
                }
                .onEach { controllerWithWidgets ->
                    val controllerTitle = controllerWithWidgets.controller.title

                    _uiState.update {
                        AddEditControllerUiState.Success(
                            controllerTitle = if (it is AddEditControllerUiState.Success
                                && it.controllerTitle.text == controllerTitle
                            ) {
                                it.controllerTitle
                            } else {
                                TextFieldValue(
                                    text = controllerTitle,
                                    selection = TextRange(controllerTitle.length)
                                )
                            },
                            controller = controllerWithWidgets.controller,
                            widgets = controllerWithWidgets.widgets,
                        )
                    }
                }
                .catch { it.printStackTrace() }
                .launchIn(this)
        }
    }

    fun updateControllerTitle(value: TextFieldValue) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            if (state.controller.title != value.text) {
                onUpdateControllerTitle(value.text)
            }
            _uiState.update { state.copy(controllerTitle = value) }
        }
    }

    fun updateControllerWithAccelerometer(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateController(it.controller.copy(withAccelerometer = checked))
            }
        }
    }

    fun updateControllerWithVoiceInput(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateController(it.controller.copy(withVoiceInput = checked))
            }
        }
    }

    fun updateControllerWithRefresh(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateController(it.controller.copy(withRefresh = checked))
            }
        }
    }

    fun updateControllerColumnCount(newColumnsCount: ControllerColumns) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateController(it.controller.copy(columnsCount = newColumnsCount))
            }
        }
    }

    fun updateWidgetSize(widgetId: UUID, newSize: WidgetSize) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            viewModelScope.launch {
                state.widgets.find { it.id == widgetId }?.let {
                    controllerRepository.updateWidget(it.copy(size = newSize))
                }
            }
        }
    }

    fun updateWidgetEnable(widgetId: UUID, enabled: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            viewModelScope.launch {
                state.widgets.find { it.id == widgetId }?.let {
                    controllerRepository.updateWidget(it.copy(enabled = enabled))
                }
            }
        }
    }

    fun updateWidgetPosition(widgetId: UUID, newPosition: Int) {
        viewModelScope.launch {
            controllerRepository.updateWidgetPositionById(widgetId, newPosition)
        }
    }
}