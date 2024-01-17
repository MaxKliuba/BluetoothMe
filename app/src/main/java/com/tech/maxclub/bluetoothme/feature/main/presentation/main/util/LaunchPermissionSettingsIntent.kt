package com.tech.maxclub.bluetoothme.feature.main.presentation.main.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun launchPermissionSettingsIntent(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}