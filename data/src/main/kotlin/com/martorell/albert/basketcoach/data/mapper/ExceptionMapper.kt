package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.HttpErrorParser
import com.martorell.albert.basketcoach.domain.model.DomainError
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
    is HttpException -> {
        val cleanMessage = HttpErrorParser.parse(message)
        when {
            code == 401 || code == 403 -> DomainError.Network.Unauthorized
            code == 400 && (cleanMessage.contains("auth key", ignoreCase = true) || cleanMessage.contains("api key", ignoreCase = true)) -> {
                DomainError.Network.Unauthorized
            }
            code in 500..599 -> DomainError.Network.ServerError(code, cleanMessage.ifBlank { null })
            else -> DomainError.Network.ServerError(code, cleanMessage.ifBlank { null })
        }
    }

    is FileNotFoundException, is NoSuchElementException -> DomainError.Local.NotFound
    is ConnectException, is UnknownHostException -> DomainError.Network.NoInternet
    is IOException -> DomainError.Network.NoInternet
    is SerializationException -> DomainError.Generic(message = localizedMessage, cause = this)
    else -> DomainError.Generic(message = localizedMessage, cause = this)
}
