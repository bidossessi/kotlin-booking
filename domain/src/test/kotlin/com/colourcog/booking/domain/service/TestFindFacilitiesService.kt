package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.gateways.BookingGateway
import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestFindFacilitiesService {
    val facilityGateway: FacilityGateway = mockk(relaxed = true)
    val bookingGateway: BookingGateway = mockk(relaxed = true)
    val useCase = FindFacilitiesService(facilityGateway, bookingGateway)

    @Test
    fun `we call the find with excluded`() {
        //given
        val timeFrame = TimeFrame(
            LocalDateTime.of(2050,1,1,0,0),
            LocalDateTime.of(2050, 2, 1, 0, 0)
        )
        coEvery { bookingGateway.getBookedFacilityIds(any()) } returns emptyList()
        coEvery { facilityGateway.find(including = any(), excluding = any(), tags = any()) } returns emptySequence()

        //when
        runBlocking { useCase.action(listOf("a", "b"), timeFrame) }

        //then
        coVerify { facilityGateway.find(any(), any(), any()) }
        coVerify { bookingGateway.getBookedFacilityIds(any()) }
        confirmVerified(facilityGateway)
        confirmVerified(bookingGateway)
    }
    @Test
    fun `we call the find with tags only`() {
        //given
        coEvery { bookingGateway.getBookedFacilityIds(any()) } returns emptyList()
        coEvery { facilityGateway.find(including = any(), excluding = any(), tags = any()) } returns emptySequence()

        //when
        runBlocking { useCase.action(listOf("a", "b")) }

        //then
        coVerify { facilityGateway.find(tags = any())}
        coVerify { bookingGateway wasNot Called }
        confirmVerified(facilityGateway)
        confirmVerified(bookingGateway)
    }
}