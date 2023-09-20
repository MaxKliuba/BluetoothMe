package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R

@Composable
fun AddControllerFab(
    isOpen: Boolean,
    onClickOptions: () -> Unit,
    onAddEdit: () -> Unit,
    onAddFromFile: () -> Unit,
    onAddFromQrCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (isOpen) {
            SmallFloatingActionButton(onClick = onAddFromQrCode) {
                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = stringResource(R.string.scan_qr_code_button)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SmallFloatingActionButton(onClick = onAddFromFile) {
                Icon(
                    imageVector = Icons.Outlined.UploadFile,
                    contentDescription = stringResource(R.string.upload_from_file_button)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SmallFloatingActionButton(onClick = onAddEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.create_manually_button)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        FloatingActionButton(onClick = onClickOptions) {
            if (isOpen) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_button)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_controller_button)
                )
            }
        }
    }
}