package studio.hcmc.reminisce.io.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val DATA_STORE = "userAuthInstance"
private val Context.datastore by preferencesDataStore(DATA_STORE)

data class UserAuthVO(
    val email: String,
    val password: String
) {
    companion object {
        val emailKey = stringPreferencesKey("email")
        val passwordKey = stringPreferencesKey("password")
    }
    suspend fun save(context: Context) {
        context.datastore.edit {
            it[emailKey] = email
            it[passwordKey] = password
        }
    }

    suspend fun delete(context: Context) {
        context.datastore.edit {
            it.clear()
        }
    }
}

suspend fun UserAuthVO(context: Context): UserAuthVO? {
    val (email, password) = context.datastore.data
        .map { it[UserAuthVO.emailKey] to it[UserAuthVO.passwordKey] }
        .firstOrNull() ?: return null
    if (email == null || password == null) {
        return null
    }

    return UserAuthVO(email, password)
}