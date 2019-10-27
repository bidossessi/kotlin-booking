package com.colourcog.booking.domain.api

import java.util.*

interface CreateFacility  {
    suspend fun action(tags: List<String> = emptyList<String>()): UUID
}