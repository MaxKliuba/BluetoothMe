package com.tech.maxclub.bluetoothme.feature.controllers.presentation.share_controller

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.share_controller.components.ShareControllerTopBar
import com.tech.maxclub.bluetoothme.feature.main.presentation.main.components.PermissionRationaleDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareControllerScreen(
    onNavigateUp: () -> Unit,
    viewModel: ShareControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val storagePermissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.saveControllerAsFile()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ShareControllerUiAction.RequestMissingStoragePermission -> {
                    when {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            action.permission
                        ) -> viewModel.showStoragePermissionRationaleDialog()

                        else -> storagePermissionResultLauncher.launch(action.permission)
                    }
                }

                is ShareControllerUiAction.ShowSavedSuccessfullyMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.json_successfully_saved_message,
                            action.filePath
                        ),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                    )
                }

                is ShareControllerUiAction.ShowSavingErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.json_saving_error_message),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }

                is ShareControllerUiAction.LaunchFileSharingIntent -> {
                    context.startActivity(action.intent)
                }

                is ShareControllerUiAction.ShowSharingErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.sharing_error_message),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    (state as? ShareControllerUiState.Success)?.let {
        if (it.isStoragePermissionRationaleDialogVisible) {
            PermissionRationaleDialog(
                title = stringResource(R.string.storage_permission_dialog_title),
                text = stringResource(R.string.storage_permission_dialog_text),
                onDismiss = viewModel::dismissStoragePermissionRationaleDialog,
            )
        }
    }

    Scaffold(
        topBar = {
            (state as? ShareControllerUiState.Success)?.let { state ->
                ShareControllerTopBar(
                    controllerTitle = state.controllerTitle,
                    onSaveFile = viewModel::saveControllerAsFile,
                    onShare = { viewModel.shareFile(context) },
                    onNavigateUp = onNavigateUp,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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