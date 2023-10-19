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

    suspend fun listByUserId(userId: Int): List<TagVO> {
        return httpClient
            .get("/user/tag/list/all") { parameter("userId", userId) }
            .body()
    }
}