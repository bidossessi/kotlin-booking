package com.colourcog.booking.domain.entities

import com.colourcog.booking.domain.errors.InvalidTimeFrameException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import java.time.LocalDateTime

class TestTimeFrame {

    @Test
    fun `crossed dates are invalid`() {
        assertThrows(InvalidTimeFrameException::class.java) {
            TimeFrame(
                LocalDateTime.of(2019, 10, 1, 0,0),
                LocalDateTime.of(2019, 10, 1, 0 ,0)
            )
        }
    }
}