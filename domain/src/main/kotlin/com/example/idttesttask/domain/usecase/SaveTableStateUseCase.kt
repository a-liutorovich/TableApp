package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.repository.ITableRepository

class SaveTableStateUseCase(private val repository: ITableRepository) {
    suspend operator fun invoke(tableState: TableState) = repository.save(tableState)
}
