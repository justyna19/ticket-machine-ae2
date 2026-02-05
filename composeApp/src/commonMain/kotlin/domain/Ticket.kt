package domain

/* The class Ticket represents a ticket that has been successfully purchased */

data class Ticket(
    val origin: String,
    val destination: String,
    val ticketType: TicketType,
    val price: Double
) {
/* the printTicket method is responsible for printing the ticket to the user*/

    fun printTicket() {
        println("\n***")
        println("[$origin]")
        println("to")
        println("[$destination]")
        println("Price: £%.2f [%s]".format(price, ticketType.name))
        print("***\n")
    }
}