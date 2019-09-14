package com.colourcog.booking.application

import com.colourcog.booking.api.CreateFacility
import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.gateway.FacilityGateway
import java.util.*

class CreateFacilityService(val facilityGateway: FacilityGateway) : CreateFacility {
    private fun validateRequest(request: CreateFacility.Request): Unit {
        // Do business validation here
        // raise exceptions when something is wrong
    }

    override fun create(request: CreateFacility.Request): CreateFacility.Response {
        validateRequest(request)
        val id = facilityGateway.create(request.toDomain())
        return CreateFacility.Response(id)
    }
}

fun CreateFacility.Request.toDomain(): Facility {
    return Facility(UUID.randomUUID().toString(), tags)
}