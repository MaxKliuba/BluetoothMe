package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllersWithWidgetCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ControllersViewModel @Inject constructor(
    private val controllerRepository: ControllerRepository,
    private val getControllersWithWidgetCountUseCase: GetControllersWithWidgetCount,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        ControllersUiState(
            isLoading = true,
            controllers = emptyList(),
            selectedControllerId = null,
        )
    )
    val uiState: State<ControllersUiState> = _uiState

    private var getControllersWithWidgetCountJob: Job? = null
    private var swapControllersJob: Job? = null

    init {
        getControllersWithWidgetCount()

        viewModelScope.launch {
            controllerRepository.deleteMarkedAsDeletedControllers()
            controllerRepository.deleteMarkedAsDeletedWidgets()
        }
    }

    private fun getControllersWithWidgetCount() {
        getControllersWithWidgetCountJob?.cancel()
        getControllersWithWidgetCountJob = getControllersWithWidgetCountUseCase()
            .onStart {
                _uiState.update { it.copy(isLoading = true) }
            }
            .onEach { controllers ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        controllers = controllers,
                    )
                }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    fun updateControllersPosition(fromPosition: Int, toPosition: Int) {
        val currentId = _uiState.value.controllers[fromPosition].controller.id
        val otherId = _uiState.value.controllers[toPosition].controller.id

        _uiState.update {
            it.copy(controllers = it.controllers.toMutableList()
                .apply { add(fromPosition, removeAt(toPosition)) }
            )
        }

        swapControllersJob?.cancel()
        swapControllersJob = viewModelScope.launch {
            delay(1000)
            controllerRepository.swapControllersByIds(currentId, otherId, fromPosition, toPosition)
        }
    }

    fun setSelectedController(controllerId: UUID?) {
        _uiState.update { it.copy(selectedControllerId = controllerId) }
    }

    fun shareController(controllerId: UUID) {
        // TODO
    }
}