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
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.AddEditControllerUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
    private val messagesUseCases: MessagesUseCases,
    private val controllerRepository: ControllerRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val _uiState = mutableStateOf(
        MainUiState(
            bluetoothState = bluetoothUseCases.getState().value,
            favoriteBluetoothDevice = bluetoothUseCases.getFavoriteBluetoothDevice().value,
            controllersCount = 0,
            inputMessagesCount = 0,
            outputMessagesCount = 0,
            currentDestination = Screen.Connection.route,
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
                uiActionChannel.sendIn(MainUiAction.OpenNavigationDrawer, viewModelScope)
            }

            is MainUiEvent.OnSelectNavDrawerItem -> {
                uiActionChannel.sendIn(
                    MainUiAction.NavigateToSelectedNavDrawerItem(event.selectedItem),
                    viewModelScope
                )
            }

            is MainUiEvent.OnDestinationChanged -> {
                setCurrentDestination(event.route)
            }

            is MainUiEvent.OnRequestMissingPermissions -> {
                uiActionChannel.sendIn(
                    MainUiAction.RequestMissingPermissions(*event.permissions),
                    viewModelScope
                )
            }

            is MainUiEvent.OnShowBluetoothPermissionRationaleDialog -> {
                setBluetoothPermissionRationaleDialogVisibility(true)
            }

            is MainUiEvent.OnConfirmBluetoothPermissionRationaleDialog -> {
                setBluetoothPermissionRationaleDialogVisibility(false)
                uiActionChannel.sendIn(MainUiAction.LaunchPermissionSettingsIntent, viewModelScope)
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
                uiActionChannel.sendIn(
                    MainUiAction.ShowMessagesCount(
                        inputMessagesCount = _uiState.value.inputMessagesCount,
                        outputMessagesCount = _uiState.value.outputMessagesCount,
                    ),
                    viewModelScope
                )
            }

            is MainUiEvent.OnShowSendingErrorMessage -> {
                uiActionChannel.sendIn(
                    MainUiAction.ShowSendingErrorMessage(_uiState.value.favoriteBluetoothDevice),
                    viewModelScope
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

            uiActionChannel.sendIn(
                MainUiAction.RequestMissingPermissions(*e.permissions),
                viewModelScope
            )
        } catch (e: EnableBluetoothAdapterException) {
            uiActionChannel.sendIn(
                MainUiAction.LaunchBluetoothAdapterEnableIntent(e.intent),
                viewModelScope
            )
        }
    }

    private fun connect(device: BluetoothDevice) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                bluetoothUseCases.connect(device)
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()

                uiActionChannel.sendIn(
                    MainUiAction.RequestMissingPermissions(*e.permissions),
                    viewModelScope
                )
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()

                uiActionChannel.sendIn(
                    MainUiAction.ShowConnectionErrorMessage(e.bluetoothDevice ?: device),
                    viewModelScope
                )
            }
        }
    }

    private fun disconnect(device: BluetoothDevice?) {
        try {
            bluetoothUseCases.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            uiActionChannel.sendIn(
                MainUiAction.RequestMissingPermissions(*e.permissions),
                viewModelScope
            )
        }
    }

    private fun setCurrentDestination(route: String) {
        _uiState.update { it.copy(currentDestination = route) }
    }

    private fun setBluetoothPermissionRationaleDialogVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(isBluetoothPermissionRationaleDialogVisible = isVisible) }
    }

    fun deleteWidget(widgetId: UUID) {
        viewModelScope.launch {
            controllerRepository.deleteWidgetById(widgetId)

            uiActionChannel.send(MainUiAction.ShowWidgetDeletedMessage(widgetId))
        }
    }

    fun tryRestoreWidgetById(widgetId: UUID) {
        viewModelScope.launch {
            controllerRepository.tryRestoreWidgetById(widgetId)
        }
    }

    fun deleteController(controllerId: UUID) {
        viewModelScope.launch {
            controllerRepository.deleteControllerById(controllerId)

            uiActionChannel.send(MainUiAction.ShowControllerDeletedMessage(controllerId))
        }
    }

    fun tryRestoreControllerById(controllerId: UUID) {
        viewModelScope.launch {
            controllerRepository.tryRestoreControllerById(controllerId)
        }
    }
}