package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileDialogData
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileType

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleProfileDialog(
    data: BleProfileDialogData,
    onChangeBleProfileData: (BleProfileDialogData) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (BleProfileDialogData) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.bluetooth_le_profile_title))
        },
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BleProfileType.values().forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = it == data.selectedBleProfileType,
                                onClick = {
                                    onChangeBleProfileData(
                                        data.copy(selectedBleProfileType = it)
                                    )
                                },
                            )
                            Text(text = stringResource(id = it.titleResId))
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    TextField(
                        value = data.serviceUuid,
                        onValueChange = { onChangeBleProfileData(data.copy(serviceUuid = it)) },
                        label = {
                            Text(text = stringResource(R.string.service_uuid_hint))
                        },
                        isError = !data.serviceUuidErrorMessage.isNullOrEmpty(),
                        singleLine = true,
                        maxLines = 1,
                        enabled = data.selectedBleProfileType == BleProfileType.CUSTOM,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = data.readCharacteristicUuid,
                        onValueChange = { onChangeBleProfileData(data.copy(readCharacteristicUuid = it)) },
                        label = {
                            Text(text = stringResource(R.string.read_characteristic_uuid_hint))
                        },
                        isError = !data.readCharacteristicUuidErrorMessage.isNullOrEmpty(),
                        singleLine = true,
                        maxLines = 1,
                        enabled = data.selectedBleProfileType == BleProfileType.CUSTOM,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = data.writeCharacteristicUuid,
                        onValueChange = { onChangeBleProfileData(data.copy(writeCharacteristicUuid = it)) },
                        label = {
                            Text(text = stringResource(R.string.write_characteristic_uuid_hint))
                        },
                        isError = !data.writeCharacteristicUuidErrorMessage.isNullOrEmpty(),
                        singleLine = true,
                        maxLines = 1,
                        enabled = data.selectedBleProfileType == BleProfileType.CUSTOM,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(data) }
            ) {
                Text(text = stringResource(R.string.ble_profile_dialog_confirm_button_text))
            }
        },
        modifier = modifier
    )
}