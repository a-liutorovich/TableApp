package com.example.idttesttask.domain.repository

import com.example.idttesttask.domain.model.TableState
import kotlinx.coroutines.flow.Flow

interface ITableRepository {
    fun observe(): Flow<TableState>
    fun get(): TableState
    suspend fun save(tableState: TableState)
    suspend fun generate(rows: Int, cols: Int)
}
