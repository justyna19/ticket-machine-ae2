package data

import data.Database

class AdminRepository {

    fun validateLogin(username: String, password: String): Boolean {
        println("TEST LOGIN -> username='$username' password='$password'")

        return try {
            Database.connect().use { conn ->
                conn.prepareStatement(
                    "SELECT 1 FROM admin_users WHERE username = ? AND password = ?"
                ).use { ps ->
                    ps.setString(1, username.trim())
                    ps.setString(2, password.trim())
                    val rs = ps.executeQuery()
                    val ok = rs.next()
                    println("TEST LOGIN RESULT -> $ok")
                    ok
                }
            }
        } catch (e: Exception) {
            println("TEST LOGIN ERROR -> ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
