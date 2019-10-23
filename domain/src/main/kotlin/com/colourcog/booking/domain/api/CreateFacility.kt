package com.colourcog.booking.domain.api

import java.util.*

interface CreateFacility  {
    fun create(tags: List<String> = emptyList<String>()): UUID
}