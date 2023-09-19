package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.util.debounce
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.toWidgetClass
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import com.android.maxclub.bluetoothme.feature.controllers.domain.validators.MessageTagValidator
import com.android.maxclub.bluetoothme.feature.controllers.domain.validators.WidgetTitleValidator
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controllerRepository: ControllerRepository,
    private val widgetTitleValidator: WidgetTitleValidator,
    private val messageTagValidator: MessageTagValidator,
) : ViewModel() {
    private val id: Int = savedStateHandle[Screen.AddEditWidget.ARG_ID]
        ?: Screen.AddEditWidget.DEFAULT_ID
    private val isNew: Boolean = savedStateHandle[Screen.AddEditWidget.ARG_IS_NEW]
        ?: Screen.AddEditWidget.DEFAULT_IS_NEW
    private val columnsCount: Int = savedStateHandle[Screen.AddEditWidget.ARG_COLUMNS_COUNT]
        ?: Screen.AddEditWidget.DEFAULT_COLUMNS_COUNT

    private val _uiState = mutableStateOf<AddEditWidgetUiState>(AddEditWidgetUiState.Loading)
    val uiState: State<AddEditWidgetUiState> = _uiState

    private var getWidgetsJob: Job? = null

    private val onUpdateWidgetTitleWithDebounce: (Widget<*>, String) -> Unit =
        viewModelScope.debounce { widget, newTitle ->
            if (widget.title != newTitle) {
                controllerRepository.updateWidgets(widget.copy(title = newTitle))
            }
        }

    private val onUpdateWidgetTagWithDebounce: (Widget<*>, String) -> Unit =
        viewModelScope.debounce { widget, newTag ->
            if (widget.messageTag != newTag) {
                controllerRepository.updateWidgets(widget.copy(messageTag = newTag))
            }
        }

    private val onUpdateWidgetSliderParamsWithDebounce: (Widget.Slider, Triple<Int, Int, Int>) -> Unit =
        viewModelScope.debounce { widget, params ->
            controllerRepository.updateWidgets(
                widget.copySliderParams(
                    minValue = params.first,
                    maxValue = params.second,
                    step = params.third,
                )
            )
        }

    init {
        (_uiState.value as? AddEditWidgetUiState.Loading)?.let {
            getWidget()
        }
    }

    fun updateWidgetSize(widget: Widget<*>, newSize: WidgetSize) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.copy(size = newSize))
        }
    }

    fun updateWidgetEnable(widget: Widget<*>, enabled: Boolean) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.copy(enabled = enabled))
        }
    }

    fun updateWidgetType(widget: Widget<*>, newWidgetType: WidgetType) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.asType(newWidgetType.toWidgetClass()))
        }
    }

    fun tryUpdateWidgetTitle(value: TextFieldValue): Boolean =
        if (widgetTitleValidator(value.text)) {
            (_uiState.value as? AddEditWidgetUiState.Success)?.let { state ->
                onUpdateWidgetTitleWithDebounce(state.widget, value.text)
                _uiState.update { state.copy(widgetTitle = value) }
            }
            true
        } else {
            false
        }

    fun tryUpdateWidgetTag(value: TextFieldValue): Boolean =
        if (messageTagValidator(value.text)) {
            (_uiState.value as? AddEditWidgetUiState.Success)?.let { state ->
                onUpdateWidgetTagWithDebounce(state.widget, value.text)
                _uiState.update { state.copy(widgetTag = value) }
            }
            true
        } else {
            false
        }

    fun updateWidgetIcon(widget: Widget<*>, newWidgetIcon: WidgetIcon) {
        viewModelScope.launch {
            controllerRepository.updateWidgets(widget.copy(icon = newWidgetIcon))
        }
    }

    fun updateSliderWidgetRange(sliderWidget: Widget.Slider, minValue: Int, maxValue: Int) {
        viewModelScope.launch {
            val maxStep = maxValue - minValue
            val step = if (sliderWidget.step > maxStep) maxStep else sliderWidget.step

            onUpdateWidgetSliderParamsWithDebounce(sliderWidget, Triple(minValue, maxValue, step))
        }
    }

    fun updateSliderWidgetStep(sliderWidget: Widget.Slider, step: Int) {
        viewModelScope.launch {
            onUpdateWidgetSliderParamsWithDebounce(
                sliderWidget, Triple(sliderWidget.minValue, sliderWidget.maxValue, step)
            )
        }
    }

    private fun getWidget() {
        getWidgetsJob?.cancel()
        getWidgetsJob = viewModelScope.launch {
            val widgetId = if (isNew) {
                val widget = Widget.Empty(controllerId = id)
                controllerRepository.addWidget(widget)
            } else {
                id
            }

            controllerRepository.getWidgetById(widgetId)
                .onStart {
                    _uiState.update { AddEditWidgetUiState.Loading }
                }
                .onEach { widget ->
                    val widgetTitle = widget.title
                    val widgetTag = widget.messageTag

                    _uiState.update {
                        if (it is AddEditWidgetUiState.Success) {
                            it.copy(
                                widget = widget,
                                columnsCount = columnsCount,
                                widgetTitle = if (it.widgetTitle.text == widgetTitle) {
                                    it.widgetTitle
                                } else {
                                    TextFieldValue(widgetTitle, TextRange(widgetTitle.length))
                                },
                                widgetTag = if (it.widgetTag.text == widgetTag) {
                                    it.widgetTag
                                } else {
                                    TextFieldValue(widgetTag, TextRange(widgetTag.length))
                                },
                            )
                        } else {
                            AddEditWidgetUiState.Success(
                                widget = widget,
                                columnsCount = columnsCount,
                                widgetTitle = TextFieldValue(
                                    widgetTitle,
                                    TextRange(widgetTitle.length)
                                ),
                                widgetTag = TextFieldValue(widgetTag, TextRange(widgetTag.length))
                            )
                        }
                    }
                }
                .catch { it.printStackTrace() }
                .launchIn(this)
        }
    }
}