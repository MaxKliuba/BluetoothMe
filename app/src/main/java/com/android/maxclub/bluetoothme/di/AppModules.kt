package com.android.maxclub.bluetoothme.di

import android.content.Context
import com.android.maxclub.bluetoothme.data.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.data.repository.BluetoothRepositoryImpl
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
    @Singleton
    @Provides
    fun provideBluetoothRepository(
        @ApplicationContext context: Context,
        service: BluetoothService,
    ): BluetoothRepository =
        BluetoothRepositoryImpl(context, service)
}