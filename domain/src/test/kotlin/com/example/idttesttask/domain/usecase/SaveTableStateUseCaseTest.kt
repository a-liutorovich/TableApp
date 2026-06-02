package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.fake.FakeTableRepository
import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SaveTableStateUseCaseTest {

    private val repository = FakeTableRepository()
    private val useCase = SaveTableStateUseCase(repository)

    @Test
    fun `saved state is immediately readable via get`() = runTest {
        val state = TableState(
            rows = listOf(RowModel("r0", listOf(CellModel("r0_c0", "hello")))),
        )
        useCase(state)
        assertEquals(state, repository.get())
    }

    @Test
    fun `saving empty state clears previous state`() = runTest {
        val filled = TableState(rows = listOf(RowModel("r0", listOf(CellModel("r0_c0", "x")))))
        useCase(filled)
        val empty = TableState()
        useCase(empty)
        assertEquals(empty, repository.get())
    }
}
