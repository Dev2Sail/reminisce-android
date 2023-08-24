package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.vo.location.LocationVO

object LocationIO {
    suspend fun post(dto: LocationDTO.Post) {
        httpClient
            .post("/location") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
    }

    suspend fun put(locationId: Int, dto: LocationDTO) {
        httpClient
            .put("/location/${locationId}") {
                setBody(Gson().toJsonTree(dto))
            }.bodyAsText()
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

    // user의 모든 location count 조회
    suspend fun getTotalCountByUserId(userId: Int) {
        httpClient
            .get("/location/report")
            .bodyAsText()
    }

    // userId의 category별로 저장된 location 조회
    suspend fun listByCategoryId(categoryId: Int): List<LocationVO> {
        return httpClient
            .get("/location/list") {
                parameter("categoryId", categoryId)
            }.body()
    }

    // userId에 동일 opponentId가 등록된 location 조회
    suspend fun listByUserIdAndOpponentId(userId: Int, opponentId: Int): List<LocationVO> {
        return httpClient
            .get("/location/friend/list") {
                parameter("userId", userId)
                parameter("opponentId", opponentId)
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