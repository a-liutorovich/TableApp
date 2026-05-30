package com.example.idttesttask.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object InputScreenRoute

@Serializable
data class TableScreenRoute(val rows: Int, val cols: Int)
