package com.example.idttesttask.domain.model

data class TableState(
    val rows: List<RowModel> = emptyList(),
)

fun TableState.mapCell(
    rowId: String,
    cellId: String,
    transform: (CellModel) -> CellModel,
): TableState {
    val rowIndex = rows.indexOfFirst { it.id == rowId }
    if (rowIndex == -1) return this
    val row = rows[rowIndex]
    val cellIndex = row.cells.indexOfFirst { it.id == cellId }
    if (cellIndex == -1) return this
    val newCells = row.cells.toMutableList().also { it[cellIndex] = transform(it[cellIndex]) }
    val newRows = rows.toMutableList().also { it[rowIndex] = row.copy(cells = newCells) }
    return copy(rows = newRows)
}
