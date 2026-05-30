package com.example.idttesttask.presentation.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idttesttask.domain.usecase.GenerateTableUseCase
import com.example.idttesttask.domain.usecase.ValidateInputUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InputViewModel(
    private val validateInput: ValidateInputUseCase,
    private val generateTable: GenerateTableUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(InputUiState())
    val state: StateFlow<InputUiState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Pair<Int, Int>>()
    val navigationEvent: SharedFlow<Pair<Int, Int>> = _navigationEvent.asSharedFlow()

    fun onRowsInputChange(value: String) {
        _state.update { it.copy(rowsInput = value, rowsError = null) }
    }

    fun onColsInputChange(value: String) {
        _state.update { it.copy(colsInput = value, colsError = null) }
    }

    fun onGenerateClick() {
        val rows = _state.value.rowsInput.toIntOrNull() ?: 0
        val cols = _state.value.colsInput.toIntOrNull() ?: 0

        val rowsError = if (rows !in 1..1000) InputFieldError.RowsOutOfRange else null
        val colsError = if (cols !in 1..6) InputFieldError.ColsOutOfRange else null

        if (rowsError != null || colsError != null) {
            _state.update { it.copy(rowsError = rowsError, colsError = colsError) }
            return
        }

        if (!validateInput(rows, cols)) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            generateTable(rows, cols)
            _state.update { it.copy(isLoading = false) }
            _navigationEvent.emit(rows to cols)
        }
    }
}
