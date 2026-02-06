package domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DestinationTest {

    @Test
    // Tests that the correct single ticket price is returned for a destination
    fun singleTicketPriceIsReturnedCorrectly() {
        val destination = Destination(
            id = 1,
            name = "Central",
            singlePrice = 3.0,
            returnPrice = 5.0,
            sales = 0
        )

        val price = destination.getPrice(TicketType.Single)
        assertEquals(3.0, price)
    }

    @Test
    // Tests that the correct return ticket price is returned for a destination
    fun returnTicketPriceIsReturnedCorrectly() {
        val destination = Destination(
            id = 1,
            name = "Central",
            singlePrice = 3.0,
            returnPrice = 5.0,
            sales = 0
        )

        val price = destination.getPrice(TicketType.Return)
        assertEquals(5.0, price)
    }
}
