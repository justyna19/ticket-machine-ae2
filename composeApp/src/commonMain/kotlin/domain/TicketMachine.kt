package domain
import domain.Destination
import domain.CurrentSelection
import domain.TicketType
import domain.Ticket

/*
This class represents the body of the ticket machine system
- It holds the list of destinations and keeps track of user selections
 */
class TicketMachine(
    val origin: String,
    val destinations: MutableList<Destination>
) {
    var moneyInserted: Double = 0.0
        private set
    var currentSelection: CurrentSelection? = null
        private set

    /* Below are the methods in this class
    * ListDestinations is responsible to input all destinations to the user */

    fun listDestinations() {
        if (destinations.isEmpty()) {
            print("Apologies, there are no destinations available at the moment")
            return
        }
        println("\nAvailable destinations from $origin:")
        for (destination in destinations) {
            // Prints the ID of each destination so the user can pick by number
            println(
                "${destination.id}. ${destination.name} " +
                        "(Single: £${destination.singlePrice}, " +
                        "Return: £${destination.returnPrice})"
            )
        }
    }

    // This class searches for a destination by its name
    // The search is case-insensitive and returns null if not found.
    fun searchDestination(name: String): Destination? {
        return destinations.firstOrNull {
            it.name.equals(name, ignoreCase = true)
        }
    }

    /**
     * This method does the below:
     * 1. Looks up the destination by name.
     * 2. Calculates the price for the requested ticket type.
     * 3. Stores everything into currentSelection.
     *
     * Returns true if the selection was successful, false otherwise.
     */
    fun selectTicket(destinationName: String, ticketType: TicketType): Boolean {
        val destination = searchDestination(destinationName)

        if (destination == null) {
            println("Destination $destinationName not found.")
            return false
        }
        val price = destination.getPrice(ticketType)

        currentSelection = CurrentSelection(
            origin = origin,
            destination = destination,
            ticketType = ticketType,
            totalPrice = price
        )

        println("Selected: $origin to ${destination.name}, " + "Type: $ticketType, Price: £$price")
        return true
    }

    /**
     * this method adds money to the ticket machine
     * it adds the amount to moneyInserted, only if it is a positive amount.
     */
    fun insertMoney(amount: Double) {
        if (amount <= 0.0) {
            println("Please insert a positive amount.")
            return
        }
        moneyInserted += amount
        println("You inserted £$amount. Total available: £$moneyInserted")
    }

    /**
     * This method is responsible for checking whether the user has inserted enough money
     * only for the current selection.
     */
    fun canPurchase(): Boolean {
        val selection = currentSelection

        if (selection == null) {
            println("No ticket selected yet.")
            return false
        }

        return if (moneyInserted >= selection.totalPrice) {
            println("Buying ticket...")
            true

        } else {
            val missing = selection.totalPrice - moneyInserted
            println("You still need £%.2f more to buy this ticket.".format(missing))
            false
        }
    }
    /**
     * This function tries to complete the purchase.
     *
     * Steps:
     * 1. Check if we have a current selection.
     * 2. Check if enough money has been inserted.
     * 3. Create a Ticket object.
     * 4. Print the ticket.
     * 5. Return change (just printed to console).
     * 6. Reset the machine for the next customer.
     *
     * Returns the created Ticket, or null if anything failed.
    */
    fun purchase(): Ticket? {
        val selection = currentSelection

        if (selection == null) {
            println("No ticket selected yet.")
            return null
        }
        if(!canPurchase()) {
            // prints why it can't purchase
            return null
        }

        selection.destination.sales++

        // creates the ticket
        val ticket = Ticket(
            origin = selection.origin,
            destination = selection.destination.name,
            ticketType =  selection.ticketType,
            price = selection.totalPrice
        )
        // print the ticket to the console
        ticket.printTicket()

        // calculate and show change if there is any
        val change = moneyInserted - selection.totalPrice
        if (change > 0.0) {
            println("\nYour change is £%.2f".format(change))
        }
        // Prepares the machine for the next user.
        reset()
        return ticket
    }
    /**
     * Resets the machine by cleaning the inseted money and removes the current selection
     */
    fun reset() {
        moneyInserted = 0.0
        currentSelection = null
        println("\nThank you for your purchase.")
    }
}