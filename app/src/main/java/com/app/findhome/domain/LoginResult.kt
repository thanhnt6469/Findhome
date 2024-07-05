package com.app.findhome.domain

data class LoginResult(
    val data: UserData?,
    val errorMessage: String?,
)

data class UserData(
    val userId: String,
    val username: String?,
    val gmail: String?,
    val profilePictureUrl: String?,
    val fullName: String?,
    val role: String?
){
    constructor() : this("", null, null, null, null, null)
}
