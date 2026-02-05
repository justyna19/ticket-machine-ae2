package data.repo

import data.CardsDatabase

class CardRepository {

    fun getCredit(cardNumber: String): Double? {
        CardsDatabase.connect().use { conn ->
            conn.prepareStatement(
                "SELECT credit FROM cards WHERE card_number = ?"
            ).use { ps ->
                ps.setString(1, cardNumber)
                val rs = ps.executeQuery()
                return if (rs.next()) rs.getDouble("credit") else null
            }
        }
    }

    fun updateCredit(cardNumber: String, newCredit: Double) {
        CardsDatabase.connect().use { conn ->
            conn.prepareStatement(
                "UPDATE cards SET credit = ? WHERE card_number = ?"
            ).use { ps ->
                ps.setDouble(1, newCredit)
                ps.setString(2, cardNumber)
                ps.executeUpdate()
            }
        }
    }
}
