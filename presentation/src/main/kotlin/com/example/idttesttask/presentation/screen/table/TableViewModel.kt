package com.example.idttesttask.presentation.screen.table

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idttesttask.domain.usecase.GenerateTableUseCase
import com.example.idttesttask.domain.usecase.GetTableStateUseCase
import com.example.idttesttask.domain.usecase.SaveTableStateUseCase
import com.example.idttesttask.domain.usecase.ToggleCellHighlightUseCase
import com.example.idttesttask.domain.usecase.UpdateCellTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TableViewModel(
    private val getTableState: GetTableStateUseCase,
    private val saveTableState: SaveTableStateUseCase,
    private val toggleCellHighlight: ToggleCellHighlightUseCase,
    private val updateCellText: UpdateCellTextUseCase,
    private val generateTable: GenerateTableUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val stateMutex = Mutex()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            if (getTableState.current().rows.isEmpty()) {
                val rows = savedStateHandle.get<Int>("rows") ?: return@launch
                val cols = savedStateHandle.get<Int>("cols") ?: return@launch
                generateTable(rows, cols)
            }
        }
    }

    val tableUiState: StateFlow<TableScreenUiState> = getTableState()
        .scan(TableScreenUiState(isLoading = true)) { prev, state ->
            if (state.rows.isEmpty()) return@scan TableScreenUiState(isLoading = true)
            val prevRowsById = prev.rows.associateBy { it.id }
            val newRows = state.rows.map { row ->
                val prevRow = prevRowsById[row.id]
                val prevCellsById = prevRow?.cells?.associateBy { it.id } ?: emptyMap()
                var rowChanged = false
                val newCells = row.cells.map { cell ->
                    val prevCell = prevCellsById[cell.id]
                    if (prevCell != null && prevCell.text == cell.text && prevCell.isHighlighted == cell.isHighlighted) {
                        prevCell
                    } else {
                        rowChanged = true
                        CellUiState(id = cell.id, text = cell.text, isHighlighted = cell.isHighlighted)
                    }
                }.toImmutableList()
                if (!rowChanged && prevRow != null) prevRow else RowUiState(id = row.id, cells = newCells)
            }.toImmutableList()
            TableScreenUiState(isLoading = false, rows = newRows)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TableScreenUiState(isLoading = true),
        )

    fun onCellClick(rowId: String, cellId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            stateMutex.withLock {
                val newState = toggleCellHighlight(getTableState.current(), rowId, cellId)
                saveTableState(newState)
            }
        }
    }

    fun onCellTextSaved(rowId: String, cellId: String, text: String) {
        viewModelScope.launch(Dispatchers.Default) {
            stateMutex.withLock {
                val newState = updateCellText(getTableState.current(), rowId, cellId, text)
                saveTableState(newState)
            }
        }
    }
}
