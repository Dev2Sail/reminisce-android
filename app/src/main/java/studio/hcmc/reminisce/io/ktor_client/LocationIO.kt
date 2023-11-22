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

    // location 내용 전체 수정
    suspend fun put(locationId: Int, dto: LocationDTO.Put) {
        httpClient
            .put("/location/${locationId}") { setBody(Gson().toJsonTree(dto)) }
            .bodyAsText()
    }

    // location에 저장된 categoryId 수정
    suspend fun patchCategoryId(locationId: Int, categoryId: Int) {
        httpClient
            .patch("/location/${locationId}") { parameter("categoryId", categoryId) }
            .bodyAsText()
    }

    suspend fun delete(locationId: Int) {
        httpClient
            .delete("/location/${locationId}")
            .bodyAsText()
    }

    // 단일 location
    suspend fun getById(locationId: Int): LocationVO {
        return httpClient
            .get("/location/${locationId}")
            .body()
    }

    // userId로 저장된 모든 location list
    suspend fun listByUserId(userId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/all") {
                parameter("userId", userId)
            }.body()
    }

    // userId의 category별로 저장된 location list
    suspend fun listByCategoryId(categoryId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("categoryId", categoryId)
            }.body()
    }

    suspend fun listByTagId(tagId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") { parameter("tagId", tagId) }
            .body()
    }

    // userId로 등록된 location 중 opponentId가 등록된 location list
    suspend fun listByUserIdAndOpponentId(userId: Int, opponentId: Int): List<LocationVO> {
        return httpClient
            .get("/location/friend/list") {
                parameter("userId", userId)
                parameter("opponentId", opponentId)
            }.body()
    }

    // userId로 등록된 location 중 tagId가 등록된 location list
    suspend fun listByUserIdAndTagId(userId: Int, tagId: Int): List<LocationVO> {
        return httpClient
            .get("/location/tag/list") {
                parameter("userId", userId)
                parameter("tagId", tagId)
            }.body()
    }

    // userId에 저장된 location 중 동일 title로 저장된 location list
    suspend fun listByUserIdAndTitle(userId: Int, title: String): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("userId", userId)
                parameter("title", title)
            }.body()
    }

    // userId에 저장된 location 중 '해수욕장'이 포함된 location list
    suspend fun beachListByUserId(userId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/beach") { parameter("userId", userId) }
            .body()
    }

    // userId에 저장된 location 중 '휴게소'가 포함된 location list
    suspend fun serviceAreaListByUserId(userId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/service") { parameter("userId", userId) }
            .body()
    }

    // userId에 저장된 location 중 visitedAt 기준 1년 전 오늘 저장된 location list
    suspend fun yearAgoTodayByUserIdAndDate(userId: Int, today: String): List<LocationVO> {
        return httpClient
            .get("/location/today") {
                parameter("userId", userId)
                parameter("yearAgoToday", today)
            }.body()
    }
}