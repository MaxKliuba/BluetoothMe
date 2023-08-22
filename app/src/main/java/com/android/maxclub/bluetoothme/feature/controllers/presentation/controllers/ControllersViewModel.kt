package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllersViewModel @Inject constructor(
    private val controllerRepository: ControllerRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        ControllersUiState(
            controllersWithWidgetCount = emptyList(),
        )
    )
    val uiState: State<ControllersUiState> = _uiState

    private var getControllersWithWidgetCountJob: Job? = null

    init {
        getControllersWithWidgetCount()
    }

    private fun getControllersWithWidgetCount() {
        getControllersWithWidgetCountJob?.cancel()
        getControllersWithWidgetCountJob = controllerRepository.getControllersWithWidgetCount()
            .onEach { controllersWithWidgetCount ->
                _uiState.update { state ->
                    state.copy(
                        controllersWithWidgetCount = controllersWithWidgetCount.sortedBy { it.controller.position }
                    )
                }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    fun addNewController() {
        val newController = Controller(
            title = "New Controller",
        )

        viewModelScope.launch {
            controllerRepository.addController(newController)
        }
    }

    fun deleteControllerById(controllerId: Int) {
        viewModelScope.launch {
            controllerRepository.deleteControllerById(controllerId)
        }
    }
}