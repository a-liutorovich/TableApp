package com.example.idttesttask.presentation.screen.table

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import com.example.idttesttask.domain.usecase.GenerateTableUseCase
import com.example.idttesttask.domain.usecase.GetTableStateUseCase
import com.example.idttesttask.domain.usecase.SaveTableStateUseCase
import com.example.idttesttask.domain.usecase.ToggleCellHighlightUseCase
import com.example.idttesttask.domain.usecase.UpdateCellTextUseCase
import com.example.idttesttask.presentation.fake.FakeTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TableViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTableRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        initial: TableState = TableState(),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): TableViewModel {
        repository = FakeTableRepository(initial)
        return TableViewModel(
            getTableState = GetTableStateUseCase(repository),
            saveTableState = SaveTableStateUseCase(repository),
            toggleCellHighlight = ToggleCellHighlightUseCase(),
            updateCellText = UpdateCellTextUseCase(),
            generateTable = GenerateTableUseCase(repository),
            savedStateHandle = savedStateHandle,
        )
    }

    // Helpers — DRY builders used across multiple tests.
    private fun singleCellState(text: String = "t", isHighlighted: Boolean = false) = TableState(
        rows = listOf(RowModel("r0", listOf(CellModel("r0_c0", text, isHighlighted)))),
    )

    @Test
    fun `empty state shows loading`() = runTest(dispatcher) {
        buildViewModel(TableState()).tableUiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertTrue(state.rows.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `non-empty state maps rows and cells correctly`() = runTest(dispatcher) {
        val initial = TableState(
            rows = listOf(RowModel("r0", listOf(CellModel("r0_c0", "hello", false)))),
        )
        buildViewModel(initial).tableUiState.test {
            skipItems(1) // initial isLoading=true
            val state = awaitItem() // mapped loaded state
            assertFalse(state.isLoading)
            assertEquals(1, state.rows.size)
            assertEquals("r0", state.rows[0].id)
            assertEquals("r0_c0", state.rows[0].cells[0].id)
            assertEquals("hello", state.rows[0].cells[0].text)
            assertFalse(state.rows[0].cells[0].isHighlighted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCellClick toggles highlight of target cell`() = runTest(dispatcher) {
        val vm = buildViewModel(singleCellState())
        vm.tableUiState.test {
            skipItems(2) // isLoading=true + initial loaded state
            vm.onCellClick("r0", "r0_c0")
            val state = awaitItem()
            assertTrue(state.rows[0].cells[0].isHighlighted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCellClick does not affect other cells`() = runTest(dispatcher) {
        val initial = TableState(
            rows = listOf(
                RowModel("r0", listOf(CellModel("r0_c0", "t", false), CellModel("r0_c1", "t", false))),
            ),
        )
        val vm = buildViewModel(initial)
        vm.tableUiState.test {
            skipItems(2)
            vm.onCellClick("r0", "r0_c0")
            val state = awaitItem()
            assertTrue(state.rows[0].cells[0].isHighlighted)
            assertFalse(state.rows[0].cells[1].isHighlighted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCellTextSaved updates text of target cell`() = runTest(dispatcher) {
        val vm = buildViewModel(singleCellState(text = "old"))
        vm.tableUiState.test {
            skipItems(2)
            vm.onCellTextSaved("r0", "r0_c0", "new")
            val state = awaitItem()
            assertEquals("new", state.rows[0].cells[0].text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCellTextSaved does not change highlight state`() = runTest(dispatcher) {
        val vm = buildViewModel(singleCellState(text = "old", isHighlighted = true))
        vm.tableUiState.test {
            skipItems(2)
            vm.onCellTextSaved("r0", "r0_c0", "new")
            val state = awaitItem()
            assertTrue(state.rows[0].cells[0].isHighlighted)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
