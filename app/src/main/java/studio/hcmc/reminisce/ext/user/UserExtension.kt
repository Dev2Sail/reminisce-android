package studio.hcmc.reminisce.ext.user

import android.content.Context
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.vo.user.UserVO

object UserExtension {
    private lateinit var _user: UserVO

    fun getUserOrThrow(): UserVO {
        return _user
    }

    suspend fun getUser(context: Context): UserVO {
        if (this::_user.isInitialized) {
            return _user
        }

        val auth = UserAuthVO(context) ?: throw IllegalStateException("Cannot sign in without auth.")
        val user = UserIO.login(auth)
        _user = user

        return user
    }

    fun getUserOrNull(): UserVO? {
        if (this::_user.isInitialized) {
            return _user
        } else {
            return null
        }
    }

    fun setUser(user: UserVO) {
        _user = user
    }
}