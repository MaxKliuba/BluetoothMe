package com.tech.maxclub.bluetoothme.di

import android.content.Context
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.repositories.BluetoothRepositoryImpl
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth.BluetoothAdapterManagerImpl
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth.BluetoothClassicService
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth.BluetoothDeviceServiceWithBleScanner
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth.BluetoothLeService
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.messages.MessagesTempDataSource
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothDeviceService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessagesDataSource
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.ControllerDao
import com.tech.maxclub.bluetoothme.feature.controllers.data.local.ControllerDatabase
import com.tech.maxclub.bluetoothme.feature.controllers.data.repositories.ControllerRepositoryImpl
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        bluetoothRepository: BluetoothRepositoryImpl
    ): BluetoothRepository
}


@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothAdapterManagerModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothAdapterManager(
        bluetoothAdapterManager: BluetoothAdapterManagerImpl
    ): BluetoothAdapterManager
}


@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothDeviceServiceModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothDeviceService(
        bluetoothDeviceService: BluetoothDeviceServiceWithBleScanner
    ): BluetoothDeviceService
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BluetoothClassic

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BluetoothLe

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothServiceModule {

    @BluetoothClassic
    @Binds
    @Singleton
    abstract fun bindBluetoothClassicService(
        bluetoothService: BluetoothClassicService
    ): BluetoothService

    @BluetoothLe
    @Binds
    @Singleton
    abstract fun bindBluetoothLeService(
        bluetoothService: BluetoothLeService
    ): BluetoothService
}


@Module
@InstallIn(SingletonComponent::class)
abstract class MessagesDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindMessagesDataSource(
        messagesDataSource: MessagesTempDataSource
    ): MessagesDataSource
}


@Module
@InstallIn(SingletonComponent::class)
abstract class ControllerRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindControllerRepository(
        controllerRepository: ControllerRepositoryImpl
    ): ControllerRepository
}


@Module
@InstallIn(SingletonComponent::class)
object ControllerDatabaseModule {

    @Provides
    @Singleton
    fun provideControllerDatabase(@ApplicationContext context: Context): ControllerDatabase =
        ControllerDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideControllerDao(controllerDatabase: ControllerDatabase): ControllerDao =
        controllerDatabase.controllerDao
}