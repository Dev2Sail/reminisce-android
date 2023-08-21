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
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO

object FriendIO {
    suspend fun post(userId: Int, dto: FriendDTO.Post) {
        httpClient.post("/user/${userId}/friend") {
            setBody(Gson().toJsonTree(dto))
        }.bodyAsText()
    }

    suspend fun put(userId: Int, opponentId: Int, dto: FriendDTO.Put) {
        httpClient.put("/user/${userId}/friend/${opponentId}") {
            setBody(Gson().toJsonTree(dto))
        }.bodyAsText()
    }

    suspend fun delete(userId: Int, opponentId: Int) {
        httpClient.delete("/user/${userId}/friend/${opponentId}").bodyAsText()
    }

    suspend fun listByUserId(userId: Int): List<FriendVO> {
        return httpClient.get("/user/${userId}/friend/list/all").body()
    }

    suspend fun mostAddedOpponentIdByUserId(userId: Int): List<LocationFriendVO> {
        return httpClient.get("/location/friend/list") {
            parameter("userId", userId)
        }.body()
    }
}