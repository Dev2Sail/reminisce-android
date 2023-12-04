package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.vo.category.CategoryVO

object CategoryIO {
    suspend fun post(dto: CategoryDTO.Post): CategoryVO {
        return httpClient
            .post("/category") { setBody(Gson().toJsonTree(dto)) }
            .body()
    }

    // category 순서 목록 변경
    suspend fun put(userId: Int, dto: CategoryDTO.Put) {
        httpClient
            .put("/category") {
                parameter("userId", userId)
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    // category title 수정
    suspend fun patch(userId: Int, categoryId: Int, dto: CategoryDTO.Patch) {
        httpClient
            .patch("/category/${categoryId}") {
                parameter("userId", userId)
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun delete(categoryId: Int) {
        httpClient
            .delete("/category/${categoryId}")
            .bodyAsText()
    }

    suspend fun getById(categoryId: Int): CategoryVO {
        return httpClient
            .get("/category/${categoryId}")
            .body()
    }

    // userId의 'Default' categoryId 조회
    suspend fun getDefaultCategoryIdByUserId(userId: Int): CategoryVO {
        return httpClient
            .get("/category") { parameter("userId", userId) }
            .body()
    }

    suspend fun listByUserId(userId: Int, lastId: Int): List<CategoryVO> {
        return httpClient
            .get("/category/list/all") {
                parameter("userId", userId)
                parameter("lastId", lastId)
            }.body()
    }

    suspend fun allByUserId(userId: Int): List<CategoryVO> {
        return httpClient
            .get("/category/list/all") { parameter("userId", userId) }
            .body()
    }

    suspend fun getCountByCategoryIdAndUserId(userId: Int, categoryId: Int): JsonObject {
        return httpClient
            .get("/category/${categoryId}/count") { parameter("userId", userId) }
            .body()
    }

    suspend fun getTotalCountByUserId(userId: Int): JsonObject {
        return httpClient
            .get("/category/all/count") { parameter("userId", userId) }
            .body()
    }
}
