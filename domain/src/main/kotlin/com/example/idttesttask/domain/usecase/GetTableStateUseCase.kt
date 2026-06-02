package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow

class GetTableStateUseCase(private val repository: TableRepository) {
    operator fun invoke(): Flow<TableState> = repository.observe()
    fun current(): TableState = repository.get()
}
