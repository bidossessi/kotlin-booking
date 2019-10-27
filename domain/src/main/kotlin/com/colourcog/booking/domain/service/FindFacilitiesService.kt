package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.api.FindFacilities
import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.gateways.FacilityGateway
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.gateways.BookingGateway
import java.util.*

class FindFacilitiesService(
    private val facilityGateway: FacilityGateway,
    private val bookingGateway: BookingGateway
): FindFacilities {
    override suspend fun action(tags: List<String>, timeFrame: TimeFrame?): Sequence<Facility> {
        // an Available facility is one that has
        // 1. one or more of the given tags
        // 2. NO bookings matching a given timeFrame
        // 3. NO intrinsic restrictions matching a given timeFrame
        val bookedIds = timeFrame?.let { bookingGateway.getBookedFacilityIds(it) }
        return bookedIds?.let {
            facilityGateway.find(excluding = it, tags = tags)
        } ?: kotlin.run {
            facilityGateway.find(tags = tags)
        }
    }
}

