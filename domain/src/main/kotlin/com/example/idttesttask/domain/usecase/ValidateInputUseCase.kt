package com.example.idttesttask.domain.usecase

class ValidateInputUseCase {
    operator fun invoke(rows: Int, cols: Int): Boolean =
        rows in 1..1000 && cols in 1..6
}
