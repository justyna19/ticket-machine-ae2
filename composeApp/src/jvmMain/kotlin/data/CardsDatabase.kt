package data

import java.sql.Connection
import java.sql.DriverManager

object CardsDatabase {

    private const val DB_URL = "jdbc:sqlite:cards.db"

    fun connect(): Connection = DriverManager.getConnection(DB_URL)

    fun init() {
        connect().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS cards (
                        card_number TEXT PRIMARY KEY,
                        credit REAL NOT NULL
                    )
                    """.trimIndent()
                )

                // Seed test cards (Stripe-like test numbers, just examples)
                stmt.execute(
                    """
                    INSERT OR IGNORE INTO cards(card_number, credit) VALUES
                        ('4242424242424242', 50.00),
                        ('4000000000009995', 5.00),
                        ('5555555555554444', 20.00)
                    """
                )
            }
        }
    }
}
