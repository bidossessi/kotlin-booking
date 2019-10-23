package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.api.FindFacilities
import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.gateways.FacilityGateway
import com.colourcog.booking.domain.gateways.FacilitiesQuery

class FindFacilitiesService(private val facilityGateway: FacilityGateway): FindFacilities {
    override fun findByTags(tags: List<String>): Sequence<Facility> {
        return facilityGateway.findFacilities(FacilitiesQuery(tags))
    }
}

