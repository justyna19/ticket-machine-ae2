package domain

/* This class is to represent the current selection made by the user */
data class CurrentSelection(
    val origin: String,
    val destination: Destination,
    val ticketType: TicketType,
    val totalPrice: Double,
)