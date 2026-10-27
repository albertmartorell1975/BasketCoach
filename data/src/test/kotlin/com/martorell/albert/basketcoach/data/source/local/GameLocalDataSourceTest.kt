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
    fun `getGames parses all 14 fixtures and caches the stream after first read`() = runTest {
        var invocations = 0
        val dataSource = GameLocalDataSourceImpl(
            jsonStreamProvider = {
                invocations++
                openDatasetStream()
            }
        )

        val firstRead = dataSource.getGames()
        val secondRead = dataSource.getGames()

        assertEquals(14, firstRead.size)
        assertEquals(14, secondRead.size)
        assertEquals(1, invocations)
    }

    @Test
    fun `getGames throws FileNotFoundException on missing stream and SerializationException on corrupt JSON`() = runTest {
        val missingSource = GameLocalDataSourceImpl(jsonStreamProvider = { null })
        try {
            missingSource.getGames()
            fail("Expected FileNotFoundException")
        } catch (_: FileNotFoundException) {
            // Expected
        }

        val corruptSource = GameLocalDataSourceImpl(
            jsonStreamProvider = { "{ not a valid json at all }".byteInputStream() }
        )
        try {
            corruptSource.getGames()
            fail("Expected SerializationException")
        } catch (_: SerializationException) {
            // Expected
        }
    }
}
