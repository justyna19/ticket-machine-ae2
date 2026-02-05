package offers

// This class represents a special offer for a destination.
// Dates are strings to keep it simple

data class SpecialOffer(
    val id: Int,
    val destinationName: String,
    val startDate: String,
    val endDate: String,
    val setPrice: Double
)