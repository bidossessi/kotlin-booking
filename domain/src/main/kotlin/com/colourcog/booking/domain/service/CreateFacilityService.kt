package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.api.CreateFacility
import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.gateways.FacilityGateway
import java.util.*

class CreateFacilityService(private val facilityGateway: FacilityGateway) : CreateFacility {

    override fun create(tags: List<String>): UUID {
        val facility = Facility(UUID.randomUUID(), tags)
        facilityGateway.create(facility)
        return facility.id
    }
}

