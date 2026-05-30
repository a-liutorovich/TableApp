package com.example.idttesttask.domain.model

data class CellModel(
    val id: String,
    val text: String,
    val isHighlighted: Boolean = false,
)
