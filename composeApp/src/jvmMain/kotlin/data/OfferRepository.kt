package data

import data.Database
import domain.Offer
import java.sql.Types
import java.time.LocalDate
import java.time.LocalTime

class OfferRepository {

    fun addOffer(
        destinationId: Int,
        discountPercent: Double,
        startDate: String,
        endDate: String,
        afterTime: String?
    ) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO offers (destination_id, discount_percent, start_date, end_date, after_time)
                VALUES (?, ?, ?, ?, ?)
                """
            ).use { ps ->
                ps.setInt(1, destinationId)
                ps.setDouble(2, discountPercent)
                ps.setString(3, startDate)
                ps.setString(4, endDate)
                if (afterTime.isNullOrBlank()) ps.setNull(5, Types.VARCHAR)
                else ps.setString(5, afterTime)
                ps.executeUpdate()
            }
        }
    }

    fun getAllOffers(): List<Offer> {
        val list = mutableListOf<Offer>()
        Database.connect().use { conn ->
            val rs = conn.createStatement().executeQuery(
                """
                SELECT id, destination_id, discount_percent, start_date, end_date, after_time
                FROM offers
                ORDER BY id DESC
                """
            )
            while (rs.next()) {
                list.add(
                    Offer(
                        id = rs.getInt("id"),
                        destinationId = rs.getInt("destination_id"),
                        discountPercent = rs.getDouble("discount_percent"),
                        startDate = rs.getString("start_date"),
                        endDate = rs.getString("end_date"),
                        afterTime = rs.getString("after_time")?.takeIf { it.isNotBlank() }
                    )
                )
            }
        }
        return list
    }

    fun deleteOffer(id: Int) {
        Database.connect().use { conn ->
            conn.prepareStatement("DELETE FROM offers WHERE id = ?").use { ps ->
                ps.setInt(1, id)
                ps.executeUpdate()
            }
        }
    }

    // dla Customer — zwraca najlepszą aktywną zniżkę dla stacji
    fun getBestActiveDiscountPercent(destinationId: Int, date: LocalDate, time: LocalTime): Double? {
        var best: Double? = null

        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                SELECT discount_percent, start_date, end_date, after_time
                FROM offers
                WHERE destination_id = ?
                """
            ).use { ps ->
                ps.setInt(1, destinationId)
                val rs = ps.executeQuery()

                while (rs.next()) {
                    val discount = rs.getDouble("discount_percent")
                    val start = LocalDate.parse(rs.getString("start_date"))
                    val end = LocalDate.parse(rs.getString("end_date"))
                    val after = rs.getString("after_time")?.takeIf { it.isNotBlank() }

                    val inDate = !date.isBefore(start) && !date.isAfter(end)
                    val inTime = after == null || time >= LocalTime.parse(after)

                    if (inDate && inTime) {
                        if (best == null || discount > best!!) best = discount
                    }
                }
            }
        }
        return best
    }
}
