package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.vo.tag.TagVO

object TagIO {
    suspend fun delete(tagId: Int) {
        httpClient
            .delete("/user/tag/${tagId}")
            .bodyAsText()
    }

    suspend fun getByUserIdAndTagId(userId: Int, tagId: Int): TagVO {
        return httpClient
            .get("/tag/${tagId}") { parameter("userId", userId) }
            .body()
    }

    suspend fun listByUserId(userId: Int): List<TagVO> {
        return httpClient
            .get("/tag/list/all") { parameter("userId", userId) }
            .body()
    }

    suspend fun listByLocationId(locationId: Int): List<TagVO> {
        return httpClient
            .get("/tag/list") { parameter("locationId", locationId) }
            .body()
    }
}