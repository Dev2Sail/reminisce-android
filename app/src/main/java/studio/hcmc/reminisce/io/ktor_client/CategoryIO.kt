package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
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
    suspend fun post(dto: CategoryDTO.Post) {
        httpClient
            .post("/user/category") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun put(userId: Int, dto: CategoryDTO.Put) {
        httpClient
            .put("/user/${userId}/category") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun patch(userId: Int, categoryId: Int, dto: CategoryDTO.Patch) {
        httpClient
            .patch("/user/${userId}/category/${categoryId}") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun delete(categoryId: Int) {
        httpClient
            .delete("/user/category/${categoryId}")
            .bodyAsText()
    }

    suspend fun listByUserId(userId: Int): List<CategoryVO> {
        return httpClient
            .get("/user/${userId}/category/list/all")
            .body()
    }

    // userId의 category별로 저장된 location count 조회
    suspend fun getCountByCategoryIdAndUserId(userId: Int, categoryId: Int) {
        httpClient
            .get("/user/${userId}/category") {
                parameter("categoryId", categoryId)
            }.bodyAsText()
    }
}