package com.tech.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import android.graphics.Bitmap

sealed class ShareControllerUiState {
    object Loading : ShareControllerUiState()
    data class Success(
        val controllerTitle: String,
        val json: String,
        val qrCode: Bitmap?,
        val isStoragePermissionRationaleDialogVisible: Boolean,
    ) : ShareControllerUiState()

    object Error : ShareControllerUiState()
}
