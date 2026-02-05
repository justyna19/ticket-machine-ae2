package domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DestinationTest {

    // Tests that single ticket price is returned correctly for a destination
    @Test
    fun singleTicketPriceIsReturnedCorrectly() {
        val d = Destination(
            id = 1,
            name = "Central",
            singlePrice = 3.0,
            returnPrice = 5.0,
            sales = 0
        )
        assertEquals(3.0, d.getPrice(TicketType.Single))
    }
    // Tests that return ticket price is returned correctly for a destination
    @Test
    fun returnTicketPriceIsReturnedCorrectly() {
        val d = Destination(
            id = 1,
            name = "Central",
            singlePrice = 3.0,
            returnPrice = 5.0,
            sales = 0
        )
        assertEquals(5.0, d.getPrice(TicketType.Return))
    }
}
