package studio.hcmc.reminisce.io.ktor_client

import io.ktor.utils.io.errors.IOException
import java.sql.Timestamp

open class SpringException(
    val timestamp: Timestamp?,
    val status: Int?,
    val error: String?,
    val path: String?
): IOException()


