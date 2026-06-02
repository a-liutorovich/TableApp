package com.example.idttesttask.presentation.screen.table

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CellUiState(
    val id: String,
    val text: String,
    val isHighlighted: Boolean,
)

data class RowUiState(
    val id: String,
    val cells: ImmutableList<CellUiState>,
)

data class TableScreenUiState(
    val rows: ImmutableList<RowUiState> = persistentListOf(),
    val isLoading: Boolean = true,
)
