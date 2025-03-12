package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.domain.external.SpotifyGetArtistsResult
import org.releasetrackr.domain.external.SpotifySearchResult
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class SpotifyGetArtistsByIdDriver(private val spotifyGetAccessTokenDriver: SpotifyGetAccessTokenDriver) {

    fun getByIds(query: List<String>): SpotifyGetArtistsResult? {
        val accessToken = spotifyGetAccessTokenDriver.getAccessToken()

        return try {
            WebClient.builder().build().get()
                .uri("https://api.spotify.com/v1/artists?ids=${query.joinToString(",")}")
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<SpotifyGetArtistsResult>() {})
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
