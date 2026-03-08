package com.example.adirtkaanki.data


sealed class LoginResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String
    ) : LoginResult()

    data class Error(
        val message: String
    ) : LoginResult()
}

class AuthRepository(
    private val database: Database
) {
    fun login(username: String, password: String): LoginResult {
        return if (database.login(username, password)) {
            LoginResult.Success(
                accessToken = "fake_access_token_$username",
                refreshToken = "fake_refresh_token_$username"
            )
        } else {
            LoginResult.Error("Неверный логин или пароль")
        }
    }

    fun register(username: String, password: String): LoginResult {
        return if (database.register(username, password)) {
            LoginResult.Success(
                accessToken = "fake_access_token_$username",
                refreshToken = "fake_refresh_token_$username"
            )
        } else {
            LoginResult.Error("Что-то пошло не так")
        }
    }
}