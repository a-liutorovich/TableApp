package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.TableRepository

class SaveTableStateUseCase(private val repository: TableRepository) {
    suspend operator fun invoke(tableState: TableState) = repository.save(tableState)
}
