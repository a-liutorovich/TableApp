package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState

class UpdateCellTextUseCase {
    operator fun invoke(state: TableState, rowId: String, cellId: String, text: String): TableState =
        state.copy(
            rows = state.rows.map { row ->
                if (row.id != rowId) row
                else row.copy(
                    cells = row.cells.map { cell ->
                        if (cell.id != cellId) cell
                        else cell.copy(text = text)
                    }
                )
            }
        )
}
