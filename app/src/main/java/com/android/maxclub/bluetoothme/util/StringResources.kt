package com.android.maxclub.bluetoothme.util

import android.content.Context
import androidx.annotation.StringRes

sealed class StringResources {
    data class String(val string: kotlin.String) : StringResources()
    data class Resources(@StringRes val stringResId: Int) : StringResources()

    fun toString(context: Context): kotlin.String =
        when (this) {
            is String -> this.string
            is Resources -> context.getString(this.stringResId)
        }
}