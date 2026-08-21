package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.config.SpotifyConfiguration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

@Component
class SpotifyClientCredentialsDriver(private val spotifyConfiguration: SpotifyConfiguration) {

    private var cachedToken: String? = null
    private var tokenExpiry: Instant? = null

    fun getAccessToken(): String {
        if (cachedToken != null && tokenExpiry != null && tokenExpiry!! > Instant.now()) {
            return cachedToken!!
        }

        val body = "grant_type=client_credentials"

        val tokenResponse = WebClient.create()
            .post()
            .uri("https://accounts.spotify.com/api/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(body)
            .headers {
                it.setBasicAuth(spotifyConfiguration.clientId, spotifyConfiguration.clientSecret)
            }
            .retrieve()
            .bodyToMono(SpotifyClientCredentialsResponse::class.java)
            .block()

        cachedToken = tokenResponse?.access_token
            ?: throw IllegalStateException("Could not retrieve access token")
        tokenExpiry = Instant.now().plusSeconds(tokenResponse?.expires_in?.toLong() ?: 3600)

        return cachedToken!!
    }
}

data class SpotifyClientCredentialsResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

private companion object : KLogging()
