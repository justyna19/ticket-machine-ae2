package data

import data.Database
import domain.Destination

class DestinationRepository {

    fun getAll(): List<Destination> {
        val list = mutableListOf<Destination>()

        Database.connect().use { conn ->
            val rs = conn.createStatement().executeQuery(
                """
                SELECT id, name, single_price, return_price, sales
                FROM destinations
                ORDER BY name
                """
            )

            while (rs.next()) {
                list.add(
                    Destination(
                        id = rs.getInt("id"),
                        name = rs.getString("name"),
                        singlePrice = rs.getDouble("single_price"),
                        returnPrice = rs.getDouble("return_price"),
                        sales = rs.getInt("sales")
                    )
                )
            }
        }
        return list
    }

    fun updatePrices(id: Int, single: Double, ret: Double) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                UPDATE destinations
                SET single_price = ?, return_price = ?
                WHERE id = ?
                """
            ).use { ps ->
                ps.setDouble(1, single)
                ps.setDouble(2, ret)
                ps.setInt(3, id)
                ps.executeUpdate()
            }
        }
    }

    fun incrementSales(id: Int) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                UPDATE destinations
                SET sales = sales + 1
                WHERE id = ?
                """
            ).use { ps ->
                ps.setInt(1, id)
                ps.executeUpdate()
            }
        }
    }

    fun addTakings(id: Int, amount: Double) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                UPDATE destinations
                SET takings = takings + ?
                WHERE id = ?
                """
            ).use { ps ->
                ps.setDouble(1, amount)
                ps.setInt(2, id)
                ps.executeUpdate()
            }
        }
    }

    fun applyPriceFactor(factor: Double) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                UPDATE destinations
                SET 
                    single_price = single_price * ?,
                    return_price = return_price * ?
                """
            ).use { ps ->
                ps.setDouble(1, factor)
                ps.setDouble(2, factor)
                ps.executeUpdate()
            }
        }
    }

    // ===== 9.3 ADD DESTINATION =====
    fun addDestination(name: String, single: Double, ret: Double) {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO destinations (name, single_price, return_price, sales, takings)
                VALUES (?, ?, ?, 0, 0)
                """
            ).use { ps ->
                ps.setString(1, name)
                ps.setDouble(2, single)
                ps.setDouble(3, ret)
                ps.executeUpdate()
            }
        }
    }
}
