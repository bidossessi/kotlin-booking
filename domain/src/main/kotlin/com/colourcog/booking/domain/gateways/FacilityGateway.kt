package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Facility
import java.util.*

interface FacilityGateway {
    suspend fun create(facility: Facility): Boolean
    suspend fun create(facilities: List<Facility>): Boolean
    suspend fun getFacility(id: UUID): Facility
    suspend fun find(
        including: List<UUID>? = null,
        excluding: List<UUID>? = null,
        tags: List<String>? = null
    ): Sequence<Facility>

}

