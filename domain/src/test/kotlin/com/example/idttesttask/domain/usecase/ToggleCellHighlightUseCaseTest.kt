package com.example.idttesttask.domain.usecase

import com.example.idttesttask.domain.model.CellModel
import com.example.idttesttask.domain.model.RowModel
import com.example.idttesttask.domain.model.TableState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleCellHighlightUseCaseTest {

    private val useCase = ToggleCellHighlightUseCase()

    private fun tableState(vararg rows: List<Pair<String, Boolean>>): TableState =
        TableState(
            rows = rows.mapIndexed { r, cells ->
                RowModel(
                    id = "r$r",
                    cells = cells.map { (id, highlighted) ->
                        CellModel(id = id, text = "t", isHighlighted = highlighted)
                    },
                )
            },
        )

    @Test
    fun `toggles non-highlighted cell to highlighted`() {
        val state = tableState(listOf("r0_c0" to false))
        val result = useCase(state, "r0", "r0_c0")
        assertTrue(result.rows[0].cells[0].isHighlighted)
    }

    @Test
    fun `toggles highlighted cell to non-highlighted`() {
        val state = tableState(listOf("r0_c0" to true))
        val result = useCase(state, "r0", "r0_c0")
        assertFalse(result.rows[0].cells[0].isHighlighted)
    }

    @Test
    fun `other cells in same row are not affected`() {
        val state = tableState(listOf("r0_c0" to false, "r0_c1" to false))
        val result = useCase(state, "r0", "r0_c0")
        assertTrue(result.rows[0].cells[0].isHighlighted)
        assertFalse(result.rows[0].cells[1].isHighlighted)
    }

    @Test
    fun `cells in other rows are not affected`() {
        val state = tableState(
            listOf("r0_c0" to false),
            listOf("r1_c0" to false),
        )
        val result = useCase(state, "r0", "r0_c0")
        assertTrue(result.rows[0].cells[0].isHighlighted)
        assertFalse(result.rows[1].cells[0].isHighlighted)
    }

    @Test
    fun `double toggle restores original state`() {
        val state = tableState(listOf("r0_c0" to false))
        val toggled = useCase(state, "r0", "r0_c0")
        val restored = useCase(toggled, "r0", "r0_c0")
        assertFalse(restored.rows[0].cells[0].isHighlighted)
    }

    @Test
    fun `unknown rowId returns state unchanged`() {
        val state = tableState(listOf("r0_c0" to false))
        val result = useCase(state, "unknown", "r0_c0")
        assertFalse(result.rows[0].cells[0].isHighlighted)
    }

    @Test
    fun `unknown cellId returns state unchanged`() {
        val state = tableState(listOf("r0_c0" to false))
        val result = useCase(state, "r0", "unknown")
        assertFalse(result.rows[0].cells[0].isHighlighted)
    }
}
