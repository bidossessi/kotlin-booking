package com.colourcog.booking.application

import com.colourcog.booking.api.CreateFacility
import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestCreateFacilityService {

    @Test
    fun `we call the proper methods`() {
        //given
        val gateway: FacilityGateway = mockk()
        every { gateway.create(any()) } returns "created"
        val useCase = CreateFacilityService(gateway)
        val req = CreateFacility.Request(listOf("a", "b", "c"))

        //when
        val resp = useCase.create(req)

        //then
        verify { gateway.create(any()) }
        Assertions.assertEquals("created", resp.id)
    }
}