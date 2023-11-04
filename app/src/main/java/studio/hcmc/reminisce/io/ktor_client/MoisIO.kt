package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import studio.hcmc.reminisce.BuildConfig
import studio.hcmc.reminisce.io.mois.MoisResponse

object MoisIO {
    suspend fun transformAddress(address: String): MoisResponse {
        return httpClient
            .get("https://business.juso.go.kr/addrlink/addrLinkApi.do") {
                parameter("confmKey", BuildConfig.MOIS_JUSO_API_KEY)
                parameter("currentPage", 1)
                parameter("countPerPage", 10)
                parameter("keyword", address)
                parameter("resultType", "json")
            }.body()

    }




}