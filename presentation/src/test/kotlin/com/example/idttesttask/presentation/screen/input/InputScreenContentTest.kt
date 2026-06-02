package com.example.idttesttask.presentation.screen.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.input.ImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class InputScreenContentTest {

    @get:Rule
    val rule = createComposeRule()

    // Launches InputScreenContent with a state holder that mirrors ViewModel callbacks.
    private fun launch(
        initial: InputUiState = InputUiState(),
        onGenerateClick: (() -> Unit)? = null,
    ): () -> InputUiState {
        var state by mutableStateOf(initial)
        rule.setContent {
            InputScreenContent(
                state = state,
                onRowsInputChange = { state = state.copy(rowsInput = it, rowsError = null) },
                onColsInputChange = { state = state.copy(colsInput = it, colsError = null) },
                onGenerateClick = onGenerateClick ?: {
                    val rows = state.rowsInput.toIntOrNull() ?: 0
                    val cols = state.colsInput.toIntOrNull() ?: 0
                    state = state.copy(
                        rowsError = if (rows !in 1..1000) InputFieldError.RowsOutOfRange else null,
                        colsError = if (cols !in 1..6) InputFieldError.ColsOutOfRange else null,
                    )
                },
            )
        }
        return { state }
    }

    @Test
    fun initialState_noErrorsShown() {
        launch()
        rule.onNodeWithText("Enter a number from 1 to 1000").assertDoesNotExist()
        rule.onNodeWithText("Enter a number from 1 to 6").assertDoesNotExist()
    }

    @Test
    fun generateWithEmptyInput_showsBothErrors() {
        launch()
        rule.onNodeWithText("Generate Table").performClick()
        rule.onNodeWithText("Enter a number from 1 to 1000").assertIsDisplayed()
        rule.onNodeWithText("Enter a number from 1 to 6").assertIsDisplayed()
    }

    @Test
    fun generateWithRowsOutOfRange_showsRowsErrorOnly() {
        launch(InputUiState(rowsInput = "9999", colsInput = "3"))
        rule.onNodeWithText("Generate Table").performClick()
        rule.onNodeWithText("Enter a number from 1 to 1000").assertIsDisplayed()
        rule.onNodeWithText("Enter a number from 1 to 6").assertDoesNotExist()
    }

    @Test
    fun generateWithColsOutOfRange_showsColsErrorOnly() {
        launch(InputUiState(rowsInput = "10", colsInput = "99"))
        rule.onNodeWithText("Generate Table").performClick()
        rule.onNodeWithText("Enter a number from 1 to 1000").assertDoesNotExist()
        rule.onNodeWithText("Enter a number from 1 to 6").assertIsDisplayed()
    }

    @Test
    fun typingInRowsField_clearsRowsError() {
        launch(InputUiState(rowsError = InputFieldError.RowsOutOfRange))
        rule.onNodeWithText("Enter a number from 1 to 1000").assertIsDisplayed()
        rule.onNode(hasSetTextAction() and hasImeAction(ImeAction.Next)).performTextInput("5")
        rule.onNodeWithText("Enter a number from 1 to 1000").assertDoesNotExist()
    }

    @Test
    fun typingInColsField_clearsColsError() {
        launch(InputUiState(colsError = InputFieldError.ColsOutOfRange))
        rule.onNodeWithText("Enter a number from 1 to 6").assertIsDisplayed()
        rule.onNode(hasSetTextAction() and hasImeAction(ImeAction.Done)).performTextInput("3")
        rule.onNodeWithText("Enter a number from 1 to 6").assertDoesNotExist()
    }

    @Test
    fun generationErrorState_showsErrorBanner() {
        launch(InputUiState(generationError = true))
        rule.onNodeWithText("Failed to generate table. Please try again.").assertIsDisplayed()
    }

    @Test
    fun generateButtonClick_triggersCallback() {
        var clicked = false
        launch(onGenerateClick = { clicked = true })
        rule.onNodeWithText("Generate Table").performClick()
        assertTrue(clicked)
    }

    @Test
    fun loadingState_showsSpinnerAndHidesFields() {
        launch(InputUiState(isLoading = true))
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        rule.onNodeWithText("Generate Table").assertDoesNotExist()
    }
}
