package com.example.idttesttask.presentation.screen.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.idttesttask.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun TableScreen(
    onNavigateBack: () -> Unit,
    viewModel: TableViewModel = koinViewModel(),
) {
    val uiState by viewModel.tableUiState.collectAsStateWithLifecycle()
    TableScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCellClick = viewModel::onCellClick,
        onCellTextSaved = viewModel::onCellTextSaved,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TableScreenContent(
    uiState: TableScreenUiState,
    onNavigateBack: () -> Unit,
    onCellClick: (rowId: String, cellId: String) -> Unit,
    onCellTextSaved: (rowId: String, cellId: String, text: String) -> Unit,
) {
    val horizontalScrollState = rememberScrollState()

    var closeEditing by remember { mutableStateOf<(() -> Unit)?>(null) }
    val setCloseEditing: ((() -> Unit)?) -> Unit = remember { { closeEditing = it } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.table_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(uiState.rows, key = { row -> row.id }) { row ->
                    val onCellClick = remember(row.id) {
                        { cellId: String -> onCellClick(row.id, cellId) }
                    }
                    val onCellTextSaved = remember(row.id) {
                        { cellId: String, text: String -> onCellTextSaved(row.id, cellId, text) }
                    }
                    TableRow(
                        row = row,
                        horizontalScrollState = horizontalScrollState,
                        closeEditing = closeEditing,
                        setCloseEditing = setCloseEditing,
                        onCellClick = onCellClick,
                        onCellTextSaved = onCellTextSaved,
                    )
                }
            }
        }
    }
}

@Composable
private fun TableRow(
    row: RowUiState,
    horizontalScrollState: ScrollState,
    closeEditing: (() -> Unit)?,
    setCloseEditing: ((() -> Unit)?) -> Unit,
    onCellClick: (cellId: String) -> Unit,
    onCellTextSaved: (cellId: String, text: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.Center,
    ) {
        row.cells.forEach { cell ->
            val cellOnClick = remember(cell.id) { { onCellClick(cell.id) } }
            val cellOnSave = remember(cell.id) { { text: String -> onCellTextSaved(cell.id, text) } }
            TableCell(
                cell = cell,
                closeEditing = closeEditing,
                setCloseEditing = setCloseEditing,
                onSingleClick = cellOnClick,
                onTextSaved = cellOnSave,
            )
        }
    }
}

@Composable
private fun TableCell(
    cell: CellUiState,
    closeEditing: (() -> Unit)?,
    setCloseEditing: ((() -> Unit)?) -> Unit,
    onSingleClick: () -> Unit,
    onTextSaved: (String) -> Unit,
) {
    val cellWidth = dimensionResource(R.dimen.cell_width)
    val cellHeight = dimensionResource(R.dimen.cell_height)
    val highlightColor = colorResource(R.color.cell_highlight)
    val saveTextColor = colorResource(R.color.save_text)
    val saveLabel = stringResource(R.string.table_cell_save)

    var isEditing by remember(cell.id) { mutableStateOf(false) }
    var editText by remember(cell.id) { mutableStateOf(cell.text) }
    var hadFocus by remember(cell.id) { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val closeEditingState = rememberUpdatedState(closeEditing)

    fun saveAndClose() {
        hadFocus = false
        onTextSaved(editText)
        isEditing = false
        setCloseEditing(null)
    }

    val backgroundColor = if (cell.isHighlighted) highlightColor else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(backgroundColor)
            .pointerInput(cell.id, isEditing) {
                if (!isEditing) {
                    detectTapGestures(
                        onTap = {
                            closeEditingState.value?.invoke() ?: onSingleClick()
                        },
                        onDoubleTap = {
                            closeEditingState.value?.invoke() ?: run {
                                editText = cell.text
                                isEditing = true
                                setCloseEditing(::saveAndClose)
                            }
                        },
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isEditing) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BasicTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                hadFocus = true
                            } else if (hadFocus) {
                                saveAndClose()
                            }
                        },
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    singleLine = true,
                )
                Text(
                    text = saveLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = saveTextColor,
                        textDecoration = TextDecoration.Underline,
                    ),
                    modifier = Modifier.clickable(onClick = ::saveAndClose),
                )
            }
        } else {
            Text(
                text = cell.text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

private fun mockRows(rowCount: Int, colCount: Int) = List(rowCount) { r ->
    RowUiState(
        id = r.toString(),
        cells = List(colCount) { c ->
            CellUiState(
                id = "${r}_${c}",
                text = "R${r + 1}C${c + 1}",
                isHighlighted = r == 1 && c == 2,
            )
        }
    )
}

@Preview(name = "Table — 5 rows × 4 cols", showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
private fun TableScreenDataPreview() {
    MaterialTheme {
        TableScreenContent(
            uiState = TableScreenUiState(rows = mockRows(5, 4), isLoading = false),
            onNavigateBack = {},
            onCellClick = { _, _ -> },
            onCellTextSaved = { _, _, _ -> },
        )
    }
}

@Preview(name = "Table — 20 rows × 6 cols", showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
private fun TableScreenFullPreview() {
    MaterialTheme {
        TableScreenContent(
            uiState = TableScreenUiState(rows = mockRows(20, 6), isLoading = false),
            onNavigateBack = {},
            onCellClick = { _, _ -> },
            onCellTextSaved = { _, _, _ -> },
        )
    }
}
