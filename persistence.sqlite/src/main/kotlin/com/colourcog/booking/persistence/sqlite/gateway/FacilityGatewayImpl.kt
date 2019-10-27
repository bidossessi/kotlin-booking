package com.colourcog.booking.persistence.sqlite.gateway

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.domain.gateways.FacilityGateway
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*

class FacilityGatewayImpl(private val connection: Connection) : FacilityGateway {

    private val facilitiesTable = "facilities"
    private val tagsTable = "facilityTags"
    private val createStmt: PreparedStatement
    private val createTagStmt: PreparedStatement
    private val getStmt: PreparedStatement

    init {
        val facilitiesInit =
            """
            CREATE TABLE IF NOT EXISTS $facilitiesTable (id string PRIMARY KEY, tags string, createdAt DATETIME DEFAULT CURRENT_TIMESTAMP)
            """.trimIndent()
        val tagsInit = "CREATE TABLE IF NOT EXISTS $tagsTable (facilityId string KEY, tag string)"

        val statement = connection.createStatement()
        statement.execute(facilitiesInit)
        statement.execute(tagsInit)
        getStmt = connection.prepareStatement("SELECT * from $facilitiesTable WHERE id = ?")
        createStmt = connection.prepareStatement("INSERT into $facilitiesTable (id, tags) VALUES (?, ?)")
        createTagStmt = connection.prepareStatement("INSERT into $tagsTable (facilityId, tag) VALUES (?, ?)")
    }

    override suspend fun create(facility: Facility): Boolean {
        createStmt.setString(1, facility.id.toString())
        createStmt.setString(2, facility.tags.joinToString())
        createStmt.executeUpdate()
        for (tag in facility.tags) {
            createTagStmt.setString(1, facility.id.toString())
            createTagStmt.setString(2, tag)
            createTagStmt.addBatch()
        }
        createTagStmt.executeBatch()
        return true
    }

    override suspend fun create(facilities: List<Facility>): Boolean {
        for (facility in facilities) {
            createStmt.setString(1, facility.id.toString())
            createStmt.setString(2, facility.tags.joinToString())
            createStmt.addBatch()
            for (tag in facility.tags) {
                createTagStmt.setString(1, facility.id.toString())
                createTagStmt.setString(2, tag)
                createTagStmt.addBatch()
            }
        }
        createStmt.executeBatch()
        createTagStmt.executeBatch()
        return true
    }

    private fun buildFindStatement(including: List<UUID>?, excluding: List<UUID>?, tags: List<String>?): String {
        val tagsLine = tags?.let {
            "AND tag IN ${ it.joinToString(prefix = "(", postfix = ")") { x: String -> "'$x'" } }"
        } ?: ""
        val includeLine = including?.let {
            "AND id IN ${it.joinToString(prefix = "(", postfix = ")") { x: UUID -> "'$x'" }}"
        } ?: ""
        val excludeLine = excluding?.let {
            "AND id NOT IN ${it.joinToString(prefix = "(", postfix = ")") { x: UUID -> "'$x'" }}"
        } ?: ""

        return """
            SELECT DISTINCT ${facilitiesTable}.* from $tagsTable 
            JOIN $facilitiesTable on ${tagsTable}.facilityId = ${facilitiesTable}.id 
            WHERE 1
            $tagsLine
            $includeLine
            $excludeLine
        """.trimIndent()
    }

    override suspend fun find(including: List<UUID>?, excluding: List<UUID>?, tags: List<String>?): Sequence<Facility> {
        val findStr = buildFindStatement(including, excluding, tags)
        val statement = connection.createStatement()
        val res = statement.executeQuery(findStr)
        return sequence {
            while (res.next()) {
                yield(res.toFacility())
            }
        }
    }

    override suspend fun getFacility(id: UUID): Facility {
        getStmt.setString(1, id.toString())
        val res = getStmt.executeQuery()
        if (!res.next()) throw NoSuchFacilityException(id.toString())
        return res.toFacility()
    }

}

fun ResultSet.toFacility(): Facility = Facility(
    UUID.fromString(getString("id")),
    getString("tags").split(',').map { it.trim() }.toList()
)

