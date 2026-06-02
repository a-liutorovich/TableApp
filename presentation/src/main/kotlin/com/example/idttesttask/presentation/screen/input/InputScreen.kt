package com.example.idttesttask.presentation.screen.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.idttesttask.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
private fun InputFieldError.asString(): String = stringResource(
    when (this) {
        InputFieldError.RowsOutOfRange -> R.string.input_rows_error
        InputFieldError.ColsOutOfRange -> R.string.input_cols_error
    }
)

@Composable
fun InputScreen(
    onNavigateToTable: (Int, Int) -> Unit,
    viewModel: InputViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { (rows, cols) ->
            onNavigateToTable(rows, cols)
        }
    }
    InputScreenContent(
        state = state,
        onRowsInputChange = viewModel::onRowsInputChange,
        onColsInputChange = viewModel::onColsInputChange,
        onGenerateClick = viewModel::onGenerateClick,
    )
}

@Composable
internal fun InputScreenContent(
    state: InputUiState,
    onRowsInputChange: (String) -> Unit,
    onColsInputChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
) {
    val fieldWidth = dimensionResource(R.dimen.input_field_width)
    val colsFocusRequester = remember { FocusRequester() }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.input_screen_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.rowsInput,
                        onValueChange = onRowsInputChange,
                        label = { Text(stringResource(R.string.input_rows_label)) },
                        isError = state.rowsError != null,
                        supportingText = state.rowsError?.let { error -> { Text(error.asString()) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { colsFocusRequester.requestFocus() },
                        ),
                        singleLine = true,
                        modifier = Modifier.width(fieldWidth),
                    )
                    OutlinedTextField(
                        value = state.colsInput,
                        onValueChange = onColsInputChange,
                        label = { Text(stringResource(R.string.input_cols_label)) },
                        isError = state.colsError != null,
                        supportingText = state.colsError?.let { error -> { Text(error.asString()) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onGenerateClick() },
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .width(fieldWidth)
                            .focusRequester(colsFocusRequester),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onGenerateClick,
                        modifier = Modifier.width(fieldWidth),
                    ) {
                        Text(stringResource(R.string.input_generate_button))
                    }
                    if (state.generationError) {
                        Text(
                            text = stringResource(R.string.input_generation_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Input — empty", showBackground = true, widthDp = 900, heightDp = 600)
@Composable
private fun InputScreenEmptyPreview() {
    MaterialTheme {
        InputScreenContent(
            state = InputUiState(),
            onRowsInputChange = {},
            onColsInputChange = {},
            onGenerateClick = {},
        )
    }
}

@Preview(name = "Input — errors", showBackground = true, widthDp = 900, heightDp = 600)
@Composable
private fun InputScreenErrorPreview() {
    MaterialTheme {
        InputScreenContent(
            state = InputUiState(
                rowsInput = "9999",
                colsInput = "99",
                rowsError = InputFieldError.RowsOutOfRange,
                colsError = InputFieldError.ColsOutOfRange,
            ),
            onRowsInputChange = {},
            onColsInputChange = {},
            onGenerateClick = {},
        )
    }
}
