package com.example.adirtkaanki.data

class Database {
    fun login(username: String, password: String): Boolean {
        return username == "admin" && password == "1234"
    }

    fun register(username: String, password: String): Boolean {
        return username == "admin" && password == "1234"
    }
}