package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import studio.hcmc.reminisce.BuildConfig
import studio.hcmc.reminisce.io.kakao.KAResponse
import studio.hcmc.reminisce.io.kakao.KCTAResponse
import studio.hcmc.reminisce.io.kakao.KCTRResponse
import studio.hcmc.reminisce.io.kakao.KKResponse

object KakaoIO {
    suspend fun listByKeyword(query: String): KKResponse {
        return httpClient
            .get("https://dapi.kakao.com/v2/local/search/keyword.json") {
                header("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                parameter("query", query)
            }.body()
    }

    suspend fun listByAddress(query: String): KAResponse {
        return httpClient
            .get("https://dapi.kakao.com/v2/local/search/address.json") {
                header("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                parameter("query", query)
            }.body()
    }

    suspend fun getRegionCodeByCoord(longitude: String, latitude: String): KCTRResponse {
        return httpClient
            .get("https://dapi.kakao.com/v2/local/geo/coord2regioncode.json") {
                header("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                parameter("x", longitude)
                parameter("y", latitude)
            }.body()
    }

    suspend fun getAddressByCoord(longitude: String, latitude: String): KCTAResponse {
        return httpClient
            .get("https://dapi.kakao.com/v2/local/geo/coord2address.json") {
                header("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                parameter("x", longitude)
                parameter("y", latitude)
            }.body()
    }
}