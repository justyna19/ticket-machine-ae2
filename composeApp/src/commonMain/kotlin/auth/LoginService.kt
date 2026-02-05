package auth

/* This class is responsible for handling login and keeping track of the current user logged. */

class LoginService(
    private val users: List<User> // List of all users in the system

) {
    // currently logged in user or null if nobody is logged in
    var currentUser: User? = null
    private set

    /* Ask for username and password and log the user in.
    @return true if login was successful, false otherwise.
     */
    fun login(): Boolean {
        println("\n=== Login ===")

        print("Username: ")
        val username = readln().trim()

        print("Password: ")
        val password = readln().trim()

        // try to match a user
        val user = users.find { it.username == username && it.password == password }

        return if (user != null) {
            currentUser = user
            println("\nLogin successful. Welcome ${user.username}.")
            true
        } else {
             println("Login failed. Invalid username or password.")
             login()

        }
    }
    //Logs out the current user.

    fun logout() {
        if (currentUser != null) {
            println("User ${currentUser?.username} logged out.")
        }
        currentUser = null
    }

    //Returns true if the currently logged-in user is an admin.

    fun isAdminLoggedIn(): Boolean {
        return currentUser?.userType == UserType.ADMIN
    }

    /* more methods (logout) will do next */
}
