package auth

/* This class represents as system user. The users will be hard-coded */

data class User(
    val id: Int,
    val userType: UserType,
    val username: String,
    val password: String
) {
    /* methods to be written here */
}