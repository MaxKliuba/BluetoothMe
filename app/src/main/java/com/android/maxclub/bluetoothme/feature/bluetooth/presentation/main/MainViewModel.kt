package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.core.exceptions.EnableBluetoothAdapterException
import com.android.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
    private val messagesUseCases: MessagesUseCases,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        MainUiState(
            bluetoothState = bluetoothUseCases.getState().value,
            favoriteBluetoothDevice = bluetoothUseCases.getFavoriteBluetoothDevice().value,
            controllersCount = 0,
            inputMessagesCount = 0,
            outputMessagesCount = 0,
            selectedNavDrawerItem = Screen.Connection.route,
            isBluetoothPermissionRationaleDialogOpen = false,
        )
    )
    val uiState: State<MainUiState> = _uiState

    private val _uiActionChannel = Channel<MainUiAction>()
    val uiAction = _uiActionChannel.receiveAsFlow()

    private var getStateJob: Job? = null
    private var getFavoriteDeviceJob: Job? = null
    private var connectJob: Job? = null
    private var getMessagesJob: Job? = null

    init {
        getState()
        getFavoriteDevice()
        getMessages()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnDestinationChanged -> {
                _uiState.value = _uiState.value.copy(selectedNavDrawerItem = event.route)
            }

            is MainUiEvent.OnOpenBluetoothPermissionRationaleDialog -> {
                _uiState.value = uiState.value.copy(isBluetoothPermissionRationaleDialogOpen = true)
            }

            is MainUiEvent.OnCloseBluetoothPermissionRationaleDialog -> {
                _uiState.value =
                    uiState.value.copy(isBluetoothPermissionRationaleDialogOpen = false)
            }

            is MainUiEvent.OnEnableAdapter -> enableAdapter()
            is MainUiEvent.OnConnect -> connect(event.device)
            is MainUiEvent.OnDisconnect -> disconnect(event.device)
            is MainUiEvent.OnClickMessagesCountBudge -> {
                viewModelScope.launch {
                    _uiActionChannel.send(
                        MainUiAction.ShowMessagesCount(
                            inputMessagesCount = uiState.value.inputMessagesCount,
                            outputMessagesCount = uiState.value.outputMessagesCount,
                        )
                    )
                }
            }
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.value = uiState.value.copy(bluetoothState = state)
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
                _uiState.value = uiState.value.copy(favoriteBluetoothDevice = favoriteDevice)
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun enableAdapter() {
        try {
            bluetoothUseCases.enableAdapter()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiActionChannel.send(MainUiAction.RequestMissingPermissions(*e.permissions))
            }
        } catch (e: EnableBluetoothAdapterException) {
            viewModelScope.launch {
                _uiActionChannel.send(MainUiAction.LaunchIntent(e.intent))
            }
        }
    }

    private fun connect(device: BluetoothDevice) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                bluetoothUseCases.connect(device)
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()
                _uiActionChannel.send(MainUiAction.RequestMissingPermissions(*e.permissions))
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()
                _uiActionChannel.send(
                    MainUiAction.ShowConnectionErrorMessage(e.bluetoothDevice ?: device)
                )
            }
        }
    }

    private fun disconnect(device: BluetoothDevice?) {
        try {
            bluetoothUseCases.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiActionChannel.send(MainUiAction.RequestMissingPermissions(*e.permissions))
            }
        }
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                _uiState.value = uiState.value.copy(
                    inputMessagesCount = messages.count { it.type == Message.Type.Input },
                    outputMessagesCount = messages.count { it.type == Message.Type.Output || it.type == Message.Type.Error },
                )
                println(messages)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }
}