package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.AddControllerFab
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllerList
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllersTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerListScreen(
    onOpenNavigationDrawer: () -> Unit,
    onNavigateToController: (Int) -> Unit,
    onNavigateToAddEditController: (Int?) -> Unit,
    onDeleteController: (Int) -> Unit,
    viewModel: ControllersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val hasSelection by remember { derivedStateOf { state.selectedControllerId != null } }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    LaunchedEffect(key1 = true) {
        viewModel.setSelectedController(null)
        viewModel.setFabState(false)
    }

    BackHandler(hasSelection) {
        viewModel.setSelectedController(null)
    }

    val onUnselectController = { viewModel.setSelectedController(null) }

    Scaffold(
        topBar = {
            ControllersTopBar(
                scrollBehavior = scrollBehavior,
                onOpenNavigationDrawer = onOpenNavigationDrawer,
                isControllerSelected = hasSelection,
                onDeleteController = {
                    state.selectedControllerId?.let { onDeleteController(it) }
                    onUnselectController()
                },
                onEditController = { onNavigateToAddEditController(state.selectedControllerId) },
                onShareController = { state.selectedControllerId?.let { viewModel.shareController(it) } },
            )
        },
        floatingActionButton = {
            if (!hasSelection) {
                AddControllerFab(
                    isOpen = state.isFabOpen,
                    onClickOptions = viewModel::switchFabState,
                    onAddEdit = { onNavigateToAddEditController(null) },
                    onAddFromFile = { /*TODO*/ },
                    onAddFromQrCode = { /*TODO*/ }
                )
            } else {
                FloatingActionButton(onClick = onUnselectController) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(R.string.done_button),
                    )
                }
            }
        },
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
                    onShareController = viewModel::shareController,
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