package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllerWithWidgetsById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    private var updateControllerTitleJob: Job? = null
    private var swapWidgetsJob: Job? = null

    init {
        (_uiState.value as? AddEditControllerUiState.Loading)?.let {
            getControllerWithWidgetsById(it.controllerId)
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
                                uiActionChannel.sendIn(
                                    AddEditControllerUiAction.SetFocusToTitleTextField,
                                    viewModelScope
                                )
                                TextFieldValue(controllerTitle, TextRange(controllerTitle.length))
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
                updateControllerTitleJob?.cancel()
                updateControllerTitleJob = viewModelScope.launch {
                    delay(300)
                    controllerRepository.updateController(state.controller.copy(title = value.text))
                }
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

    fun updateControllerColumnCount() {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateController(
                    it.controller.copy(columnsCount = it.controller.columnsCount.next())
                )
            }
        }
    }

    fun updateWidgetSize(widget: Widget, newSize: WidgetSize) {
        viewModelScope.launch {
            controllerRepository.updateWidget(widget.copy(size = newSize))
        }
    }

    fun updateWidgetEnable(widget: Widget, enabled: Boolean) {
        viewModelScope.launch {
            controllerRepository.updateWidget(widget.copy(enabled = enabled))
        }
    }

    fun updateWidgetsPosition(fromPosition: Int, toPosition: Int) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            val currentId = state.widgets[fromPosition].id
            val otherId = state.widgets[toPosition].id

            _uiState.update {
                state.copy(widgets = state.widgets.toMutableList()
                    .apply { add(fromPosition, removeAt(toPosition)) }
                )
            }

            swapWidgetsJob?.cancel()
            swapWidgetsJob = viewModelScope.launch {
                delay(1000)
                controllerRepository.swapControllersByIds(
                    currentId,
                    otherId,
                    fromPosition,
                    toPosition
                )
            }
        }
    }
}