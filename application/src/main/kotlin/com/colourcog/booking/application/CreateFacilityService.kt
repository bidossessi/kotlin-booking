package com.colourcog.booking.application

import com.colourcog.booking.api.CreateFacility
import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.gateway.FacilityGateway
import java.util.*

class CreateFacilityService(val facilityGateway: FacilityGateway) : CreateFacility {
    fun validateRequest(request: CreateFacility.Request) {
        // Do business validation here
    }

    override fun create(request: CreateFacility.Request): CreateFacility.Response {
        val id = facilityGateway.create(request.toDomain())
        return CreateFacility.Response(id)
    }
}

fun CreateFacility.Request.toDomain(): Facility {
    return Facility(UUID.randomUUID().toString(), tags)
}