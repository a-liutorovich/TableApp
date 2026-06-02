package com.example.idttesttask.presentation.screen.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun TableScreen(
    rows: Int,
    cols: Int,
    onNavigateBack: () -> Unit,
    viewModel: TableViewModel = koinViewModel(),
) {
    val uiState by viewModel.tableUiState.collectAsStateWithLifecycle()
    TableScreenContent(
        uiState = uiState,
        title = stringResource(R.string.table_screen_title, rows, cols),
        onNavigateBack = onNavigateBack,
        onCellClick = viewModel::onCellClick,
        onCellTextSaved = viewModel::onCellTextSaved,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TableScreenContent(
    uiState: TableScreenUiState,
    title: String,
    onNavigateBack: () -> Unit,
    onCellClick: (rowId: String, cellId: String) -> Unit,
    onCellTextSaved: (rowId: String, cellId: String, text: String) -> Unit,
) {
    // A single callback that closes whichever cell is currently being edited.
    // Stored at the screen level so that tapping any other cell can first commit the active editor
    // before opening a new one — only one cell can be in edit mode at a time.
    var closeEditing by remember { mutableStateOf<(() -> Unit)?>(null) }
    val setCloseEditing: ((() -> Unit)?) -> Unit = { closeEditing = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                items(uiState.rows, key = { row -> row.id }, contentType = { "row" }) { row ->
                    TableRow(
                        row = row,
                        closeEditing = closeEditing,
                        setCloseEditing = setCloseEditing,
                        onCellClick = { cellId -> onCellClick(row.id, cellId) },
                        onCellTextSaved = { cellId, text -> onCellTextSaved(row.id, cellId, text) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TableRow(
    row: RowUiState,
    closeEditing: (() -> Unit)?,
    setCloseEditing: ((() -> Unit)?) -> Unit,
    onCellClick: (cellId: String) -> Unit,
    onCellTextSaved: (cellId: String, text: String) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        row.cells.forEach { cell ->
            TableCell(
                cell = cell,
                modifier = Modifier.weight(1f),
                closeEditing = closeEditing,
                setCloseEditing = setCloseEditing,
                onSingleClick = { onCellClick(cell.id) },
                onTextSaved = { text -> onCellTextSaved(cell.id, text) },
            )
        }
    }
}

@Composable
private fun TableCell(
    cell: CellUiState,
    modifier: Modifier = Modifier,
    closeEditing: (() -> Unit)?,
    setCloseEditing: ((() -> Unit)?) -> Unit,
    onSingleClick: () -> Unit,
    onTextSaved: (String) -> Unit,
) {
    val cellHeight = dimensionResource(R.dimen.cell_height)
    val highlightColor = colorResource(R.color.cell_highlight)
    val saveTextColor = colorResource(R.color.save_text)
    val saveLabel = stringResource(R.string.table_cell_save)

    var isEditing by remember(cell.id) { mutableStateOf(false) }
    var editText by remember(cell.id) { mutableStateOf(cell.text) }
    var hadFocus by remember(cell.id) { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // rememberUpdatedState captures a stable reference that always reflects the latest
    // closeEditing value. The pointerInput block below is created once (keyed on cell.id +
    // isEditing) and would otherwise close over a stale null if closeEditing changed later.
    val closeEditingState = rememberUpdatedState(closeEditing)

    fun saveAndClose() {
        hadFocus = false
        onTextSaved(editText)
        isEditing = false
        setCloseEditing(null)
    }

    val backgroundColor =
        if (cell.isHighlighted) highlightColor else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .height(cellHeight)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(backgroundColor)
            // pointerInput is keyed on (cell.id, isEditing) so gesture detection restarts
            // whenever edit mode changes. While isEditing=true the handler is disabled,
            // letting BasicTextField consume touch events normally.
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
                                // hadFocus guards against saveAndClose() firing immediately:
                                // focus arrives right after isEditing=true, before the user types.
                                hadFocus = true
                            } else if (hadFocus) {
                                // Focus lost after the user actually had it — commit and exit edit mode.
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
        }.toImmutableList()
    )
}.toImmutableList()

@Preview(name = "Table — 5 rows × 4 cols", showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
private fun TableScreenDataPreview() {
    MaterialTheme {
        TableScreenContent(
            uiState = TableScreenUiState(rows = mockRows(5, 4), isLoading = false),
            title = "Table 5×4",
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
            title = "Table 20×6",
            onNavigateBack = {},
            onCellClick = { _, _ -> },
            onCellTextSaved = { _, _, _ -> },
        )
    }
}
