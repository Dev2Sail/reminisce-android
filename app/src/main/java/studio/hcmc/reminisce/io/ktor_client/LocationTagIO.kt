package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.tag.TagDTO
import studio.hcmc.reminisce.vo.location.LocationTagVO

object LocationTagIO {
    suspend fun post(locationId: Int, dto: TagDTO.Post): LocationTagVO {
        return httpClient
            .post("/location/${locationId}/tag") { setBody(Gson().toJsonTree(dto)) }
            .body()
    }

    suspend fun delete(locationId: Int, tagId: Int) {
        httpClient
            .delete("/location/${locationId}/tag/${tagId}")
            .bodyAsText()
    }

    suspend fun listByUserId(userId: Int): List<LocationTagVO> {
        return httpClient
            .get("location/tag/list/all") { parameter("userId", userId) }
            .body()
    }
}