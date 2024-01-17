package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.R

@Composable
fun AddNewWidgetItem(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 8.dp
    val backgroundShape = RoundedCornerShape(cornerRadius)
    val borderColor = MaterialTheme.colorScheme.secondary
    val stroke = Stroke(
        width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
    )

    Box(
        modifier = modifier
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = backgroundShape,
            )
            .clip(backgroundShape)
            .clickable(onClick = onAdd)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = stroke,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                )
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_new_widget_button),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
            )
        }
    }
}