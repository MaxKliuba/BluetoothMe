package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

data class ControllerWithWidgets(
    val controller: Controller,
    val widgets: List<Widget<*>>,
)