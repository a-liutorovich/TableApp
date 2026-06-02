package com.example.idttesttask.presentation.screen.input

data class InputUiState(
    val rowsInput: String = "",
    val colsInput: String = "",
    val rowsError: InputFieldError? = null,
    val colsError: InputFieldError? = null,
    val isLoading: Boolean = false,
    val generationError: Boolean = false,
)
