package com.martorell.albert.basketcoach.domain.model

/**
 * Functional result type representing either a successful outcome ([Success]) with data [T],
 * or a typed failure ([Error]) with domain error [E].
 *
 * @param T Type of the successful payload.
 * @param E Type of the domain error, constrained to [DomainError].
 */
sealed interface Result<out T, out E : DomainError> {
    /**
     * Represents a successful operation returning [data].
     *
     * @property data The computed result value.
     */
    data class Success<out T>(val data: T) : Result<T, Nothing>

    /**
     * Represents a failed operation containing an [error].
     *
     * @property error The typed domain failure reason.
     */
    data class Error<out E : DomainError>(val error: E) : Result<Nothing, E>
}

/**
 * Transforms the successful value of this [Result] using the provided [transform] function,
 * propagating any existing [Result.Error] unchanged.
 */
inline fun <T, E : DomainError, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(error)
    }

/**
 * Executes the given [action] if this [Result] is a [Result.Success], returning the original result.
 */
inline fun <T, E : DomainError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Executes the given [action] if this [Result] is a [Result.Error], returning the original result.
 */
inline fun <T, E : DomainError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) action(error)
    return this
}
