package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.fake.FakeTableRepository
import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTableStateUseCaseTest {

    private val initial = TableState(
        rows = listOf(RowModel("r0", listOf(CellModel("r0_c0", "text")))),
    )
    private val repository = FakeTableRepository(initial)
    private val useCase = GetTableStateUseCase(repository)

    @Test
    fun `invoke returns flow with current state`() = runTest {
        val emitted = useCase().first()
        assertEquals(initial, emitted)
    }

    @Test
    fun `current returns synchronous state`() {
        assertEquals(initial, useCase.current())
    }

    @Test
    fun `flow emits updated state after save`() = runTest {
        val newState = TableState()
        repository.save(newState)
        val emitted = useCase().first()
        assertEquals(newState, emitted)
    }
}
