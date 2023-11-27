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

    // 단일 location
    suspend fun getById(locationId: Int): LocationVO {
        return httpClient
            .get("/location/${locationId}")
            .body()
    }

    suspend fun listByUserId(userId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/all") {
                parameter("userId", userId)
                parameter("lastId", lastId)
            }.body()
    }

    suspend fun listByCategoryId(categoryId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("categoryId", categoryId)
                parameter("lastId", lastId)
            }.body()
    }

    suspend fun listByTagId(tagId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("tagId", tagId)
                parameter("lastId", lastId)
            }.body()
    }

    // userId로 등록된 location 중 opponentId가 등록된 location list
    suspend fun listByUserIdAndOpponentId(userId: Int, opponentId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/friend/list") {
                parameter("userId", userId)
                parameter("opponentId", opponentId)
                parameter("lastId", lastId)
            }.body()
    }

    // userId에 저장된 location 중 동일 title로 저장된 location list
    suspend fun listByUserIdAndTitle(userId: Int, title: String, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("userId", userId)
                parameter("title", title)
                parameter("lastId", lastId)
            }.body()
    }

    // userId에 저장된 location 중 '해수욕장'이 포함된 location list
    suspend fun beachListByUserId(userId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/beach") {
                parameter("userId", userId)
                parameter("lastId", lastId)
            }.body()
    }

    // userId에 저장된 location 중 '휴게소'가 포함된 location list
    suspend fun serviceAreaListByUserId(userId: Int, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/service") {
                parameter("userId", userId)
                parameter("lastId", lastId)
            }.body()
    }

    // userId에 저장된 location 중 visitedAt 기준 1년 전 오늘 저장된 location list
    suspend fun yearAgoTodayByUserIdAndDate(userId: Int, date: String, lastId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list/today") {
                parameter("userId", userId)
                parameter("date", date)
                parameter("lastId", lastId)
            }.body()
    }
}