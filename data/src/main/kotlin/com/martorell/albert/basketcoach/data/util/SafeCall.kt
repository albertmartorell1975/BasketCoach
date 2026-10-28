package com.martorell.albert.basketcoach.data.util

import com.martorell.albert.basketcoach.data.mapper.toDomainError
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.CancellationException

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