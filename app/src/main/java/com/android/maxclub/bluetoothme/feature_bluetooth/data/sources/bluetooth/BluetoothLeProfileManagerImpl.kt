package com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.BluetoothLeProfileManager
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothLeProfile
import java.util.*

class CustomProfileManager private constructor(
    gattService: BluetoothGattService,
    private val profile: BluetoothLeProfile.Custom,
) : BluetoothLeProfileManager(gattService) {

    private var _readCharacteristic: BluetoothGattCharacteristic? = null
    private var _writeCharacteristic: BluetoothGattCharacteristic? = null

    override val readCharacteristic: BluetoothGattCharacteristic?
        get() = _readCharacteristic

    override val writeCharacteristic: BluetoothGattCharacteristic?
        get() = _writeCharacteristic

    override fun connectCharacteristics(): Boolean {
        _readCharacteristic = gattService.getCharacteristic(profile.readCharacteristicUuid)
        _writeCharacteristic = gattService.getCharacteristic(profile.writeCharacteristicUuid)

        return _readCharacteristic != null && _writeCharacteristic != null
    }

    companion object {
        fun fromService(
            gattService: BluetoothGattService,
            profile: BluetoothLeProfile.Custom,
        ): CustomProfileManager? =
            if (gattService.uuid == profile.serviceUuid) {
                CustomProfileManager(gattService, profile)
            } else {
                null
            }
    }
}

class CC254XProfileManager private constructor(gattService: BluetoothGattService) :
    BluetoothLeProfileManager(gattService) {

    private var readWriteCharacteristic: BluetoothGattCharacteristic? = null

    override val readCharacteristic: BluetoothGattCharacteristic?
        get() = readWriteCharacteristic

    override val writeCharacteristic: BluetoothGattCharacteristic?
        get() = readWriteCharacteristic

    override fun connectCharacteristics(): Boolean {
        readWriteCharacteristic = gattService.getCharacteristic(readWriteCharacteristicUuid)

        return readWriteCharacteristic != null
    }

    companion object {
        fun fromService(gattService: BluetoothGattService): CC254XProfileManager? =
            if (gattService.uuid == serviceUuid) {
                CC254XProfileManager(gattService)
            } else {
                null
            }

        private val serviceUuid: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        private val readWriteCharacteristicUuid: UUID =
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    }
}

class RN4870ProfileManager private constructor(gattService: BluetoothGattService) :
    BluetoothLeProfileManager(gattService) {

    private var readWriteCharacteristic: BluetoothGattCharacteristic? = null

    override val readCharacteristic: BluetoothGattCharacteristic?
        get() = readWriteCharacteristic

    override val writeCharacteristic: BluetoothGattCharacteristic?
        get() = readWriteCharacteristic

    override fun connectCharacteristics(): Boolean {
        readWriteCharacteristic = gattService.getCharacteristic(readWriteCharacteristicUuid)

        return readWriteCharacteristic != null
    }

    companion object {
        fun fromService(gattService: BluetoothGattService): RN4870ProfileManager? =
            if (gattService.uuid == serviceUuid) {
                RN4870ProfileManager(gattService)
            } else {
                null
            }

        private val serviceUuid: UUID = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455")
        private val readWriteCharacteristicUuid: UUID =
            UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616")
    }
}

class NRFProfileManager private constructor(gattService: BluetoothGattService) :
    BluetoothLeProfileManager(gattService) {

    private var _readCharacteristic: BluetoothGattCharacteristic? = null
    private var _writeCharacteristic: BluetoothGattCharacteristic? = null

    override val readCharacteristic: BluetoothGattCharacteristic?
        get() = _readCharacteristic

    override val writeCharacteristic: BluetoothGattCharacteristic?
        get() = _writeCharacteristic

    override fun connectCharacteristics(): Boolean {
        val readWrite2Characteristic =
            gattService.getCharacteristic(readWrite2CharacteristicUuid) ?: return false
        val readWrite3Characteristic =
            gattService.getCharacteristic(readWrite3CharacteristicUuid) ?: return false

        val propertyWrite2Flag =
            (readWrite2Characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
        val propertyWrite3Flag =
            (readWrite3Characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0

        return when {
            propertyWrite2Flag && propertyWrite3Flag -> false
            propertyWrite2Flag -> {
                _readCharacteristic = readWrite3Characteristic
                _writeCharacteristic = readWrite2Characteristic
                true
            }
            propertyWrite3Flag -> {
                _readCharacteristic = readWrite2Characteristic
                _writeCharacteristic = readWrite3Characteristic
                true
            }
            else -> false
        }
    }

    companion object {
        fun fromService(gattService: BluetoothGattService): NRFProfileManager? =
            if (gattService.uuid == serviceUuid) {
                NRFProfileManager(gattService)
            } else {
                null
            }

        private val serviceUuid: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        private val readWrite2CharacteristicUuid: UUID =
            UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        private val readWrite3CharacteristicUuid: UUID =
            UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    }
}