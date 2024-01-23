package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.journeyapps.barcodescanner.ScanOptions
import com.tech.maxclub.bluetoothme.core.util.sendIn
import com.tech.maxclub.bluetoothme.core.util.update
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.tech.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllersWithWidgetCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
            isCameraPermissionRationaleDialogVisible = false,
        )
    )
    val uiState: State<ControllersUiState> = _uiState

    private val uiActionChannel = Channel<ControllersUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getControllersWithWidgetCountJob: Job? = null
    private var swapControllersJob: Job? = null

    init {
        if (_uiState.value.isLoading) {
            getControllersWithWidgetCount()
        }
    }

    fun setFabState(isOpen: Boolean) {
        _uiState.update { it.copy(isFabOpen = isOpen) }
    }

    fun switchFabState() {
        setFabState(!_uiState.value.isFabOpen)
    }

    fun showCameraPermissionRationaleDialog() {
        _uiState.update { it.copy(isCameraPermissionRationaleDialogVisible = true) }
    }

    fun dismissCameraPermissionRationaleDialog() {
        _uiState.update { it.copy(isCameraPermissionRationaleDialogVisible = false) }
    }

    fun launchQrCodeScanner() {
        setFabState(false)

        val scanOptions = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setOrientationLocked(false)
            setBeepEnabled(false)
            setPrompt("")
        }
        uiActionChannel.sendIn(ControllersUiAction.LaunchQrCodeScanner(scanOptions), viewModelScope)
    }

    fun launchOpenJsonFileIntent() {
        setFabState(false)

        uiActionChannel.sendIn(
            ControllersUiAction.LaunchOpenJsonFileIntent("application/json"),
            viewModelScope
        )
    }

    fun addControllerFromJsonFile(context: Context, jsonFileUri: Uri) {
        context.contentResolver.openInputStream(jsonFileUri)?.use { inputStream ->
            val json = String(inputStream.readBytes())
            addControllerFromJson(json)
        }
    }

    fun addControllerFromJson(json: String) {
        try {
            val controllerWithWidgetsJson = Json.decodeFromString<ControllerWithWidgetsJson>(json)
            viewModelScope.launch {
                controllerRepository.addControllerWithWidgets(controllerWithWidgetsJson)
            }
        } catch (e: SerializationException) {
            e.printStackTrace()
            uiActionChannel.sendIn(ControllersUiAction.ShowJsonDecodingErrorMessage, viewModelScope)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            uiActionChannel.sendIn(ControllersUiAction.ShowJsonDecodingErrorMessage, viewModelScope)
        }
    }

    fun setSelectedController(controllerId: Int?) {
        _uiState.update { it.copy(selectedControllerId = controllerId) }
    }

    fun reorderLocalControllers(fromIndex: Int, toIndex: Int) {
        try {
            val fromItem = _uiState.value.controllers[fromIndex]
            val toItem = _uiState.value.controllers[toIndex]

            val newFromItem = fromItem.copy(
                controller = fromItem.controller.copy(position = toItem.controller.position)
            )
            val newToItem = toItem.copy(
                controller = toItem.controller.copy(position = fromItem.controller.position)
            )

            _uiState.update {
                it.copy(
                    controllers = it.controllers.toMutableList().apply {
                        set(toIndex, newFromItem)
                        set(fromIndex, newToItem)
                    }
                )
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun applyControllersReorder() {
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