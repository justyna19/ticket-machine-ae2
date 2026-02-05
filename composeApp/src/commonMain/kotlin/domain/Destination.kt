package domain

/* This Class represents a local station with its ticket prices */
data class Destination(
    val id: Int,
    val name: String,
    var singlePrice: Double,
    var returnPrice: Double,
    var sales: Int
) {
/* The getPrice function returns the correct price depending on the ticket price */
    fun getPrice(ticketType: TicketType): Double {
        return when (ticketType) {
            TicketType.Single -> singlePrice
            TicketType.Return -> returnPrice
        }
    }
}


