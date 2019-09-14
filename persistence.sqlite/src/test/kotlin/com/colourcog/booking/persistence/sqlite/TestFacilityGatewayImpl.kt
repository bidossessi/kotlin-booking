package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.domain.gateway.FacilitiesQuery
import com.colourcog.booking.domain.gateway.FacilityGateway
import com.colourcog.booking.persistence.sqlite.gateway.FacilityGatewayImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class TestFacilityGatewayImpl {

    val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = FacilityGatewayImpl(connection)

    @Test
    fun `assert we can create facilities`() {
        gateway.create(Facility("c", listOf("a", "b", "d")))
    }

    @Test
    fun `assert we find the correct facilities with a search`() {
        val facilities = listOf<Facility>(
            Facility("first", listOf("a", "b", "c")),
            Facility("second", listOf("a", "d", "c")),
            Facility("third", listOf("r", "d", "w")),
            Facility("fourth", listOf("h", "r", "g")),
            Facility("fifth", listOf("e", "b", "d"))
        )
        facilities.forEach { gateway.create(it) }

        val query = FacilitiesQuery(listOf("a", "g"))
        val expected = listOf<String>("first", "second", "fourth")
        val results = gateway.findFacilities(query)
        val ids = results.map { it.id }.toList()
        Assertions.assertEquals(expected, ids)
    }

    @Test
    fun `what happens if there's nothing to fetch?`() {
        val query = FacilitiesQuery(listOf("a", "g"))
        val expected = listOf<String>()
        val results = gateway.findFacilities(query)
        val ids = results.map { it.id }.toList()
        Assertions.assertEquals(expected, ids)
    }

    @Test
    fun `make sure we get the correct exception when the facility doesn't exist`() {
        Assertions.assertThrows(NoSuchFacilityException::class.java)  {
            gateway.getFacility("unknown")
        }
    }

    @Test
    fun `assert we can get a single facility`() {
        val facilities = listOf<Facility>(
            Facility("first", listOf("a", "b", "c")),
            Facility("second", listOf("a", "d", "c")),
            Facility("third", listOf("r", "d", "w")),
            Facility("fourth", listOf("h", "r", "g")),
            Facility("fifth", listOf("e", "b", "d"))
        )
        facilities.forEach { gateway.create(it) }

        val f = gateway.getFacility("third")
        Assertions.assertEquals(listOf("r", "d", "w"), f.tags)
    }

    @AfterEach
    fun tearDown() {
        connection.close()
    }
}
