package com.example.coffepet

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.intPreferencesKey

// 🔹 Extensión global: solo esta, fuera de la clase
val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USERS_KEY = stringPreferencesKey("users")
    }

    // 🟤 Guardar monedas por usuario
    suspend fun saveCoinsForUser(email: String, coins: Int) {
        val key = intPreferencesKey("coins_$email")
        context.userDataStore.edit { prefs ->
            prefs[key] = coins
        }
    }

    // 🟤 Leer monedas del usuario
    fun getCoinsForUser(email: String): Flow<Int> {
        val key = intPreferencesKey("coins_$email")
        return context.userDataStore.data.map { prefs ->
            prefs[key] ?: 0 // si no tiene monedas aún, empieza con 0
        }
    }

    // 🟤 Guardar usuario
    suspend fun saveUser(email: String, password: String) {
        context.userDataStore.edit { prefs ->
            val current = prefs[USERS_KEY] ?: ""
            val updated = if (current.isEmpty()) {
                "$email:$password"
            } else {
                "$current|$email:$password"
            }
            prefs[USERS_KEY] = updated
        }
    }

    // 🟤 Obtener usuarios
    fun getUsers(): Flow<Map<String, String>> {
        return context.userDataStore.data.map { prefs ->
            val saved = prefs[USERS_KEY] ?: ""
            val users = mutableMapOf<String, String>()
            if (saved.isNotEmpty()) {
                saved.split("|").forEach { entry ->
                    val parts = entry.split(":")
                    if (parts.size == 2) users[parts[0]] = parts[1]
                }
            }
            users
        }
    }
}
