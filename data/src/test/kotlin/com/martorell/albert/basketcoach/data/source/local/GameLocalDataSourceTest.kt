package com.martorell.albert.basketcoach.data.source.local

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.FileNotFoundException
import java.io.InputStream

class GameLocalDataSourceTest {

    private fun openDatasetStream(): InputStream {
        val stream = javaClass.classLoader?.getResourceAsStream("dataset.json")
        return checkNotNull(stream) { "Resource dataset.json not found on classpath" }
    }

    @Test
    fun `getGames returns all 14 fixtures from the bundled dataset`() = runTest {
        val dataSource = GameLocalDataSourceImpl()

        assertEquals(14, dataSource.getGames().size)
    }

    @Test
    fun `getGames caches the result and reads the stream only once`() = runTest {
        var invocations = 0
        val dataSource = GameLocalDataSourceImpl(
            jsonStreamProvider = {
                invocations++
                openDatasetStream()
            }
        )

        dataSource.getGames()
        dataSource.getGames()

        assertEquals(1, invocations)
    }

    @Test
    fun `getGames throws FileNotFoundException when the dataset is missing`() = runTest {
        val dataSource = GameLocalDataSourceImpl(jsonStreamProvider = { null })

        try {
            dataSource.getGames()
            fail("Expected FileNotFoundException")
        } catch (_: FileNotFoundException) {
            // Success
        }
    }

    @Test
    fun `getGames throws SerializationException when the dataset is corrupted`() = runTest {
        val dataSource = GameLocalDataSourceImpl(
            jsonStreamProvider = { "{ not a valid json at all }".byteInputStream() }
        )

        try {
            dataSource.getGames()
            fail("Expected SerializationException")
        } catch (_: SerializationException) {
            // Success
        }
    }
}
