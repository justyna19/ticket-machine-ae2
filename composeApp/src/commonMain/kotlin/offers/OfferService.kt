package offers

/**
 * Manages special offers: add, list, search, delete.
 */
class OfferService {

    private val offers = mutableListOf<SpecialOffer>()
    private var nextId = 1

    fun addOffer(
        destinationName: String,
        startDate: String,
        endDate: String,
        setPrice: Double
    ): SpecialOffer {
        val offer = SpecialOffer(
            id = nextId++,
            destinationName = destinationName,
            startDate = startDate,
            endDate = endDate,
            setPrice = setPrice
        )
        offers.add(offer)
        return offer
    }

    fun listOffers(): List<SpecialOffer> = offers.toList()

    fun searchOffers(query: String): List<SpecialOffer> {
        val q = query.lowercase()
        return offers.filter {
            it.destinationName.lowercase().contains(q)
        }
    }

    fun deleteOfferById(id: Int): Boolean {
        return offers.removeIf { it.id == id }
    }
}
