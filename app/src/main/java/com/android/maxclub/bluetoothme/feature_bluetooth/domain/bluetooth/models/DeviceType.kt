package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models

sealed class DeviceType(
    val connectionType: ConnectionType,
    val availableConnectionTypes: List<ConnectionType>,
) {
    object Classic : DeviceType(
        ConnectionType.Classic,
        listOf(ConnectionType.Classic),
    )

    object Ble : DeviceType(
        ConnectionType.Ble(),
        listOf(ConnectionType.Ble()),
    )

    class Dual(connectionType: ConnectionType = ConnectionType.Classic) : DeviceType(
        connectionType,
        listOf(ConnectionType.Classic, ConnectionType.Ble()),
    )

    class Unknown(connectionType: ConnectionType = ConnectionType.Classic) : DeviceType(
        connectionType,
        listOf(ConnectionType.Classic, ConnectionType.Ble())
    )

    fun copy(connectionType: ConnectionType = this.connectionType) =
        when (this) {
            Classic, Ble -> this
            is Dual -> Dual(connectionType)
            is Unknown -> Unknown(connectionType)
        }
}