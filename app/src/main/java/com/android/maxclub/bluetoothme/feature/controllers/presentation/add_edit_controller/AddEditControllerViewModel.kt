package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllerWithWidgetsById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditControllerViewModel @Inject constructor(
    private val controllerRepository: ControllerRepository,
    private val getControllerWithWidgetsUseCase: GetControllerWithWidgetsById,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val controllerId: UUID? =
        savedStateHandle.get<String>(Screen.AddEditController.ARG_CONTROLLER_ID)?.let {
            UUID.fromString(it)
        }

    private val _uiState = mutableStateOf(
        if (controllerId == null) {
            AddEditControllerUiState.Success(
                controllerTitle = TextFieldValue(""),
                controller = Controller(
                    title = "",
                    withAccelerometer = false,
                    withVoiceInput = false,
                    withRefresh = false,
                ),
                widgets = emptyList(),
            )
        } else {
            AddEditControllerUiState.Loading(controllerId)
        }
    )
    val uiState: State<AddEditControllerUiState> = _uiState

    private var getControllerWithWidgetsJob: Job? = null

    init {
        (_uiState.value as? AddEditControllerUiState.Loading)?.let {
            getControllerWithWidgetsById(it.controllerId)
        }
    }

    private fun getControllerWithWidgetsById(controllerId: UUID) {
        getControllerWithWidgetsJob?.cancel()
        getControllerWithWidgetsJob = getControllerWithWidgetsUseCase(controllerId)
            .onStart {
                _uiState.update { AddEditControllerUiState.Loading(controllerId) }
            }
            .onEach { controllerWithWidgets ->
                val controllerTitle = controllerWithWidgets.controller.title

                _uiState.update {
                    AddEditControllerUiState.Success(
                        controllerTitle = TextFieldValue(
                            text = controllerTitle,
                            selection = TextRange(controllerTitle.length)
                        ),
                        controller = controllerWithWidgets.controller,
                        widgets = controllerWithWidgets.widgets,
                    )
                }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    fun onTitleFocused() {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update { state.copy(isTitleFocused = true) }
        }
    }

    fun onTitleChange(value: TextFieldValue) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update {
                state.copy(
                    controllerTitle = value,
                    controller = state.controller.copy(title = value.text)
                )
            }
        }
    }

    fun onChangeWithAccelerometer(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update {
                state.copy(controller = state.controller.copy(withAccelerometer = checked))
            }
        }
    }

    fun onChangeWithVoiceInput(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update {
                state.copy(controller = state.controller.copy(withVoiceInput = checked))
            }
        }
    }

    fun onChangeWithRefresh(checked: Boolean) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update {
                state.copy(controller = state.controller.copy(withRefresh = checked))
            }
        }
    }

    fun addWidget(widget: Widget) {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            _uiState.update {
                state.copy(widgets = state.widgets.plus(widget))
            }
        }
    }

    fun applyChanges() {
        (_uiState.value as? AddEditControllerUiState.Success)?.let { state ->
            viewModelScope.launch {
                controllerRepository.addController(state.controller)
                controllerRepository.addWidgets(*state.widgets.toTypedArray())
            }
        }
    }
}