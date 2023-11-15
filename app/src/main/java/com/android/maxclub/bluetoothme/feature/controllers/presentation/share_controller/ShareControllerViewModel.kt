package com.android.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
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

    private val uiActionChannel = Channel<ShareControllerUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getControllerWithWidgetsJsonJob: Job? = null

    init {
        (_uiState.value as? ShareControllerUiState.Loading)?.let {
            getControllerWithWidgetsJson(controllerId)
        }
    }

    fun saveControllerAsFile() {
        (_uiState.value as? ShareControllerUiState.Success)?.let { state ->
            try {
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "BluetoothMe"
                )
                if (!directory.exists()) {
                    directory.mkdir()
                }

                val jsonFile = renameJsonFileWithNumber(
                    File(directory, "${state.controllerTitle}.json")
                )

                FileOutputStream(jsonFile).use { outputStream ->
                    outputStream.write(state.json.toByteArray())

                    uiActionChannel.sendIn(
                        ShareControllerUiAction.ShowSavedSuccessfullyMessage(jsonFile.path),
                        viewModelScope
                    )
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    ShareControllerUiAction.RequestMissingStoragePermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    viewModelScope
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    ShareControllerUiAction.RequestMissingStoragePermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    viewModelScope
                )
            } catch (e: IOException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    ShareControllerUiAction.ShowSavingErrorMessage,
                    viewModelScope
                )
            }
        }
    }

    fun shareFile(context: Context) {
        (_uiState.value as? ShareControllerUiState.Success)?.let { state ->
            val authority = "${context.packageName}.fileprovider"
            val fileName = "${state.controllerTitle}.json"
            val jsonFile = File(context.cacheDir, fileName)

            try {
                FileWriter(jsonFile).use { fileWriter ->
                    fileWriter.write(state.json)
                }

                val jsonFileUri = FileProvider.getUriForFile(context, authority, jsonFile)

                val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_SUBJECT, state.controllerTitle)
                    putExtra(Intent.EXTRA_STREAM, jsonFileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooserIntent = Intent.createChooser(sharingIntent, null)

                uiActionChannel.sendIn(
                    ShareControllerUiAction.LaunchFileSharingIntent(chooserIntent),
                    viewModelScope
                )
            } catch (e: IOException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    ShareControllerUiAction.ShowSharingErrorMessage,
                    viewModelScope
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    ShareControllerUiAction.ShowSharingErrorMessage,
                    viewModelScope
                )
            }
        }
    }

    fun showStoragePermissionRationaleDialog() {
        (_uiState.value as? ShareControllerUiState.Success)?.let { state ->
            _uiState.update { state.copy(isStoragePermissionRationaleDialogVisible = true) }
        }
    }

    fun dismissStoragePermissionRationaleDialog() {
        (_uiState.value as? ShareControllerUiState.Success)?.let { state ->
            _uiState.update { state.copy(isStoragePermissionRationaleDialogVisible = false) }
        }
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
                            controllerTitle = controllerWithWidgetsJson.controller.title.ifBlank { "Controller" },
                            json = json,
                            qrCode = qrCode,
                            isStoragePermissionRationaleDialogVisible = false,
                        )
                    }
                }
                .catch {
                    it.printStackTrace()
                    _uiState.update { ShareControllerUiState.Error }
                }
                .launchIn(viewModelScope)
    }

    private fun renameJsonFileWithNumber(file: File): File {
        var renamedFile = file

        val parentFile = renamedFile.parentFile
        val fileNameWithoutExtension = renamedFile.nameWithoutExtension
        val fileExtension = renamedFile.extension

        var counter = 1
        while (renamedFile.exists()) {
            val newFileName = "$fileNameWithoutExtension (${counter++}).$fileExtension"
            renamedFile = File(parentFile, newFileName)
        }

        return renamedFile
    }
}