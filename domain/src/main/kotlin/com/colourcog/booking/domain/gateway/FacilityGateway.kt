package com.colourcog.booking.domain.gateway

import com.colourcog.booking.domain.entity.Facility

interface FacilityGateway {
    fun findFacilities(query: FacilitiesQuery): Sequence<Facility>
    fun create(facility: Facility): String
}

data class FacilitiesQuery(val tags: List<String>)