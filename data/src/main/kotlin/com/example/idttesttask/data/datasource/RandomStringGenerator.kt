package com.example.idttesttask.data.datasource

private val CHARS = ('A'..'Z') + ('a'..'z') + ('0'..'9')

class RandomStringGenerator {
    fun generate(length: Int = 8): String =
        (1..length).map { CHARS.random() }.joinToString("")
}
