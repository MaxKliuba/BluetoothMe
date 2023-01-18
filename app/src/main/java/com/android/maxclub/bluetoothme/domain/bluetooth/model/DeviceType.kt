package com.android.maxclub.bluetoothme.domain.bluetooth.model

sealed class DeviceType(
    val connectionType: ConnectionType,
    val availableConnectionTypes: List<ConnectionType>,
) {
    object Classic : DeviceType(
        ConnectionType.Classic,
        listOf(ConnectionType.Classic),
    )

    object Ble : DeviceType(
        ConnectionType.Ble,
        listOf(ConnectionType.Ble),
    )

    class Dual(connectionType: ConnectionType = ConnectionType.Classic) :
        DeviceType(
            connectionType,
            listOf(ConnectionType.Classic, ConnectionType.Ble),
        )

    class Unknown(connectionType: ConnectionType = ConnectionType.Classic) :
        DeviceType(
            connectionType,
            listOf(ConnectionType.Classic, ConnectionType.Ble)
        )
}