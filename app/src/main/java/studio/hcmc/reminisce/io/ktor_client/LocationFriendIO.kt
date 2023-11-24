package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.location.LocationFriendDTO
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO

object LocationFriendIO {
    suspend fun post(dto: LocationFriendDTO.Post): List<LocationFriendVO> {
        return httpClient
            .post("/location/options/friend") { setBody(Gson().toJsonTree(dto)) }
            .body()
    }

    suspend fun delete(locationId: Int, opponentId: Int) {
        httpClient
            .delete("/location/options/friend/${opponentId}") { parameter("locationId", locationId) }
            .bodyAsText()
    }
}
