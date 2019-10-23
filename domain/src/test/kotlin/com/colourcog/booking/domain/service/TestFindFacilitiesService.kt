package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class TestFindFacilitiesService {
    val gateway: FacilityGateway = mockk(relaxed = true)
    val useCase = FindFacilitiesService(gateway)

    @Test
    fun `we call the proper methods`() {
        //given
        //when
        useCase.findByTags(listOf("a", "b"))

        //then
        verify { gateway.findFacilities(any()) }
        confirmVerified(gateway)
    }
}