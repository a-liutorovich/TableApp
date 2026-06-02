package com.example.idttesttask.data.repository

import com.example.idttesttask.data.datasource.RandomStringGenerator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TableRepositoryImplTest {

    private lateinit var repository: TableRepositoryImpl

    @Before
    fun setUp() {
        repository = TableRepositoryImpl(RandomStringGenerator())
    }

    @Test
    fun `generate produces correct number of rows`() = runTest {
        repository.generate(rows = 5, cols = 3)
        assertEquals(5, repository.get().rows.size)
    }

    @Test
    fun `generate produces correct number of cells per row`() = runTest {
        repository.generate(rows = 4, cols = 6)
        repository.get().rows.forEach { row ->
            assertEquals(6, row.cells.size)
        }
    }

    @Test
    fun `generate at max scale produces correct dimensions`() = runTest {
        repository.generate(rows = 1000, cols = 6)
        val state = repository.get()
        assertEquals(1000, state.rows.size)
        state.rows.forEach { row -> assertEquals(6, row.cells.size) }
    }

    @Test
    fun `all row ids are unique`() = runTest {
        repository.generate(rows = 10, cols = 3)
        val ids = repository.get().rows.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `all cell ids within a row are unique`() = runTest {
        repository.generate(rows = 3, cols = 6)
        repository.get().rows.forEach { row ->
            val ids = row.cells.map { it.id }
            assertEquals(ids.size, ids.toSet().size)
        }
    }

    @Test
    fun `all cell ids across the table are unique`() = runTest {
        repository.generate(rows = 10, cols = 6)
        val ids = repository.get().rows.flatMap { row -> row.cells.map { it.id } }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `generate updates observed state`() = runTest {
        repository.generate(rows = 2, cols = 2)
        val observed = repository.observe().first()
        assertEquals(2, observed.rows.size)
    }

    @Test
    fun `second generate replaces first state`() = runTest {
        repository.generate(rows = 3, cols = 3)
        repository.generate(rows = 1, cols = 1)
        assertEquals(1, repository.get().rows.size)
        assertEquals(1, repository.get().rows[0].cells.size)
    }

    @Test
    fun `generate with zero rows produces empty state`() = runTest {
        repository.generate(rows = 0, cols = 6)
        assertTrue(repository.get().rows.isEmpty())
    }
}
