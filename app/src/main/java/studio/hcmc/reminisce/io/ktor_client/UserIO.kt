package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.user.UserVO

object UserIO {
    suspend fun signUp(dto: UserDTO.Post): CategoryVO {
        return httpClient
            .post("/user") { setBody(Gson().toJsonTree(dto)) }
            .body()
    }

    suspend fun login(dto: UserAuthVO): UserVO {
        val loginDto = UserDTO.Post().apply {
            email = dto.email
            password = dto.password.sha512
        }
        LocalLogger.v("sending info: ${loginDto.email}, ${loginDto.password}")
        return httpClient
            .post("/user/signIn") { setBody(Gson().toJsonTree(loginDto)) }
            .body()
    }

    suspend fun patch(id: Int, dto: UserDTO.Patch) {
        httpClient
            .patch("/user/${id}") { setBody(Gson().toJsonTree(dto)) }
            .bodyAsText()
    }

    suspend fun delete(id: Int) {
        httpClient
            .delete("/user/${id}")
            .bodyAsText()
    }

    suspend fun getById(id: Int): UserVO {
        return httpClient
            .get("/user") { parameter("id", id) }
            .body()
    }

    suspend fun getByEmail(email: String): UserVO {
        return httpClient
            .get("/user") { parameter("email", email) }
            .body()
    }

    suspend fun testEmail(email: String): CategoryVO {
        val get = httpClient.post("/user") { parameter("email", email) }
        val response = get.call.body<SpringException>()
        LocalLogger.v("call body springException -> $response")
        val s = get.call.response.status.value
        val ss = get.call.response.status.description
        LocalLogger.v("call response status value: $s || call response status descriptions: $ss")

        return httpClient
            .post("/user") { parameter("email", email) }
            .body()
    }
}