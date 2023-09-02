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
        )
    )
    val uiState: State<ControllersUiState> = _uiState

    private var getControllersWithWidgetCountJob: Job? = null

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
}