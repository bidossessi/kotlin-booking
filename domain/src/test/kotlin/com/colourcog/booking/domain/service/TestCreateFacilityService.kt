package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class TestCreateFacilityService {

    val gateway: FacilityGateway = mockk(relaxed = true)
    val useCase = CreateFacilityService(gateway)

    @ExperimentalCoroutinesApi
    @Test
    fun `we call the proper methods`() = runBlockingTest {
        //given
        val tags = listOf("a", "b", "c")

        //when
        useCase.action(tags)
        coVerify { gateway.create(facility = any()) }
        confirmVerified(gateway)
    }
}