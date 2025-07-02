package org.releasetrackr.driver

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.releasetrackr.domain.external.SpotifyAlbum
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Component
class SpotifyGetArtistAlbumsDriver {

    suspend fun getAlbumsForArtists(authToken: String, artistIds: List<String>): List<SpotifyAlbum> = coroutineScope {
        val client = WebClient.builder().build()

        artistIds.map { artistId ->
            async {
                try {
                    val uri = UriComponentsBuilder
                        .fromHttpUrl("https://api.spotify.com/v1/artists/$artistId/albums")
                        .queryParam("market", "DE")
                        .queryParam("limit", 50)
                        .build()
                        .toUri()

                    val response = client.get()
                        .uri(uri)
                        .headers { it.setBearerAuth(authToken) }
                        .retrieve()
                        .bodyToMono(object : ParameterizedTypeReference<SpotifyAlbumsResponse>() {})
                        .block()

                    response?.items ?: emptyList()
                } catch (e: WebClientResponseException) {
                    logger.error("Spotify API error: ${e.statusCode} - ${e.responseBodyAsString}")
                    emptyList()
                } catch (e: WebClientRequestException) {
                    logger.error("Request to Spotify API failed: ${e.message}")
                    emptyList()
                } catch (e: Exception) {
                    logger.error("Unexpected error for artist $artistId: ${e.message}")
                    emptyList()
                }
            }
        }.flatMap { it.await() }
    }

    data class SpotifyAlbumsResponse(
        val items: List<SpotifyAlbum>
    )

    private companion object : KLogging()
}
