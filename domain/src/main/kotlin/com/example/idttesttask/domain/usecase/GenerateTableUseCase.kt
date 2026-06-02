package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.repository.TableRepository

class GenerateTableUseCase(private val repository: TableRepository) {
    suspend operator fun invoke(rows: Int, cols: Int) = repository.generate(rows, cols)
}
