package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.bluetoothme.core.util.debounce
import com.tech.maxclub.bluetoothme.core.util.sendIn
import com.tech.maxclub.bluetoothme.core.util.update
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.tech.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllerWithWidgetsById
import com.tech.maxclub.bluetoothme.feature.controllers.domain.validators.ControllerTitleValidator
import com.tech.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
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
class AddEditControllerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
    private val getControllerWithWidgetsUseCase: GetControllerWithWidgetsById,
    private val controllerTitleValidator: ControllerTitleValidator,
) : ViewModel() {
    private val initControllerId: Int = savedStateHandle[Screen.AddEditController.ARG_CONTROLLER_ID]
        ?: Screen.AddEditController.DEFAULT_CONTROLLER_ID

    private val _uiState = mutableStateOf<AddEditControllerUiState>(
        AddEditControllerUiState.Loading(initControllerId)
    )
    val uiState: State<AddEditControllerUiState> = _uiState

    private val uiActionChannel = Channel<AddEditControllerUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getControllerWithWidgetsJob: Job? = null
    private var swapWidgetsJob: Job? = null

    private val onUpdateControllerTitleWithDebounce: (Controller, String) -> Unit =
        viewModelScope.debounce { controller, newTitle ->
            if (controller.title != newTitle) {
                controllerRepository.updateControllers(controller.copy(title = newTitle))
            }
        }

    init {
        (_uiState.value as? AddEditControllerUiState.Loading)?.let {
            getControllerWithWidgetsById(it.controllerId)
        }
    }

    fun tryUpdateControllerTitle(value: TextFieldValue): Boolean =
        if (controllerTitleValidator(value.text)) {
            (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
                onUpdateControllerTitleWithDebounce(state.controller, value.text)
                _uiState.update { state.copy(controllerTitle = value) }
            }
            true
        } else {
            false
        }

    fun updateControllerWithAccelerometer(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateControllers(it.controller.copy(withAccelerometer = checked))
            }
        }
    }

    fun updateControllerWithVoiceInput(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateControllers(it.controller.copy(withVoiceInput = checked))
            }
        }
    }

    fun updateControllerWithRefresh(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateControllers(it.controller.copy(withRefresh = checked))
            }
        }
    }

    fun updateControllerColumnCount() {
        (_uiState.value as? AddEditControllerUiState.Success)?.let {
            viewModelScope.launch {
                controllerRepository.updateControllers(
                    it.controller.copy(columnsCount = it.controller.columnsCount.next())
                )
            }
        }
    }

    fun updateWidgetSize(widget: Widget<*>, newSize: WidgetSize) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.copy(size = newSize))
        }
    }

    fun updateWidgetEnable(widget: Widget<*>, enabled: Boolean) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.copy(enabled = enabled))
        }
    }

    fun reorderLocalWidgets(fromIndex: Int, toIndex: Int) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            try {
                val fromItem = state.widgets[fromIndex]
                val toItem = state.widgets[toIndex]

                val newFromItem = fromItem.copy(position = toItem.position)
                val newToItem = toItem.copy(position = fromItem.position)

                _uiState.update {
                    state.copy(
                        widgets = state.widgets.toMutableList().apply {
                            set(toIndex, newFromItem)
                            set(fromIndex, newToItem)
                        }
                    )
                }
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
    }

    fun applyWidgetsReorder() {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            swapWidgetsJob?.cancel()
            swapWidgetsJob = viewModelScope.launch {
                controllerRepository.updateWidgets(*state.widgets.toTypedArray())
            }
        }
    }

    private fun getControllerWithWidgetsById(controllerId: Int) {
        getControllerWithWidgetsJob?.cancel()
        getControllerWithWidgetsJob = viewModelScope.launch {
            val newControllerId =
                if (controllerId == Screen.AddEditController.DEFAULT_CONTROLLER_ID) {
                    controllerRepository.addController(Controller(title = ""))
                } else {
                    controllerId
                }

            getControllerWithWidgetsUseCase(newControllerId)
                .onStart {
                    _uiState.update { AddEditControllerUiState.Loading(newControllerId) }
                }
                .onEach { controllerWithWidgets ->
                    val controllerTitle = controllerWithWidgets.controller.title

                    _uiState.update {
                        if (it is AddEditControllerUiState.Success) {
                            it.copy(
                                controllerTitle = if (it.controllerTitle.text == controllerTitle) {
                                    it.controllerTitle
                                } else {
                                    TextFieldValue(
                                        controllerTitle,
                                        TextRange(controllerTitle.length)
                                    )
                                },
                                controller = controllerWithWidgets.controller,
                                widgets = controllerWithWidgets.widgets,
                            )
                        } else {
                            uiActionChannel.sendIn(
                                AddEditControllerUiAction.SetFocusToTitleTextField,
                                viewModelScope
                            )

                            AddEditControllerUiState.Success(
                                controllerTitle = TextFieldValue(
                                    controllerTitle,
                                    TextRange(controllerTitle.length)
                                ),
                                controller = controllerWithWidgets.controller,
                                widgets = controllerWithWidgets.widgets,
                            )
                        }
                    }
                }
                .catch { it.printStackTrace() }
                .launchIn(this)
        }
    }
}