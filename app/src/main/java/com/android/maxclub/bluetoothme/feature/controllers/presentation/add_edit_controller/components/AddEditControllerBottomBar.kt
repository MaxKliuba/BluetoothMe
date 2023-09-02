package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns

@Composable
fun AddEditControllerBottomBar(
    columnsCount: ControllerColumns,
    onColumnCountChange: () -> Unit,
    onDeleteController: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = onDeleteController) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.delete_controller_button)
                )
            }

            IconButton(onClick = onColumnCountChange) {
                Icon(
                    painter = painterResource(
                        id = when (columnsCount) {
                            ControllerColumns.TWO -> R.drawable.ic_two_column_24
                            ControllerColumns.THREE -> R.drawable.ic_three_column_24
                        }
                    ),
                    contentDescription = stringResource(R.string.columns_count_button)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done_button),
                )
            }
        },
        modifier = modifier
    )
}