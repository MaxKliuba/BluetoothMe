package com.android.maxclub.bluetoothme.feature.main.presentation.main

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.core.exceptions.EnableBluetoothAdapterException
import com.android.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toFullString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
    private val messagesUseCases: MessagesUseCases,
    application: Application,
) : AndroidViewModel(application) {

    private val _uiState = mutableStateOf(
        MainUiState(
            bluetoothState = bluetoothUseCases.getState().value,
            favoriteBluetoothDevice = bluetoothUseCases.getFavoriteBluetoothDevice().value,
            controllersCount = 0,
            inputMessagesCount = 0,
            outputMessagesCount = 0,
            selectedNavDrawerItem = Screen.Connection.route,
            isBluetoothPermissionRationaleDialogVisible = false,
        )
    )
    val uiState: State<MainUiState> = _uiState

    private val uiActionChannel = Channel<MainUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getStateJob: Job? = null
    private var getFavoriteDeviceJob: Job? = null
    private var connectJob: Job? = null
    private var getMessagesCountJob: Job? = null

    init {
        getState()
        getFavoriteDevice()
        getMessagesCount()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnOpenNavigationDrawer -> {
                sendUiAction(MainUiAction.OpenNavigationDrawer)
            }

            is MainUiEvent.OnSelectNavDrawerItem -> {
                sendUiAction(MainUiAction.NavigateToSelectedNavDrawerItem(event.selectedItem))
            }

            is MainUiEvent.OnDestinationChanged -> {
                setSelectedNavDrawerItem(event.route)
            }

            is MainUiEvent.OnRequestMissingPermissions -> {
                sendUiAction(MainUiAction.RequestMissingPermissions(*event.permissions))
            }

            is MainUiEvent.OnShowBluetoothPermissionRationaleDialog -> {
                setBluetoothPermissionRationaleDialogVisibility(true)
            }

            is MainUiEvent.OnConfirmBluetoothPermissionRationaleDialog -> {
                setBluetoothPermissionRationaleDialogVisibility(false)
                sendUiAction(MainUiAction.LaunchPermissionSettingsIntent)
            }

            is MainUiEvent.OnDismissBluetoothPermissionRationaleDialog -> {
                setBluetoothPermissionRationaleDialogVisibility(false)
            }

            is MainUiEvent.OnEnableAdapter -> {
                enableAdapter()
            }

            is MainUiEvent.OnConnect -> {
                connect(event.device)
            }

            is MainUiEvent.OnDisconnect -> {
                disconnect(event.device)
            }

            is MainUiEvent.OnShowMessagesCount -> {
                sendUiAction(
                    MainUiAction.ShowMessagesCount(
                        inputMessagesCount = _uiState.value.inputMessagesCount,
                        outputMessagesCount = _uiState.value.outputMessagesCount,
                    )
                )
            }

            is MainUiEvent.OnShowSendingErrorMessage -> {
                sendUiAction(
                    MainUiAction.ShowSendingErrorMessage(
                        device = _uiState.value.favoriteBluetoothDevice
                    )
                )
            }
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .distinctUntilChangedBy { it::class }
            .onEach { state ->
                if (_uiState.value.bluetoothState != state) {
                    messagesUseCases.addMessage(
                        messageType = Message.Type.Log,
                        messageValue = state.toFullString(getApplication()),
                    )
                }

                _uiState.update { it.copy(bluetoothState = state) }
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getFavoriteDevice() {
        getFavoriteDeviceJob?.cancel()
        getFavoriteDeviceJob = bluetoothUseCases.getFavoriteBluetoothDevice()
            .onEach { favoriteDevice ->
                _uiState.update { it.copy(favoriteBluetoothDevice = favoriteDevice) }
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getMessagesCount() {
        getMessagesCountJob?.cancel()
        getMessagesCountJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.update { state ->
                    state.copy(
                        inputMessagesCount = messages.count { it.type == Message.Type.Input },
                        outputMessagesCount = messages.count {
                            it.type == Message.Type.Output || it.type == Message.Type.Error
                        },
                    )
                }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun enableAdapter() {
        try {
            bluetoothUseCases.enableAdapter()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            sendUiAction(MainUiAction.RequestMissingPermissions(*e.permissions))
        } catch (e: EnableBluetoothAdapterException) {
            sendUiAction(MainUiAction.LaunchBluetoothAdapterEnableIntent(e.intent))
        }
    }

    private fun connect(device: BluetoothDevice) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                bluetoothUseCases.connect(device)
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()

                sendUiAction(MainUiAction.RequestMissingPermissions(*e.permissions))
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()

                sendUiAction(
                    MainUiAction.ShowConnectionErrorMessage(
                        device = e.bluetoothDevice ?: device
                    )
                )
            }
        }
    }

    private fun disconnect(device: BluetoothDevice?) {
        try {
            bluetoothUseCases.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            sendUiAction(MainUiAction.RequestMissingPermissions(*e.permissions))
        }
    }

    private fun setSelectedNavDrawerItem(route: String) {
        _uiState.update { it.copy(selectedNavDrawerItem = route) }
    }

    private fun setBluetoothPermissionRationaleDialogVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(isBluetoothPermissionRationaleDialogVisible = isVisible) }
    }

    private fun sendUiAction(uiAction: MainUiAction) {
        uiActionChannel.sendIn(uiAction, viewModelScope)
    }
}