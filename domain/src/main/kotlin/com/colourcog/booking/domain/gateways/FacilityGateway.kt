package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Facility
import java.util.*

interface FacilityGateway {
    fun findFacilities(query: FacilitiesQuery): Sequence<Facility>
    fun getFacility(id: UUID): Facility
    fun create(facility: Facility): Unit
}

