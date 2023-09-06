package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.AddControllerFab
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllerList
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllersBottomBar
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components.ControllersTopBar
import java.util.UUID

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerListScreen(
    onOpenNavigationDrawer: () -> Unit,
    onNavigateToAddEditController: (UUID?) -> Unit,
    onDeleteController: (UUID) -> Unit,
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

    Scaffold(
        topBar = {
            ControllersTopBar(
                scrollBehavior = scrollBehavior,
                onOpenNavigationDrawer = onOpenNavigationDrawer,
            )
        },
        bottomBar = {
            if (hasSelection) {
                ControllersBottomBar(
                    onDeleteController = { state.selectedControllerId?.let { onDeleteController(it) } },
                    onEditController = { onNavigateToAddEditController(state.selectedControllerId) },
                    onShareController = {
                        state.selectedControllerId?.let { viewModel.shareController(it) }
                    },
                    onUnselectController = { viewModel.setSelectedController(null) }
                )
            }
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
                    onOpenController = onNavigateToAddEditController,
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