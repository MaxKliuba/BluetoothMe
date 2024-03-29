package com.tech.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import android.content.Intent

sealed class ShareControllerUiAction {
    data class RequestMissingStoragePermission(val permission: String) : ShareControllerUiAction()
    data class ShowSavedSuccessfullyMessage(val filePath: String) : ShareControllerUiAction()
    data object ShowSavingErrorMessage : ShareControllerUiAction()
    data class LaunchFileSharingIntent(val intent: Intent) : ShareControllerUiAction()
    data object ShowSharingErrorMessage : ShareControllerUiAction()
}
