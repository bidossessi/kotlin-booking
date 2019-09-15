package com.colourcog.booking.application

import com.colourcog.booking.api.FindFacilities
import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class TestFindFacilitiesService {

    @Test
    fun `we call the proper methods`() {
        //given
        val gateway: FacilityGateway = mockk(relaxed = true)
        val useCase = FindFacilitiesService(gateway)
        val req = FindFacilities.Request(listOf("a", "b"))

        //when
        useCase.find(req)

        //then
        verify { gateway.findFacilities(any()) }
    }
}