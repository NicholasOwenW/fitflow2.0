package com.example.fitflow20
data class Users(
    val uid: String="",
    val userNama: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val workoutDay: Map<String, WorkoutDay> = emptyMap()
)

data class WorkoutDay(
    val listOfWorkouts: List<String> = emptyList()
)