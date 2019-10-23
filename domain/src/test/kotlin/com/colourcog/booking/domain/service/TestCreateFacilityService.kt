package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class TestCreateFacilityService {

    val gateway: FacilityGateway = mockk(relaxed = true)
    val useCase = CreateFacilityService(gateway)

    @Test
    fun `we call the proper methods`() {
        //given
        val tags = listOf("a", "b", "c")

        //when
        val resp = useCase.create(tags)
        verify { gateway.create(any()) }
        confirmVerified(gateway)

    }
}