package com.martorell.albert.basketcoach.data.util

import com.martorell.albert.basketcoach.data.mapper.HttpException
import com.martorell.albert.basketcoach.data.mapper.toDomainError
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
        assertEquals(DomainError.Network.Unauthorized, HttpException(403, "forbidden").toDomainError())

        // 400 with invalid auth key from Google Cloud maps to Unauthorized
        val googleAuthError = HttpException(400, "[{\"error\": {\"code\": 400, \"message\": \"Invalid Auth key.\", \"status\": \"INVALID_ARGUMENT\"}}]")
        assertEquals(DomainError.Network.Unauthorized, googleAuthError.toDomainError())

        // 404 or other server errors parse and preserve clean message
        val google404 = HttpException(404, "[{\"error\": {\"code\": 404, \"message\": \"Model deprecated\", \"status\": \"NOT_FOUND\"}}]")
        val parsed404 = google404.toDomainError()
        assertTrue(parsed404 is DomainError.Network.ServerError)
        assertEquals(404, (parsed404 as DomainError.Network.ServerError).code)
        assertEquals("Model deprecated", parsed404.message)

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
        } catch (e: CancellationException) {
            assertEquals("cancelled", e.message)
        }
    }
}
