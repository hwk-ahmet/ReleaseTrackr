package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.domain.external.SpotifySearchResult
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class SpotifySearchArtistsDriver(private val spotifyGetAccessTokenDriver: SpotifyGetAccessTokenDriver) {

    fun searchSpotify(query: String): SpotifySearchResult? {
        val accessToken = spotifyGetAccessTokenDriver.getAccessToken()

        return try {
            WebClient.builder().build().get()
                .uri("https://api.spotify.com/v1/search?q=$query&type=artist&limit=10")
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<SpotifySearchResult>() {})
                .block()
        } catch (e: WebClientResponseException) {
            logger.error("Spotify API responded with an error: ${e.statusCode} - ${e.responseBodyAsString}")
            null
        } catch (e: WebClientRequestException) {
            logger.error("Request to Spotify API failed: ${e.message}")
            null
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: ${e.message}")
            null
        }
    }

    private companion object : KLogging()
}
