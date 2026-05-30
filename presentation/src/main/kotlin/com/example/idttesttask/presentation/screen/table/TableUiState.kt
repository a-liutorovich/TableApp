package com.example.idttesttask.presentation.screen.table

import androidx.compose.runtime.Immutable

@Immutable
data class CellUiState(
    val id: String,
    val text: String,
    val isHighlighted: Boolean,
)

@Immutable
data class RowUiState(
    val id: String,
    val cells: List<CellUiState>,
)

data class TableScreenUiState(
    val rows: List<RowUiState> = emptyList(),
    val isLoading: Boolean = true,
)
