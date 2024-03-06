package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controller

import android.content.Intent

sealed class ControllerUiAction {
    data object ShowSendingErrorMessage : ControllerUiAction()
    data class LaunchSpeechRecognizerIntent(val intent: Intent) : ControllerUiAction()
}
