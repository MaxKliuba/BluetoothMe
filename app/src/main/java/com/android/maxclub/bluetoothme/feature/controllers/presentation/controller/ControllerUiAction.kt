package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller

import android.content.Intent

sealed class ControllerUiAction {
    object ShowSendingErrorMessage : ControllerUiAction()
    data class LaunchSpeechRecognizerIntent(val intent: Intent) : ControllerUiAction()
}
