package com.playkids.onboard.commons

import kotlin.reflect.KClass

/**
 * Defines an Exception for when a required entity wasn't found.
 */
class EntityNotFoundException(clazz: KClass<*>, ID: Int)
    : DomainException("Fatal error", "Couldn't find entity ${clazz.simpleName} with ID $ID")