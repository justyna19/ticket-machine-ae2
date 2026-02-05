package admin

import domain.Destination

/*
This class provides admin operations over the list of destinations
- Does not handle login operations
*/
class AdminService(private val destinations: MutableList<Destination>) {


/* methods and functions to be written here */

    // Function to allow the admin to print the list destination stored
    fun listDestinations() {
        if (destinations.isEmpty()) {
            println("\nNo destinations available")
            return
        }
        destinations.forEach { d->
            println("ID: ${d.id} - Destination: ${d.name} - SinglePrice:£${"%.2f".format(d.singlePrice)} -" +
                " ReturnPrice:£${"%.2f".format(d.returnPrice)} - Sales:${d.sales}") }

    }

    /* Function created to allow admin to add destination to its portfolio by using 4 params (id, name, singlePrice
    and returnPrice)
    */
    fun addDestination(
        id: Int,
        name: String,
        singlePrice: Double,
        returnPrice: Double) {

        val destination = Destination(id, name, singlePrice, returnPrice, 0)
        destinations.add(destination)
        println("\nAdded Destination:")
        println("Id: ${destination.id} - Destination: ${destination.name} - " +
                "SinglePrice:£${destination.singlePrice} - ReturnPrice:£${destination.returnPrice}")

    }

    // Function that allows admin to change the price of either single or return ticket
    // Usage of indexOfFirst to return a match case by Name, so admin is allowed to update price by name of destination

    fun changePrice(
        name: String,
        newSingle: Double,
        newReturn: Double
    ) {
        val index = destinations.indexOfFirst { d ->
            d.name.equals(name, ignoreCase = true) }

        if (index == -1) {
            println("\nDestination '$name' not found.")
            return
        }

        val current = destinations[index]
        val updated = current.copy(
            singlePrice = newSingle,
            returnPrice = newReturn
        )

        destinations[index] = updated
        println("\nUpdated Price:")
        println("ID: ${updated.id} - Destination: ${updated.name} - " +
                "SinglePrice:£${updated.singlePrice} - ReturnPrice:£${updated.returnPrice}")
    }

    // Function that adjust price of all destination by a given factor

    fun adjustPriceByFactor(factor: Double) {
        destinations.forEach { d ->
            d.singlePrice *= factor
            d.returnPrice *= factor
        }
    }

    // Create function to add IDs automatically when adding destination
    fun getNextId(): Int = destinations.size + 1
}