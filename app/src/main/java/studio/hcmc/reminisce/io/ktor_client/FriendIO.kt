package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.friend.FriendDTO
import studio.hcmc.reminisce.vo.friend.FriendVO

object FriendIO {
    suspend fun post(userId: Int, dto: FriendDTO.Post): FriendVO {
//        val result: HttpResponse = httpClient.post("/friend") {
//            parameter("userId", userId)
//            setBody(Gson().toJsonTree(dto))
//        }

//        when (result.status) {
//            HttpStatusCode.Conflict -> { LocalLogger.v("friend post -> conflict -> duplicate") }
//            HttpStatusCode.BadRequest -> { LocalLogger.v("friend post -> badRequest -> self request")}
//
//        }

//        return result
        return httpClient
            .post("/friend") {
                parameter("userId", userId)
                setBody(Gson().toJsonTree(dto))
            }.body()
    }

    suspend fun put(userId: Int, dto: FriendDTO.Put) {
        httpClient
            .put("/friend") {
                parameter("userId", userId)
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun delete(userId: Int, opponentId: Int) {
        httpClient
            .delete("/friend/${opponentId}") { parameter("userId", userId) }
            .bodyAsText()
    }

    suspend fun getByUserIdAndOpponentId(userId: Int, opponentId: Int): FriendVO {
        return httpClient
            .get("/friend/${userId}/${opponentId}")
            .body()
    }

    suspend fun listByUserId(userId: Int): List<FriendVO> {
        return httpClient
            .get("/friend/list/all") { parameter("userId", userId) }
            .body()
    }

    suspend fun listByUserIdAndLocationId(userId: Int, locationId: Int): List<FriendVO> {
        return httpClient
            .get("/friend/list") {
                parameter("userId", userId)
                parameter("locationId", locationId)
            }.body()
    }

    suspend fun addedListByUserId(userId: Int): List<FriendVO> {
        return httpClient
            .get("/report/${userId}/friend/list")
            .body()
    }
}