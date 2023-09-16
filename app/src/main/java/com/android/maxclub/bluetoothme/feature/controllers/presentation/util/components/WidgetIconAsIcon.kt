package com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon

@Composable
fun WidgetIcon.AsIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    if (drawableResId != 0) {
        Icon(
            painter = painterResource(id = drawableResId),
            contentDescription = stringResource(id = titleResId),
            tint = tint,
            modifier = modifier,
        )
    }
}