package com.martorell.albert.basketcoach.data.util

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Represents an HTTP protocol error response with a status code.
 */
class HttpException(
    val code: Int,
    override val message: String = "HTTP error $code"
) : IOException(message)

/**
 * Maps technical framework exceptions into typed [DomainError] instances.
 */
fun Throwable.toDomainError(): DomainError = when (this) {
    is SocketTimeoutException -> DomainError.Network.Timeout
    is HttpException -> when (code) {
        401 -> DomainError.Network.Unauthorized
        in 500..599 -> DomainError.Network.ServerError(code)
        else -> DomainError.Network.ServerError(code, message)
    }
    is FileNotFoundException -> DomainError.Local.NotFound
    is ConnectException, is UnknownHostException -> DomainError.Network.NoInternet
    is IOException -> DomainError.Network.NoInternet
    is SerializationException -> DomainError.Generic(message = localizedMessage, cause = this)
    else -> DomainError.Generic(message = localizedMessage, cause = this)
}

/**
 * Safely executes a suspending block, catching technical exceptions and translating them
 * to [DomainError] via [toDomainError].
 *
 * Preserves structured concurrency by immediately rethrowing [CancellationException].
 */
suspend inline fun <T> safeCall(
    crossinline action: suspend () -> T
): Result<T, DomainError> = try {
    Result.Success(action())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.Error(e.toDomainError())
}
