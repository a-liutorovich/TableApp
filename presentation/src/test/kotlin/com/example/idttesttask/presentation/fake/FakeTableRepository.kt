package com.example.idttesttask.presentation.fake

import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTableRepository(
    initial: TableState = TableState(),
    private val generateException: Exception? = null,
) : TableRepository {

    private val _state = MutableStateFlow(initial)

    override fun observe(): Flow<TableState> = _state.asStateFlow()

    override fun get(): TableState = _state.value

    override suspend fun save(tableState: TableState) {
        _state.value = tableState
    }

    override suspend fun generate(rows: Int, cols: Int) {
        generateException?.let { throw it }
        _state.value = TableState(
            rows = List(rows) { r ->
                RowModel(
                    id = r.toString(),
                    cells = List(cols) { c ->
                        CellModel(id = "${r}_${c}", text = "cell_${r}_${c}")
                    },
                )
            },
        )
    }
}
