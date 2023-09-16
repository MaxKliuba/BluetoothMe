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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
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
            isFabOpen = false,
        )
    )
    val uiState: State<ControllersUiState> = _uiState

    private var getControllersWithWidgetCountJob: Job? = null
    private var swapControllersJob: Job? = null

    init {
        getControllersWithWidgetCount()
    }

    fun setFabState(isOpen: Boolean) {
        _uiState.update { it.copy(isFabOpen = isOpen) }
    }

    fun switchFabState() {
        setFabState(!_uiState.value.isFabOpen)
    }

    fun shareController(controllerId: Int) {
        // TODO
    }

    fun setSelectedController(controllerId: Int?) {
        _uiState.update { it.copy(selectedControllerId = controllerId) }
    }

    fun swapControllers(fromPosition: Int, toPosition: Int) {
        try {
            val newCurrentItem = _uiState.value.controllers[fromPosition].let {
                it.copy(controller = it.controller.copy(position = toPosition))
            }
            val newOtherItem = _uiState.value.controllers[toPosition].let {
                it.copy(controller = it.controller.copy(position = fromPosition))
            }

            _uiState.update {
                it.copy(
                    controllers = it.controllers.toMutableList().apply {
                        set(fromPosition, newCurrentItem)
                        set(toPosition, newOtherItem)
                    }.sortedWith(getControllersWithWidgetCountUseCase.comparator)
                )
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun applyChangedControllerPositions() {
        swapControllersJob?.cancel()
        swapControllersJob = viewModelScope.launch {
            controllerRepository.updateControllers(
                *_uiState.value.controllers.map { it.controller }.toTypedArray()
            )
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
}