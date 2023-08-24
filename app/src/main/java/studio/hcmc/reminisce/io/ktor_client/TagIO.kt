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
import studio.hcmc.reminisce.vo.tag.TagVO

object TagIO {
    suspend fun post(dto: TagDTO.Post) {
        httpClient
            .post("/user/tag") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun delete(tagId: Int) {
        httpClient
            .delete("/user/tag/${tagId}")
            .bodyAsText()
    }

    suspend fun listByUserId(userId: Int): List<TagVO> {
        return httpClient
            .get("/user/tag/list/all") {
                parameter("userId", userId)
            }.body()
    }
}