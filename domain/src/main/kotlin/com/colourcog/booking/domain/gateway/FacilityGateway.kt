package com.colourcog.booking.domain.gateway

import com.colourcog.booking.domain.entity.Facility

interface FacilityGateway {
    fun findFacilities(query: FacilitiesQuery): Sequence<Facility>
    fun getFacility(id: String): Facility
    fun create(facility: Facility): String
}

