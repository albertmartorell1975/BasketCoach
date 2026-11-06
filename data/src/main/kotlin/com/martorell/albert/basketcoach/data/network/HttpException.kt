package com.martorell.albert.basketcoach.data.network

import java.io.IOException

/**
 * Represents an HTTP protocol error response with a status code.
 */
class HttpException(
    val code: Int,
    override val message: String = "HTTP error $code"
) : IOException(message)