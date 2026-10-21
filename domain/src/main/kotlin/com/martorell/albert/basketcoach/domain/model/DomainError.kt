package com.martorell.albert.basketcoach.domain.model

sealed interface DomainError {
    sealed interface Network : DomainError {
        data object NoInternet : Network
        data object Timeout : Network
        data class ServerError(val code: Int, val message: String? = null) : Network
        data object Unauthorized : Network
        data object Unknown : Network
    }

    sealed interface Local : DomainError {
        data object DiskFull : Local
        data object DatabaseError : Local
        data object NotFound : Local
    }

    data class Generic(val message: String? = null, val cause: Throwable? = null) : DomainError
}
