package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText

object LocationTagIO {
    suspend fun post(locationId: Int, tagId: Int) {
        httpClient
            .post("/location/${locationId}/tag/${tagId}")
            .bodyAsText()
    }

    suspend fun delete(locationId: Int, tagId: Int) {
        httpClient
            .delete("/location/${locationId}/tag/${tagId}")
            .bodyAsText()
    }
}