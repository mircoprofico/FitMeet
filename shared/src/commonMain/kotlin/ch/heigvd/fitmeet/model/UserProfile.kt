package ch.heigvd.fitmeet.model

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val bio: String,
    val sports: List<UserSport>,
    val activitiesCreated: Int,
    val activitiesJoined: Int,
) {
    val fullName: String get() = "$firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}
