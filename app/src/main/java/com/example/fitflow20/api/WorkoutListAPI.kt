package com.example.fitflow20.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutListAPI {
    @GET("v1/exercises")
    suspend fun getWorkout(@Query("name") name: String? = null): Response<List<Workout>>
    //@Query("muscle")muscle : String
}