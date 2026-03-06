package com.example.adirtkaanki.data

class AuthRepository(
    private val database: Database
) {
    fun login(username: String, password: String): Boolean {
        return database.login(username, password)
    }

    fun register(username: String, password: String): Boolean {
        return database.register(username, password)
    }
}