package org.releasetrackr.driver

import org.releasetrackr.config.SpotifyConfiguration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.lang.IllegalStateException

@Component
class SpotifyUserAuthorizationDriver(private val spotifyConfiguration: SpotifyConfiguration) {

    fun getAccessToken(authCode: String = ""): String {
        val body = when {
            authCode.isNotBlank() ->
                "grant_type=authorization_code&" +
                        "code=$authCode&" +
                        "redirect_uri=http://127.0.0.1:8080/auth/callback"

            else ->
                "grant_type=client_credentials"
        }

        val tokenResponse = WebClient.create()
            .post()
            .uri("https://accounts.spotify.com/api/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(body)
            .headers {
                it.setBasicAuth(spotifyConfiguration.clientId, spotifyConfiguration.clientSecret)
            }
            .retrieve()
            .bodyToMono(SpotifyTokenResponse::class.java)
            .block()
        return tokenResponse?.access_token
            ?: throw IllegalStateException("Could retrieve access token")
    }
}

data class SpotifyTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String = "",
    val scope: String = ""
)