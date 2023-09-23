package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import com.journeyapps.barcodescanner.ScanOptions

sealed class ControllersUiAction {
    data class LaunchQrCodeScanner(val scanOptions: ScanOptions) : ControllersUiAction()
    object ShowJsonDecodingErrorMessage : ControllersUiAction()
    data class LaunchOpenJsonFileIntent(val contentType: String) : ControllersUiAction()
}