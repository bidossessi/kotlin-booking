package com.colourcog.booking.application

import com.colourcog.booking.api.CreateFacility
import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.gateway.FacilityGateway
import java.util.*

class CreateFacilityService(private val facilityGateway: FacilityGateway) : CreateFacility {

    override fun create(request: CreateFacility.Request): CreateFacility.Response {
        // Do business validation here
        // raise exceptions when something is wrong
        val id = facilityGateway.create(request.toDomain())
        return CreateFacility.Response(id)
    }
}

fun CreateFacility.Request.toDomain(): Facility {
    return Facility(UUID.randomUUID().toString(), tags)
}