package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entity.Facility
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
    fun `correct statements are created from facility`() {
        val facility = Facility("fake", listOf("a", "b", "c"))
        val statements = gateway.buildCreateStatements(facility)
        val firstExpected = "INSERT into facilities (id, tags) VALUES ('fake', 'a, b, c')"
        Assertions.assertEquals(firstExpected, statements.first)
        val secondExpected = "INSERT into facilityTags (facilityId, tag) VALUES ('fake', 'a'), ('fake', 'b'), ('fake', 'c')"
        Assertions.assertEquals(secondExpected, statements.second)
    }

    @Test
    fun `correct select statement is created from query`() {
        val query = FacilitiesQuery(listOf("a", "b", "c"))
        val actual = gateway.buildFindStatement(query)
        val expected = "SELECT DISTINCT facilities.* from facilityTags JOIN facilities on facilityTags.facilityId = facilities.id WHERE tag in ('a', 'b', 'c')"
        Assertions.assertEquals(expected, actual)
    }

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
