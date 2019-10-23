package com.colourcog.booking.domain.api

import com.colourcog.booking.domain.entities.Facility

interface FindFacilities {
    fun findByTags(tags: List<String>): Sequence<Facility>
}