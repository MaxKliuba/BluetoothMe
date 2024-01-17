package com.tech.maxclub.bluetoothme.feature.main.presentation

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun navigateToHelpScreen(url: String, context: Context) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, Uri.parse(url))
}