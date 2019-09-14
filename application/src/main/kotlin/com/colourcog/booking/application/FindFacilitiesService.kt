package com.colourcog.booking.application

import com.colourcog.booking.api.FindFacilities
import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.gateway.FacilityGateway
import com.colourcog.booking.domain.gateway.FacilitiesQuery

class FindFacilitiesService(private val facilityGateway: FacilityGateway): FindFacilities {
    override fun find(request: FindFacilities.Request): FindFacilities.Response {
        val results = facilityGateway.findFacilities(FacilitiesQuery(request.tags)).map { it.toResponse() }
        return FindFacilities.Response(results)
    }
}

fun Facility.toResponse(): FindFacilities.Response.Facility {
    return FindFacilities.Response.Facility(id)
}