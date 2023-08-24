package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText

object LocationFriendIO {
    suspend fun post(locationId: Int, opponentId: Int) {
        httpClient
            .post("/location/${locationId}/friend/${opponentId}")
            .bodyAsText()
    }

    suspend fun delete(locationId: Int, opponentId: Int) {
        httpClient
            .delete("/location/${locationId}/friend/${opponentId}")
            .bodyAsText()
    }
}