data class Users(
    val id: String = "",
    val userNama: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val workoutDays: Map<String, List<String>> = emptyMap()
)
