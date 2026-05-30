package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.ITableRepository
import kotlinx.coroutines.flow.Flow

class GetTableStateUseCase(private val repository: ITableRepository) {
    operator fun invoke(): Flow<TableState> = repository.observe()
    fun current(): TableState = repository.get()
}
