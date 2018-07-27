package com.playkids.onboard.commons

/**
 * Defines a Exception for domain specific Validations.
 *
 * e.g.: prize less than zero, throws a validation.
 */
class ValidationException(val issues: Collection<ValidationIssue>) :
        DomainException(issues.map { DomainException.ErrorDetail(it.title, it.description) })