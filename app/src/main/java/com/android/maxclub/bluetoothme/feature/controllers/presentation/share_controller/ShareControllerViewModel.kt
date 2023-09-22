package com.android.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val QR_CODE_SIZE = 2048

@HiltViewModel
class ShareControllerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
) : ViewModel() {

    private val controllerId: Int = savedStateHandle[Screen.ShareController.ARG_CONTROLLER_ID]
        ?: Screen.ShareController.DEFAULT_CONTROLLER_ID

    private val _uiState = mutableStateOf<ShareControllerUiState>(ShareControllerUiState.Loading)
    val uiState: State<ShareControllerUiState> = _uiState

    private var getControllerWithWidgetsJsonJob: Job? = null

    init {
        (_uiState.value as? ShareControllerUiState.Loading)?.let {
            getControllerWithWidgetsJson(controllerId)
        }
    }

    fun saveControllerAsFile() {
        // TODO
    }

    private fun getControllerWithWidgetsJson(controllerId: Int) {
        getControllerWithWidgetsJsonJob?.cancel()
        getControllerWithWidgetsJsonJob =
            controllerRepository.getControllerWithWidgetsJsonById(controllerId)
                .onStart {
                    _uiState.update { ShareControllerUiState.Loading }
                }
                .onEach { controllerWithWidgetsJson ->
                    val json = Json.encodeToString(controllerWithWidgetsJson)

                    val qrCode = try {
                        BarcodeEncoder().encodeBitmap(
                            json,
                            BarcodeFormat.QR_CODE,
                            QR_CODE_SIZE,
                            QR_CODE_SIZE,
                            mapOf(EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name())
                        )
                    } catch (e: WriterException) {
                        e.printStackTrace()
                        null
                    }

                    _uiState.update {
                        ShareControllerUiState.Success(
                            controllerTitle = controllerWithWidgetsJson.controller.title,
                            json = json,
                            qrCode = qrCode,
                        )
                    }
                }
                .catch {
                    it.printStackTrace()
                    _uiState.update { ShareControllerUiState.Error }
                }
                .launchIn(viewModelScope)
    }
}