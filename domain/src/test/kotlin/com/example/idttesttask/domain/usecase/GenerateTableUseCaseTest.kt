package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.fake.FakeTableRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GenerateTableUseCaseTest {

    private val repository = FakeTableRepository()
    private val useCase = GenerateTableUseCase(repository)

    @Test
    fun `delegates to repository with correct dimensions`() = runTest {
        useCase(5, 3)
        assertEquals(1, repository.generateCallCount)
        assertEquals(5, repository.lastGeneratedRows)
        assertEquals(3, repository.lastGeneratedCols)
    }

    @Test
    fun `generated state has correct row and cell count`() = runTest {
        useCase(4, 2)
        val state = repository.get()
        assertEquals(4, state.rows.size)
        state.rows.forEach { row -> assertEquals(2, row.cells.size) }
    }
}
