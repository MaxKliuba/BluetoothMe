package com.android.maxclub.bluetoothme.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothDeviceService
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothPermission
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothScanPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class BluetoothDeviceServiceWithBleScanner(private val context: Context) : BluetoothDeviceService {

    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter = manager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner?
        get() = adapter.bluetoothLeScanner

    private val scannedDevices: MutableStateFlow<List<BluetoothDevice>> =
        MutableStateFlow(emptyList())

    private val scanStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val bleScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (!scannedDevices.value.contains(it.device)) {
                    scannedDevices.value += it.device
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            scanStateFlow.value = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun getBondedDevices(): Flow<List<BluetoothDevice>> = flow {
        withCheckSelfBluetoothPermission(context) {
            emit(adapter.bondedDevices.toList())
        }
    }

    override fun getScannedDevices(): Flow<List<BluetoothDevice>> = scannedDevices

    override fun getScanState(): StateFlow<Boolean> = scanStateFlow

    @SuppressLint("MissingPermission")
    override suspend fun startScan(duration: Long) {
        withCheckSelfBluetoothScanPermission(context) {
            scannedDevices.value = emptyList()

            if (adapter.isEnabled) {
                bluetoothLeScanner?.startScan(bleScanCallback)
                scanStateFlow.value = true
                delay(duration * 1000)
            }

            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        withCheckSelfBluetoothScanPermission(context) {
            if (adapter.isEnabled) {
                bluetoothLeScanner?.stopScan(bleScanCallback)
                adapter.cancelDiscovery()
            }
            scanStateFlow.value = false
        }
    }
}