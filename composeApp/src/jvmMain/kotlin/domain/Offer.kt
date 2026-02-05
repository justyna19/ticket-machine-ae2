package domain

data class Offer(
    val id: Int,
    val destinationId: Int,
    val discountPercent: Double,
    val startDate: String,  // YYYY-MM-DD
    val endDate: String,    // YYYY-MM-DD
    val afterTime: String?  // HH:MM or null
)
