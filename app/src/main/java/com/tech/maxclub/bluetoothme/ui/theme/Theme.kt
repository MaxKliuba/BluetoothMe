package com.tech.maxclub.bluetoothme.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xffa4c9ff),
    onPrimary = Color(0xff00315d),
    primaryContainer = Color(0xff195da1),
    onPrimaryContainer = Color(0xffd4e3ff),
    secondary = Color(0xffbcc7db),
    onSecondary = Color(0xff263141),
    secondaryContainer = Color(0xff3d4758),
    onSecondaryContainer = Color(0xffd8e3f8),
    tertiary = Color(0xfff6bf00),
    onTertiary = Color(0xff3e2e00),
    tertiaryContainer = Color(0xff5a4400),
    onTertiaryContainer = Color(0xffffdf96),
    error = Color(0xffffb4ab),
    onError = Color(0xff690005),
    errorContainer = Color(0xff93000a),
    onErrorContainer = Color(0xffffdad6),
    background = Color(0xff1a1c1e),
    onBackground = Color(0xffe3e2e6),
    surface = Color(0xff1a1c1e),
    onSurface = Color(0xffe3e2e6),
    outline = Color(0xff8d9199),
    surfaceVariant = Color(0xff43474e),
    onSurfaceVariant = Color(0xffc3c6cf),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff217bd4),
    onPrimary = Color(0xffffffff),
    primaryContainer = Color(0xffbcd7ff),
    onPrimaryContainer = Color(0xff00315d),
    secondary = Color(0xff545f71),
    onSecondary = Color(0xffffffff),
    secondaryContainer = Color(0xffd8e3f8),
    onSecondaryContainer = Color(0xff111c2b),
    tertiary = Color(0xff765a00),
    onTertiary = Color(0xffffffff),
    tertiaryContainer = Color(0xffffdf96),
    onTertiaryContainer = Color(0xff251a00),
    error = Color(0xffba1a1a),
    onError = Color(0xffffffff),
    errorContainer = Color(0xffffdad6),
    onErrorContainer = Color(0xff410002),
    background = Color(0xfffdfcff),
    onBackground = Color(0xff1a1c1e),
    surface = Color(0xfffdfcff),
    onSurface = Color(0xff1a1c1e),
    outline = Color(0xff73777f),
    surfaceVariant = Color(0xffdfe2eb),
    onSurfaceVariant = Color(0xff43474e),

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BluetoothMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            @Suppress("DEPRECATION")
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}