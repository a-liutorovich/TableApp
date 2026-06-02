package com.example.idttesttask.presentation.screen.table

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TableScreenEditingTest {

    @get:Rule
    val rule = createComposeRule()

    private fun launchSingleCell(
        text: String = "original",
        onCellTextSaved: (rowId: String, cellId: String, text: String) -> Unit = { _, _, _ -> },
    ) {
        val uiState = TableScreenUiState(
            isLoading = false,
            rows = persistentListOf(
                RowUiState(
                    id = "r0",
                    cells = persistentListOf(CellUiState(id = "r0_c0", text = text, isHighlighted = false)),
                )
            ),
        )
        rule.setContent {
            TableScreenContent(
                uiState = uiState,
                title = "1×1",
                onNavigateBack = {},
                onCellClick = { _, _ -> },
                onCellTextSaved = onCellTextSaved,
            )
        }
    }

    @Test
    fun doubleTab_entersEditMode_showsTextFieldAndSaveButton() {
        launchSingleCell()

        rule.onNodeWithText("original").assertIsDisplayed()
        rule.onNodeWithText("original").performTouchInput { doubleClick() }

        rule.onNode(hasSetTextAction()).assertIsDisplayed()
        rule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun editAndSave_callsOnCellTextSavedWithNewText() {
        var savedRowId: String? = null
        var savedCellId: String? = null
        var savedText: String? = null

        launchSingleCell(
            onCellTextSaved = { rowId, cellId, text ->
                savedRowId = rowId
                savedCellId = cellId
                savedText = text
            },
        )

        rule.onNodeWithText("original").performTouchInput { doubleClick() }
        rule.onNode(hasSetTextAction()).performTextClearance()
        rule.onNode(hasSetTextAction()).performTextInput("edited")
        rule.onNodeWithText("Save").performClick()

        assertEquals("r0", savedRowId)
        assertEquals("r0_c0", savedCellId)
        assertEquals("edited", savedText)
    }

    @Test
    fun save_exitsEditMode_hidingTextFieldAndSaveButton() {
        launchSingleCell()

        rule.onNodeWithText("original").performTouchInput { doubleClick() }
        rule.onNodeWithText("Save").assertIsDisplayed()

        rule.onNodeWithText("Save").performClick()

        rule.onNodeWithText("Save").assertDoesNotExist()
        rule.onNode(hasSetTextAction()).assertDoesNotExist()
    }
}
