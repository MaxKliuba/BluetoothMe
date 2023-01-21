package com.android.maxclub.bluetoothme.di

import android.content.Context
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothAdapterStateObserver
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothClassicService
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothDeviceServiceWithBleScanner
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothLeService
import com.android.maxclub.bluetoothme.data.messages.MessagesLocalDataSource
import com.android.maxclub.bluetoothme.data.repository.BluetoothRepositoryImpl
import com.android.maxclub.bluetoothme.domain.messages.MessagesDataSource
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothRepositoryModule {
    @Provides
    @Singleton
    fun provideBluetoothRepository(
        @ApplicationContext context: Context,
        messagesDataSource: MessagesDataSource,
    ): BluetoothRepository =
        BluetoothRepositoryImpl(
            context = context,
            bluetoothAdapterStateObserver = BluetoothAdapterStateObserver(context),
            bluetoothDeviceService = BluetoothDeviceServiceWithBleScanner(context),
            bluetoothClassicService = BluetoothClassicService(context, messagesDataSource),
            bluetoothLeService = BluetoothLeService(context, messagesDataSource),
            messagesDataSource = messagesDataSource,
        )
}

@Module
@InstallIn(SingletonComponent::class)
object MessagesDataSourceModule {
    @Provides
    @Singleton
    fun provideMessagesDataSource(): MessagesDataSource =
        MessagesLocalDataSource()
}