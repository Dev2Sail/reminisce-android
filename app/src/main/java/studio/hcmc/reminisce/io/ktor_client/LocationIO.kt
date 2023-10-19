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
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.vo.location.LocationVO

object LocationIO {
    suspend fun post(dto: LocationDTO.Post): LocationVO {
        return httpClient
            .post("/location") { setBody(Gson().toJsonTree(dto)) }
            .body()
    }

    suspend fun put(locationId: Int, dto: LocationDTO.Put) {
        httpClient
            .put("/location/${locationId}") { setBody(Gson().toJsonTree(dto)) }
            .bodyAsText()
    }

    suspend fun patch(locationId: Int, categoryId: Int) {
        httpClient
            .patch("/location/${locationId}") { parameter("categoryId", categoryId) }
            .bodyAsText()
    }

    suspend fun delete(locationId: Int) {
        httpClient
            .delete("/location/${locationId}")
            .bodyAsText()
    }

    // 하나의 location 조회
    suspend fun getById(locationId: Int): LocationVO {
        return httpClient
            .get("/location/${locationId}")
            .body()
    }

    // user의 모든 location 조회
    suspend fun listByUserId(userId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/all") {
                parameter("userId", userId)
            }.body()
    }

    // userId의 category별로 저장된 location 조회
    suspend fun listByCategoryId(categoryId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("categoryId", categoryId)
            }.body()
    }

    // tagId로 location 조회
    suspend fun listByTagId(tagId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") { parameter("tagId", tagId) }
            .body()
    }

    // Friend에 존재하는 opponentId가 등록된 location 조회
    suspend fun listByUserIdAndOpponentId(userId: Int, opponentId: Int): List<LocationVO> {
        return httpClient
            .get("/location/friend/list") {
                parameter("userId", userId)
                parameter("opponentId", opponentId)
            }.body()
    }

    // tagId가 등록된 location list 조회
    suspend fun listByUserIdAndTagId(userId: Int, tagId: Int): List<LocationVO> {
        return httpClient
            .get("/location/tag/list") {
                parameter("userId", userId)
                parameter("tagId", tagId)
            }.body()
    }

    // userId에 저장된 location 중 동일 title로 저장된 location 조회
    suspend fun listByUserIdAndTitle(userId: Int, title: String): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("userId", userId)
                parameter("title", title)
            }.body()
    }
}