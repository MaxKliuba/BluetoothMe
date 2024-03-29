package com.tech.maxclub.bluetoothme.feature.main.presentation.main

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.bluetoothme.core.exceptions.BluetoothConnectionException
import com.tech.maxclub.bluetoothme.core.exceptions.EnableBluetoothAdapterException
import com.tech.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.tech.maxclub.bluetoothme.core.util.sendIn
import com.tech.maxclub.bluetoothme.core.util.update
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toFullString
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.MessagesUseCases
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.tech.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
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
    private val messagesUseCases: MessagesUseCases,
    private val bluetoothRepository: BluetoothRepository,
    private val controllerRepository: ControllerRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val _uiState = mutableStateOf(
        MainUiState(
            bluetoothState = bluetoothRepository.getState().value,
            favoriteBluetoothDevice = bluetoothRepository.getFavoriteBluetoothDevice().value,
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

        deleteMarkedAsDeletedControllersAndWidgets()
    }

    fun setCurrentDestination(route: String) {
        _uiState.update { it.copy(currentDestination = route) }
    }

    fun requestMissingPermissions(vararg permissions: String) {
        uiActionChannel.sendIn(
            MainUiAction.RequestMissingBluetoothPermissions(*permissions),
            viewModelScope
        )
    }

    fun showBluetoothPermissionRationaleDialog() {
        _uiState.update { it.copy(isBluetoothPermissionRationaleDialogVisible = true) }
    }

    fun dismissBluetoothPermissionRationaleDialog() {
        _uiState.update { it.copy(isBluetoothPermissionRationaleDialogVisible = false) }
    }

    fun enableBluetoothAdapter() {
        try {
            bluetoothRepository.enableAdapter()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            uiActionChannel.sendIn(
                MainUiAction.RequestMissingBluetoothPermissions(*e.permissions),
                viewModelScope
            )
        } catch (e: EnableBluetoothAdapterException) {
            uiActionChannel.sendIn(
                MainUiAction.LaunchBluetoothAdapterEnableIntent(e.intent),
                viewModelScope
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun connectBluetoothDevice(device: BluetoothDevice) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                bluetoothRepository.connect(device)
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()

                uiActionChannel.sendIn(
                    MainUiAction.RequestMissingBluetoothPermissions(*e.permissions),
                    viewModelScope
                )
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()

                uiActionChannel.sendIn(
                    MainUiAction.ShowConnectionErrorMessage(e.bluetoothDevice ?: device),
                    viewModelScope
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnectBluetoothDevice(device: BluetoothDevice?) {
        try {
            bluetoothRepository.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            uiActionChannel.sendIn(
                MainUiAction.RequestMissingBluetoothPermissions(*e.permissions),
                viewModelScope
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showMessagesCount() {
        uiActionChannel.sendIn(
            MainUiAction.ShowMessagesCountMessage(
                inputMessagesCount = _uiState.value.inputMessagesCount,
                outputMessagesCount = _uiState.value.outputMessagesCount,
            ),
            viewModelScope
        )
    }

    fun showSendingErrorMessage() {
        uiActionChannel.sendIn(
            MainUiAction.ShowSendingErrorMessage(_uiState.value.favoriteBluetoothDevice),
            viewModelScope
        )
    }

    fun deleteWidget(widgetId: Int) {
        viewModelScope.launch {
            controllerRepository.deleteWidgetById(widgetId)

            uiActionChannel.send(MainUiAction.ShowWidgetDeletedMessage(widgetId))
        }
    }

    fun tryRestoreWidgetById(widgetId: Int) {
        viewModelScope.launch {
            controllerRepository.tryRestoreWidgetById(widgetId)
        }
    }

    fun deleteController(controllerId: Int) {
        viewModelScope.launch {
            controllerRepository.deleteControllerById(controllerId)

            uiActionChannel.send(MainUiAction.ShowControllerDeletedMessage(controllerId))
        }
    }

    fun tryRestoreControllerById(controllerId: Int) {
        viewModelScope.launch {
            controllerRepository.tryRestoreControllerById(controllerId)
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothRepository.getState()
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
        getFavoriteDeviceJob = bluetoothRepository.getFavoriteBluetoothDevice()
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

    private fun deleteMarkedAsDeletedControllersAndWidgets() {
        viewModelScope.launch {
            controllerRepository.deleteMarkedAsDeletedControllers()
            controllerRepository.deleteMarkedAsDeletedWidgets()
        }
    }
}