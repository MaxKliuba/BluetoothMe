package com.android.maxclub.bluetoothme.presentation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.domain.exceptions.EnableBluetoothAdapterException
import com.android.maxclub.bluetoothme.domain.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.usecase.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.domain.usecase.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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
        )
    )
    val uiState: State<MainUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<MainUiEvent>()
    val uiEvent: SharedFlow<MainUiEvent> = _uiEvent

    private var getStateJob: Job? = null
    private var getFavoriteDeviceJob: Job? = null
    private var connectJob: Job? = null
    private var getMessagesJob: Job? = null

    init {
        getState()
        getFavoriteDevice()
        getMessages()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnClickNavigationIcon -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnOpenNavigationDrawer)
                }
            }
            is MainEvent.OnEnableAdapter -> enableAdapter()
            is MainEvent.OnConnect -> connect(event.device)
            is MainEvent.OnDisconnect -> disconnect(event.device)
            is MainEvent.OnClickMessagesCountBudge -> {
                viewModelScope.launch {
                    _uiEvent.emit(
                        MainUiEvent.OnShowMessagesCount(
                            inputMessagesCount = uiState.value.inputMessagesCount,
                            outputMessagesCount = uiState.value.outputMessagesCount,
                        )
                    )
                }
            }
            is MainEvent.OnNavigate -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnNavigate(event.route))
                }
            }
            is MainEvent.OnLaunchUrl -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnLaunchUrl(event.url))
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
                _uiEvent.emit(MainUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            }
        } catch (e: EnableBluetoothAdapterException) {
            viewModelScope.launch {
                _uiEvent.emit(MainUiEvent.OnLaunchIntent(e.intent))
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
                _uiEvent.emit(MainUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()
                _uiEvent.emit(MainUiEvent.OnShowConnectionErrorMessage(e.bluetoothDevice))
            }
        }
    }

    private fun disconnect(device: BluetoothDevice?) {
        try {
            bluetoothUseCases.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiEvent.emit(MainUiEvent.OnShowMissingPermissionMessage(*e.permissions))
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