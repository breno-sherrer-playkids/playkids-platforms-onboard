package com.playkids.business

import com.playkids.onboard.commons.ValidationException
import com.playkids.onboard.commons.ValidationIssue
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Asserts that the given block throws an ValidationException whose Issues matches exactly the supplied ones, otherwise,
 * the test fails.
 */
fun assertExactValidationIssues(vararg validIssues: ValidationIssue, block: () -> Unit) {

    try {
        block()
    } catch (vex: ValidationException) {
        assertTrue("" +
                "Expected: ${validIssues.joinToString { it.description }}\n" +
                "Found: ${vex.issues.joinToString { it.description }}") {
            validIssues.toList().containsAll(vex.issues)
        }

        return
    }

    fail()
}
