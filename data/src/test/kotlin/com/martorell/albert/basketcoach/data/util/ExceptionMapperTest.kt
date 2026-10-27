package com.martorell.albert.basketcoach.data.util

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ExceptionMapperTest {

    @Test
    fun `toDomainError maps network, local, and serialization exceptions to typed DomainErrors`() {
        assertEquals(DomainError.Network.Timeout, SocketTimeoutException("timeout").toDomainError())
        assertEquals(DomainError.Network.Unauthorized, HttpException(401, "unauthorized").toDomainError())

        val serverError = HttpException(500, "server error").toDomainError()
        assertTrue(serverError is DomainError.Network.ServerError)
        assertEquals(500, (serverError as DomainError.Network.ServerError).code)

        assertEquals(DomainError.Network.NoInternet, IOException().toDomainError())
        assertEquals(DomainError.Network.NoInternet, ConnectException().toDomainError())
        assertEquals(DomainError.Network.NoInternet, UnknownHostException().toDomainError())

        assertEquals(DomainError.Local.NotFound, FileNotFoundException("missing").toDomainError())
        assertEquals(DomainError.Local.NotFound, NoSuchElementException("no element").toDomainError())

        assertTrue(SerializationException("bad json").toDomainError() is DomainError.Generic)
        assertTrue(RuntimeException("unknown").toDomainError() is DomainError.Generic)
    }

    @Test
    fun `safeCall maps thrown exceptions to Result Error and rethrows CancellationException`() = runTest {
        val errorResult = safeCall<String> {
            throw SocketTimeoutException("timed out")
        }
        assertTrue(errorResult is Result.Error)
        assertEquals(DomainError.Network.Timeout, (errorResult as Result.Error).error)

        try {
            safeCall<Unit> {
                throw CancellationException("cancelled")
            }
            fail("Expected CancellationException to be rethrown")
        } catch (_: CancellationException) {
            // Structured concurrency preserved
        }
    }
}
