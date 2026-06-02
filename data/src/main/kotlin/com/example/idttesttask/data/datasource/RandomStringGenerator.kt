package com.example.idttesttask.data.datasource

private val CHARS = ('A'..'Z') + ('a'..'z') + ('0'..'9')

/**
 * Generates random alphanumeric strings used as initial cell content.
 * Each cell in a table of up to 6 000 entries gets a unique-looking value.
 */
class RandomStringGenerator {
    fun generate(length: Int = 8): String =
        (1..length).map { CHARS.random() }.joinToString("")
}
