package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateCellTextUseCaseTest {

    private val useCase = UpdateCellTextUseCase()

    private fun twoRowState(): TableState = TableState(
        rows = listOf(
            RowModel("r0", listOf(
                CellModel("r0_c0", "old0"),
                CellModel("r0_c1", "old1"),
            )),
            RowModel("r1", listOf(
                CellModel("r1_c0", "old2"),
            )),
        ),
    )

    @Test
    fun `updates text of the target cell`() {
        val result = useCase(twoRowState(), "r0", "r0_c0", "new")
        assertEquals("new", result.rows[0].cells[0].text)
    }

    @Test
    fun `other cells in same row keep their text`() {
        val result = useCase(twoRowState(), "r0", "r0_c0", "new")
        assertEquals("old1", result.rows[0].cells[1].text)
    }

    @Test
    fun `cells in other rows keep their text`() {
        val result = useCase(twoRowState(), "r0", "r0_c0", "new")
        assertEquals("old2", result.rows[1].cells[0].text)
    }

    @Test
    fun `update to empty string is allowed`() {
        val result = useCase(twoRowState(), "r0", "r0_c0", "")
        assertEquals("", result.rows[0].cells[0].text)
    }

    @Test
    fun `unknown rowId returns state unchanged`() {
        val result = useCase(twoRowState(), "unknown", "r0_c0", "new")
        assertEquals("old0", result.rows[0].cells[0].text)
    }

    @Test
    fun `unknown cellId returns state unchanged`() {
        val result = useCase(twoRowState(), "r0", "unknown", "new")
        assertEquals("old0", result.rows[0].cells[0].text)
    }
}
