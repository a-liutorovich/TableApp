package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.model.mapCell

class ToggleCellHighlightUseCase {
    operator fun invoke(state: TableState, rowId: String, cellId: String): TableState =
        state.mapCell(rowId, cellId) { it.copy(isHighlighted = !it.isHighlighted) }
}
