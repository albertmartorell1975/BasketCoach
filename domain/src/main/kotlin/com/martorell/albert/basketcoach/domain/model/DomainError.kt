package com.martorell.albert.basketcoach.domain.model

/**
 * Root domain error taxonomy representing typed failures across all operations in BasketCoach.
 *
 * Adheres to Clean Architecture error modeling by shielding the domain and presentation layers
 * from raw platform, HTTP, or SQLite exceptions.
 */
sealed interface DomainError {
    /**
     * Errors originating from remote network communications, API endpoints, or connectivity loss.
     */
    sealed interface Network : DomainError {
        /** Internet connection is absent or unreachable. */
        data object NoInternet : Network

        /** Network request timed out before receiving a response. */
        data object Timeout : Network

        /**
         * Remote server returned an error status code.
         *
         * @property code HTTP status code returned by the server.
         * @property message Optional descriptive error message from the server.
         */
        data class ServerError(val code: Int, val message: String? = null) : Network

        /** Authentication or authorization credentials rejected. */
        data object Unauthorized : Network

        /** Unclassified network communication failure. */
        data object Unknown : Network
    }

    /**
     * Errors originating from local storage, file IO, or database queries.
     */
    sealed interface Local : DomainError {
        /** Local disk or storage device is full. */
        data object DiskFull : Local

        /** Local persistence or SQLite transaction failure. */
        data object DatabaseError : Local

        /** Requested record or resource was not found locally. */
        data object NotFound : Local
    }

    /**
     * Uncategorized application or domain failure.
     *
     * @property message Optional diagnostic message.
     * @property cause Optional underlying throwable cause.
     */
    data class Generic(val message: String? = null, val cause: Throwable? = null) : DomainError
}
