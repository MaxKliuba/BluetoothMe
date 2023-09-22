package com.android.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.presentation.share_controller.components.ShareControllerTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareControllerScreen(
    onNavigateUp: () -> Unit,
    viewModel: ShareControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    Scaffold(
        topBar = {
            (state as? ShareControllerUiState.Success)?.let { state ->
                ShareControllerTopBar(
                    controllerTitle = state.controllerTitle,
                    onSaveFile = viewModel::saveControllerAsFile,
                    onShare = { /* TODO */ },
                    onNavigateUp = onNavigateUp,
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.let { state ->
                when (state) {
                    is ShareControllerUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ShareControllerUiState.Success -> {
                        if (state.qrCode != null) {
                            Image(
                                bitmap = state.qrCode.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(160.dp)
                                        .padding(8.dp)
                                )

                                Text(text = stringResource(R.string.qr_code_error_message))

                                Button(
                                    onClick = viewModel::saveControllerAsFile,
                                    modifier = Modifier.padding(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FileDownload,
                                        contentDescription = null
                                    )
                                    Text(text = stringResource(id = R.string.save_as_file_button))
                                }
                            }
                        }
                    }

                    is ShareControllerUiState.Error -> {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = stringResource(R.string.qr_code_error_message),
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }
            }
        }
    }
}