package studio.hcmc.reminisce.ext.user

import android.content.Context
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.vo.user.UserVO

object UserExtension {
    private var _user: UserVO? = null

    fun getUserOrThrow(): UserVO {
        return _user!!
    }

    suspend fun getUser(context: Context): UserVO {
        _user?.let { return it }

        val auth = UserAuthVO(context) ?: throw IllegalStateException("Cannot sign in without auth.")
        val user = UserIO.login(auth)
        _user = user

        return user
    }

    fun getUserOrNull(): UserVO? {
        return _user
    }

    fun setUser(user: UserVO?) {
        _user = user
    }
}