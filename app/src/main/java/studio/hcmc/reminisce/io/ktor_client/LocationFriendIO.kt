package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO

object LocationFriendIO {
    suspend fun post(locationId: Int, opponentId: Int): LocationFriendVO {
        return httpClient
            .post("/location/${locationId}/friend/${opponentId}")
            .body()
    }

    suspend fun delete(locationId: Int, opponentId: Int) {
        httpClient
            .delete("/location/${locationId}/friend/${opponentId}")
            .bodyAsText()
    }

    suspend fun listByUserId(userId: Int): List<LocationFriendVO> {
        return httpClient
            .get("/location/friend/list/all") { parameter("userId", userId) }
            .body()
    }
}
