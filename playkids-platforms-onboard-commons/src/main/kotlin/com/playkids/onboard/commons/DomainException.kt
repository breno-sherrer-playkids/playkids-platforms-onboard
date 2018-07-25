package com.playkids.onboard.commons

import io.ktor.http.HttpStatusCode

/**
 * Custom exception for validations.
 */
open class DomainException(
        val errors: Collection<ErrorDetail>,
        val httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest) : RuntimeException() {

    constructor(title: String, description: String) : this(listOf(ErrorDetail(title, description)))
    constructor(issues: Collection<ValidationIssue>) : this(issues.map { ErrorDetail(it.title, it.description) })

    /**
     * Details for the Error.
     *
     * Note: this class only exists to serialize the error and description to a JSON because ValidationIssue is
     * usually implemented on an Enum, and the Enum serialization is its Name.
     */
    class ErrorDetail(
            val error: String,
            val description: String)
}

