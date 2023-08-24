package studio.hcmc.reminisce.io.ktor_client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson

private val ErrorHandler = createClientPlugin("ErrorHandler", {}) {
    onResponse {
        if (it.status.value !in 200 until 300) {
            throw it.body<SpringException>()
        }
    }
}

val httpClient = HttpClient(CIO) {
    defaultRequest {
        url.host = "10.37.129.2"
        url.port = 8080
        header("Content-type", "application/json")
    }
    expectSuccess = true


    install(ContentNegotiation) {
        gson()
    }

    install(Logging) {
        level = LogLevel.ALL
    }

    install(ErrorHandler)
}