package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson

val httpClient = HttpClient(CIO) {
    defaultRequest {
        url.host = "10.37.129.2"
        url.port = 8080
        header("Content-type", "application/json")
    }

    install(ContentNegotiation) {
        gson()
    }

    install(Logging) {
        level = LogLevel.ALL
    }

}