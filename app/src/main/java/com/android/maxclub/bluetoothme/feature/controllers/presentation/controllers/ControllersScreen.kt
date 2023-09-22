package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.AddControllerFab
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.CameraPermissionRationaleDialog
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllerList
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllersTopBar
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.launchPermissionSettingsIntent
import com.journeyapps.barcodescanner.ScanContract
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerListScreen(
    onOpenNavigationDrawer: () -> Unit,
    onNavigateToController: (Int) -> Unit,
    onNavigateToShareController: (Int) -> Unit,
    onNavigateToAddEditController: (Int?) -> Unit,
    onDeleteController: (Int) -> Unit,
    viewModel: ControllersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val hasSelection by remember { derivedStateOf { state.selectedControllerId != null } }

    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val permissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.launchQrCodeScanner()
        }
    }

    val qrCodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { json ->
            viewModel.addControllerFromJson(json)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.setSelectedController(null)
        viewModel.setFabState(false)

        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ControllersUiAction.LaunchQrCodeScanner -> {
                    val isCameraAvailable =
                        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                    val cameraPermission = Manifest.permission.CAMERA

                    when {
                        !isCameraAvailable -> {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.camera_is_unavailable_message),
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        }

                        ContextCompat.checkSelfPermission(
                            context,
                            cameraPermission
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            qrCodeLauncher.launch(action.scanOptions)
                        }

                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            cameraPermission
                        ) -> {
                            viewModel.showCameraPermissionRationaleDialogVisible()
                        }

                        else -> permissionResultLauncher.launch(cameraPermission)
                    }
                }

                is ControllersUiAction.LaunchPermissionSettingsIntent -> {
                    launchPermissionSettingsIntent(context)
                }

                is ControllersUiAction.ShowJsonDecodingErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.json_decoding_error_message),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    BackHandler(hasSelection) {
        viewModel.setSelectedController(null)
    }

    if (state.isCameraPermissionRationaleDialogVisible) {
        CameraPermissionRationaleDialog(
            onDismiss = viewModel::dismissCameraPermissionRationaleDialog,
            onConfirm = viewModel::confirmCameraPermissionRationaleDialog,
        )
    }

    Scaffold(
        topBar = {
            ControllersTopBar(
                scrollBehavior = scrollBehavior,
                onOpenNavigationDrawer = onOpenNavigationDrawer,
                isControllerSelected = hasSelection,
                onDeleteController = {
                    state.selectedControllerId?.let { onDeleteController(it) }
                    viewModel.setSelectedController(null)
                },
                onEditController = { onNavigateToAddEditController(state.selectedControllerId) },
                onShareController = {
                    state.selectedControllerId?.let { onNavigateToShareController(it) }
                },
            )
        },
        floatingActionButton = {
            if (!hasSelection) {
                AddControllerFab(
                    isOpen = state.isFabOpen,
                    onClickOptions = viewModel::switchFabState,
                    onAddEdit = { onNavigateToAddEditController(null) },
                    onOpenFile = { /* TODO */ },
                    onScanQrCode = viewModel::launchQrCodeScanner,
                )
            } else {
                FloatingActionButton(onClick = { viewModel.setSelectedController(null) }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(R.string.done_button),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ControllerList(
                    controllers = state.controllers,
                    selectedControllerId = state.selectedControllerId,
                    onOpenController = onNavigateToController,
                    onShareController = onNavigateToShareController,
                    onSelectController = viewModel::setSelectedController,
                    onUnselectController = { viewModel.setSelectedController(null) },
                    onReorderController = viewModel::swapControllers,
                    onApplyChangedControllerPositions = viewModel::applyChangedControllerPositions,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}