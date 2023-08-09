package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models

sealed class DeviceType(
    val connectionType: ConnectionType,
    val availableConnectionTypes: List<ConnectionType>,
) {
    class Classic(connectionType: ConnectionType.Classic) : DeviceType(
        connectionType,
        listOf(connectionType),
    )

    class Ble(connectionType: ConnectionType.Ble) : DeviceType(
        connectionType,
        listOf(connectionType),
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
            is Classic -> (connectionType as? ConnectionType.Classic)?.let { Classic(it) } ?: this
            is Ble -> (connectionType as? ConnectionType.Ble)?.let { Ble(it) } ?: this
            is Dual -> Dual(connectionType)
            is Unknown -> Unknown(connectionType)
        }
}