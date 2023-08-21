package studio.hcmc.reminisce.io.ktor_client

import com.google.gson.Gson
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import studio.hcmc.reminisce.dto.location.LocationDTO

object LocationIO {
    suspend fun post(dto: LocationDTO.Post) {
        httpClient.post("/location") {
            setBody(Gson().toJsonTree(dto))
        }.bodyAsText()
    }

}