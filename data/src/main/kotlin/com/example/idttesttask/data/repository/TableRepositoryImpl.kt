package com.example.idttesttask.data.repository

import com.example.idttesttask.data.datasource.RandomStringGenerator
import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.ITableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class TableRepositoryImpl(
    private val generator: RandomStringGenerator,
) : ITableRepository {

    private val _state = MutableStateFlow(TableState())

    override fun observe(): Flow<TableState> = _state.asStateFlow()

    override fun get(): TableState = _state.value

    override suspend fun save(tableState: TableState) {
        _state.value = tableState
    }

    override suspend fun generate(rows: Int, cols: Int) {
        val newState = withContext(Dispatchers.Default) {
            TableState(
                rows = List(rows) { rowIndex ->
                    RowModel(
                        id = rowIndex.toString(),
                        cells = List(cols) { colIndex ->
                            CellModel(
                                id = "${rowIndex}_${colIndex}",
                                text = generator.generate(),
                            )
                        }
                    )
                }
            )
        }
        _state.value = newState
    }
}
