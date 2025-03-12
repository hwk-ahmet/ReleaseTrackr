package org.releasetrackr.driver

import org.releasetrackr.config.SpotifyConfiguration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SpotifyGetAccessTokenDriver(private val spotifyConfiguration: SpotifyConfiguration) {

    private val webClient = WebClient.builder().build()

    fun getAccessToken(): String {
        val response = webClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("grant_type=client_credentials")
            .headers {
                it.setBasicAuth(clientId, clientSecret)
            }
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        return response?.get("access_token") as String
    }

    companion object {
        private lateinit var clientId: String
        private lateinit var clientSecret: String
        private lateinit var tokenUrl: String
        private lateinit var apiUrl: String

        fun initialize(spotifyConfiguration: SpotifyConfiguration) {
            clientId = spotifyConfiguration.clientId
            clientSecret = spotifyConfiguration.clientSecret
            tokenUrl = spotifyConfiguration.accessTokenUrl
        }
    }

    init {
        initialize(spotifyConfiguration)
    }
}
