package com.android.maxclub.bluetoothme.di

import android.content.Context
import com.android.maxclub.bluetoothme.feature_bluetooth.data.repositories.BluetoothRepositoryImpl
import com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth.BluetoothAdapterManagerImpl
import com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth.BluetoothClassicService
import com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth.BluetoothDeviceServiceWithBleScanner
import com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth.BluetoothLeService
import com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.messages.MessagesLocalDataSource
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.BluetoothDeviceService
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages.MessagesDataSource
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothRepositoryModule {

    @Provides
    @Singleton
    fun provideBluetoothRepository(
        @ApplicationContext context: Context,
        bluetoothAdapterManager: BluetoothAdapterManager,
        bluetoothDeviceService: BluetoothDeviceService,
        @BluetoothClassic bluetoothClassicService: BluetoothService,
        @BluetoothLe bluetoothLeService: BluetoothService,
        messagesDataSource: MessagesDataSource,
    ): BluetoothRepository = BluetoothRepositoryImpl(
        context = context,
        bluetoothAdapterManager = bluetoothAdapterManager,
        bluetoothDeviceService = bluetoothDeviceService,
        bluetoothClassicService = bluetoothClassicService,
        bluetoothLeService = bluetoothLeService,
        messagesDataSource = messagesDataSource,
    )
}


@Module
@InstallIn(SingletonComponent::class)
object BluetoothAdapterManagerModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapterManager(
        @ApplicationContext context: Context
    ): BluetoothAdapterManager = BluetoothAdapterManagerImpl(context)
}


@Module
@InstallIn(SingletonComponent::class)
object BluetoothDeviceServiceModule {

    @Provides
    @Singleton
    fun provideBluetoothDeviceService(
        @ApplicationContext context: Context,
        bluetoothAdapterManager: BluetoothAdapterManager,
    ): BluetoothDeviceService = BluetoothDeviceServiceWithBleScanner(
        context = context,
        bluetoothAdapterManager = bluetoothAdapterManager,
    )
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BluetoothClassic

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BluetoothLe

@Module
@InstallIn(SingletonComponent::class)
object BluetoothServiceModule {

    @BluetoothClassic
    @Provides
    @Singleton
    fun provideBluetoothClassicService(
        @ApplicationContext context: Context,
        bluetoothAdapterManager: BluetoothAdapterManager,
        messagesDataSource: MessagesDataSource,
    ): BluetoothService = BluetoothClassicService(
        context = context,
        bluetoothAdapterManager = bluetoothAdapterManager,
        messagesDataSource = messagesDataSource,
    )


    @BluetoothLe
    @Provides
    @Singleton
    fun provideBluetoothLeService(
        @ApplicationContext context: Context,
        bluetoothAdapterManager: BluetoothAdapterManager,
        messagesDataSource: MessagesDataSource,
    ): BluetoothService = BluetoothLeService(
        context = context,
        bluetoothAdapterManager = bluetoothAdapterManager,
        messagesDataSource = messagesDataSource,
    )
}


@Module
@InstallIn(SingletonComponent::class)
object MessagesDataSourceModule {

    @Provides
    @Singleton
    fun provideMessagesDataSource(): MessagesDataSource = MessagesLocalDataSource()
}