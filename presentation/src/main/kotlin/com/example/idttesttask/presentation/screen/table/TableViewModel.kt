package com.example.idttesttask.presentation.screen.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idttesttask.domain.usecase.GetTableStateUseCase
import com.example.idttesttask.domain.usecase.SaveTableStateUseCase
import com.example.idttesttask.domain.usecase.ToggleCellHighlightUseCase
import com.example.idttesttask.domain.usecase.UpdateCellTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableViewModel(
    private val getTableState: GetTableStateUseCase,
    private val saveTableState: SaveTableStateUseCase,
    private val toggleCellHighlight: ToggleCellHighlightUseCase,
    private val updateCellText: UpdateCellTextUseCase,
) : ViewModel() {

    val tableUiState: StateFlow<TableScreenUiState> = getTableState()
        .map { state ->
            if (state.rows.isEmpty()) {
                TableScreenUiState(isLoading = true)
            } else {
                TableScreenUiState(
                    isLoading = false,
                    rows = state.rows.map { row ->
                        RowUiState(
                            id = row.id,
                            cells = row.cells.map { cell ->
                                CellUiState(
                                    id = cell.id,
                                    text = cell.text,
                                    isHighlighted = cell.isHighlighted,
                                )
                            }
                        )
                    }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TableScreenUiState(isLoading = true),
        )

    fun onCellClick(rowId: String, cellId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val newState = toggleCellHighlight(getTableState.current(), rowId, cellId)
            saveTableState(newState)
        }
    }

    fun onCellTextSaved(rowId: String, cellId: String, text: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val newState = updateCellText(getTableState.current(), rowId, cellId, text)
            saveTableState(newState)
        }
    }
}
