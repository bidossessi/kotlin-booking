package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.persistence.sqlite.gateway.FacilityGatewayImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.sql.DriverManager
import java.util.*


class TestFacilityGatewayImpl {

    val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = FacilityGatewayImpl(connection)

    val facilityUUID = UUID.randomUUID()
    val facilities = listOf<Facility>(
        Facility(UUID.randomUUID(), listOf("a", "b", "c")),
        Facility(UUID.randomUUID(), listOf("a", "d", "c")),
        Facility(facilityUUID, listOf("r", "d", "w")),
        Facility(UUID.randomUUID(), listOf("h", "r", "g")),
        Facility(UUID.randomUUID(), listOf("e", "b", "d"))
    )

    @ExperimentalCoroutinesApi
    @Test
    fun `assert we can create facilities`() {
        assertDoesNotThrow {
            runBlockingTest { gateway.create(Facility(UUID.randomUUID(), listOf("fee", "fi", "fo"))) }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `assert we find the correct facilities with a search`() = runBlockingTest {
        gateway.create(facilities)
        val results = gateway.find(tags = listOf("a", "g"))
        assertEquals(3, results.count())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `a query with no matches returns an empty sequence`() = runBlockingTest {
        val results = gateway.find(tags = listOf("q", "m"))
        assertEquals(0, results.count())
    }

    @Test
    fun `make sure we get the correct exception when the facility doesn't exist`() {
        assertThrows<NoSuchFacilityException> {
            runBlocking { gateway.getFacility(UUID.randomUUID()) }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `assert we can get a single facility`() = runBlockingTest {
        gateway.create(facilities)
        val f = gateway.getFacility(facilityUUID)
        assertEquals(listOf("r", "d", "w"), f.tags)
    }
}
