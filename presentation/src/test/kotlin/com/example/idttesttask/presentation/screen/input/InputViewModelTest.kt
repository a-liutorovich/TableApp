package com.example.idttesttask.presentation.screen.input

import com.example.idttesttask.domain.usecase.GenerateTableUseCase
import com.example.idttesttask.presentation.fake.FakeTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import app.cash.turbine.test
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InputViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: InputViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = InputViewModel(
            generateTable = GenerateTableUseCase(FakeTableRepository()),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty with no errors`() = runTest(dispatcher) {
        val state = viewModel.state.value
        assertEquals("", state.rowsInput)
        assertEquals("", state.colsInput)
        assertNull(state.rowsError)
        assertNull(state.colsError)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `onRowsInputChange updates rowsInput and clears rowsError`() = runTest(dispatcher) {
        viewModel.onGenerateClick()
        assertNotNull(viewModel.state.value.rowsError)

        viewModel.onRowsInputChange("5")
        assertEquals("5", viewModel.state.value.rowsInput)
        assertNull(viewModel.state.value.rowsError)
    }

    @Test
    fun `onColsInputChange updates colsInput and clears colsError`() = runTest(dispatcher) {
        viewModel.onGenerateClick()
        assertNotNull(viewModel.state.value.colsError)

        viewModel.onColsInputChange("3")
        assertEquals("3", viewModel.state.value.colsInput)
        assertNull(viewModel.state.value.colsError)
    }

    @Test
    fun `onGenerateClick with empty input sets both errors`() = runTest(dispatcher) {
        viewModel.onGenerateClick()
        assertEquals(InputFieldError.RowsOutOfRange, viewModel.state.value.rowsError)
        assertEquals(InputFieldError.ColsOutOfRange, viewModel.state.value.colsError)
    }

    @Test
    fun `onGenerateClick with invalid rows sets only rowsError`() = runTest(dispatcher) {
        viewModel.onRowsInputChange("9999")
        viewModel.onColsInputChange("3")
        viewModel.onGenerateClick()
        assertNotNull(viewModel.state.value.rowsError)
        assertNull(viewModel.state.value.colsError)
    }

    @Test
    fun `onGenerateClick with invalid cols sets only colsError`() = runTest(dispatcher) {
        viewModel.onRowsInputChange("10")
        viewModel.onColsInputChange("99")
        viewModel.onGenerateClick()
        assertNull(viewModel.state.value.rowsError)
        assertNotNull(viewModel.state.value.colsError)
    }

    @Test
    fun `onGenerateClick with valid input emits navigation event`() = runTest(dispatcher) {
        viewModel.onRowsInputChange("10")
        viewModel.onColsInputChange("4")
        viewModel.navigationEvent.test {
            viewModel.onGenerateClick()
            val event = awaitItem()
            assertEquals(10, event.first)
            assertEquals(4, event.second)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGenerateClick with valid input finishes with isLoading false`() = runTest(dispatcher) {
        viewModel.onRowsInputChange("10")
        viewModel.onColsInputChange("4")
        viewModel.onGenerateClick()
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `onGenerateClick with failing generation sets generationError and clears isLoading`() = runTest(dispatcher) {
        val failingViewModel = InputViewModel(
            generateTable = GenerateTableUseCase(
                FakeTableRepository(generateException = RuntimeException("generation failed"))
            ),
        )
        failingViewModel.onRowsInputChange("10")
        failingViewModel.onColsInputChange("4")
        failingViewModel.onGenerateClick()
        assertTrue(failingViewModel.state.value.generationError)
        assertTrue(!failingViewModel.state.value.isLoading)
    }

    @Test
    fun `onRowsInputChange clears generationError`() = runTest(dispatcher) {
        val failingViewModel = InputViewModel(
            generateTable = GenerateTableUseCase(
                FakeTableRepository(generateException = RuntimeException("generation failed"))
            ),
        )
        failingViewModel.onRowsInputChange("10")
        failingViewModel.onColsInputChange("4")
        failingViewModel.onGenerateClick()
        assertTrue(failingViewModel.state.value.generationError)

        failingViewModel.onRowsInputChange("5")
        assertTrue(!failingViewModel.state.value.generationError)
    }

    @Test
    fun `onColsInputChange clears generationError`() = runTest(dispatcher) {
        val failingViewModel = InputViewModel(
            generateTable = GenerateTableUseCase(
                FakeTableRepository(generateException = RuntimeException("generation failed"))
            ),
        )
        failingViewModel.onRowsInputChange("10")
        failingViewModel.onColsInputChange("4")
        failingViewModel.onGenerateClick()
        assertTrue(failingViewModel.state.value.generationError)

        failingViewModel.onColsInputChange("3")
        assertTrue(!failingViewModel.state.value.generationError)
    }
}
