package com.example.fitflow20
data class Users(
    val id: String,
    val userNama: String? = null,
    val email: String?,
    val phoneNumber: String,
    val gender : String
) {
    constructor():this("","","","","")

}