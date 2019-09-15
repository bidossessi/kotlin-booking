package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Facility

interface FacilityGateway {
    fun findFacilities(query: FacilitiesQuery): Sequence<Facility>
    fun getFacility(id: String): Facility
    fun create(facility: Facility): String
}

