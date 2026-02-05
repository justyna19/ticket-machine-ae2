package data

import java.sql.Connection
import java.sql.DriverManager
import java.io.File

object Database {

    private const val DB_NAME = "ticket_machine.db"

    fun connect(): Connection {
        val dbFile = File(DB_NAME).absolutePath
        println("USING DATABASE FILE: $dbFile")

        val conn = DriverManager.getConnection("jdbc:sqlite:$dbFile")

        // Ensure tables exist
        conn.createStatement().use { st ->
            st.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS admin_users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
                """.trimIndent()
            )
        }

        // Ensure default admin exists
        conn.prepareStatement(
            """
            INSERT OR IGNORE INTO admin_users(username, password)
            VALUES('admin','admin123');
            """.trimIndent()
        ).use { it.executeUpdate() }

        return conn
    }
}
