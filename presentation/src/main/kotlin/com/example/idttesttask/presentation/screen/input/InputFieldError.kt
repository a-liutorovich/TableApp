package com.example.idttesttask.presentation.screen.input

sealed class InputFieldError {
    data object RowsOutOfRange : InputFieldError()
    data object ColsOutOfRange : InputFieldError()
}
