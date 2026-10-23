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
    fun `toDomainError maps network timeouts to DomainError Network Timeout`() {
        val error = SocketTimeoutException("timeout").toDomainError()
        assertEquals(DomainError.Network.Timeout, error)
    }

    @Test
    fun `toDomainError maps 401 to DomainError Network Unauthorized`() {
        val error = HttpException(401, "unauthorized").toDomainError()
        assertEquals(DomainError.Network.Unauthorized, error)
    }

    @Test
    fun `toDomainError maps 500 to DomainError Network ServerError`() {
        val error = HttpException(500, "server error").toDomainError()
        assertTrue(error is DomainError.Network.ServerError)
        assertEquals(500, (error as DomainError.Network.ServerError).code)
    }

    @Test
    fun `toDomainError maps IOExceptions to DomainError Network NoInternet`() {
        assertEquals(DomainError.Network.NoInternet, IOException().toDomainError())
        assertEquals(DomainError.Network.NoInternet, ConnectException().toDomainError())
        assertEquals(DomainError.Network.NoInternet, UnknownHostException().toDomainError())
    }

    @Test
    fun `toDomainError maps FileNotFoundException and NoSuchElementException to DomainError Local NotFound`() {
        assertEquals(DomainError.Local.NotFound, FileNotFoundException("missing").toDomainError())
        assertEquals(DomainError.Local.NotFound, NoSuchElementException("no element").toDomainError())
    }

    @Test
    fun `toDomainError maps serialization and generic exceptions to DomainError Generic`() {
        assertTrue(SerializationException("bad json").toDomainError() is DomainError.Generic)
        assertTrue(RuntimeException("unknown").toDomainError() is DomainError.Generic)
    }

    @Test
    fun `safeCall rethrows CancellationException to preserve structured concurrency`() = runTest {
        try {
            safeCall<Unit> {
                throw CancellationException("cancelled")
            }
            fail("Expected CancellationException to be rethrown")
        } catch (_: CancellationException) {
            // Success
        }
    }

    @Test
    fun `safeCall returns Result Success when action succeeds`() = runTest {
        val result = safeCall { "hello" }
        assertTrue(result is Result.Success)
        assertEquals("hello", (result as Result.Success).data)
    }

    @Test
    fun `safeCall returns Result Error when action throws`() = runTest {
        val result = safeCall<String> {
            throw SocketTimeoutException("timed out")
        }
        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Timeout, (result as Result.Error).error)
    }
}
