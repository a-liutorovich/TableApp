package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.repository.ITableRepository

class GenerateTableUseCase(private val repository: ITableRepository) {
    suspend operator fun invoke(rows: Int, cols: Int) = repository.generate(rows, cols)
}
