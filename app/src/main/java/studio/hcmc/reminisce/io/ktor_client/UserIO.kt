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
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.vo.user.UserVO

object UserIO {
    suspend fun signUp(dto: UserDTO.Post): Int {
        return httpClient
            .post("/user") {
                setBody(Gson().toJsonTree(dto))
            }.body()
    }

    suspend fun login(dto: UserDTO.Post): UserVO {
        return httpClient
            .post("/login") {
            setBody(Gson().toJsonTree(dto))
        }.body()
    }

    suspend fun patch(id: Int, dto: UserDTO.Patch) {
        httpClient.patch("/user/${id}") {
            setBody(Gson().toJsonTree(dto))
        }.bodyAsText()
    }

    suspend fun delete(id: Int) {
        httpClient.delete("/user/${id}").bodyAsText()
    }

    suspend fun getById(id: Int): UserVO {
        return httpClient.get("/user") {
            parameter("id", id)
        }.body()
    }

    suspend fun getByEmail(email: String): UserVO {
        return httpClient.get("/user") {
            parameter("email", email)
        }.body()
    }
}