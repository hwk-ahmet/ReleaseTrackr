package org.releasetrackr.driver

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.releasetrackr.domain.internal.Album
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Component
class SpotifyGetArtistAlbumsDriver {

    suspend fun getAlbumsForArtists(authToken: String, artistIds: List<String>): List<Album> = coroutineScope {
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

                    response?.items?.map { it.toDomainAlbum() } ?: emptyList()
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
    ) {
        data class SpotifyAlbum(
            val name: String,
            val release_date: String,
            var release_date_precision: String,
            val images: List<SpotifyImage>,
            val artists: List<SpotifyArtist>,
            val external_urls: Map<String, String>?
        ) {
            data class SpotifyImage(
                val url: String,
                val height: Int,
                val width: Int
            )

            data class SpotifyArtist(
                val name: String
            )
        }
    }

    private fun SpotifyAlbumsResponse.SpotifyAlbum.toDomainAlbum(): Album {
        return Album(
            releaseDate = this.release_date,
            albumName = this.name,
            artistName = this.artists[0].name,
            imageUrl = this.images
                .sortedBy { it.height * it.width }
                .let { sortedImages ->
                    sortedImages.getOrNull(1) ?: sortedImages.firstOrNull()
                } // get second-smallest-image
                ?.url.orEmpty(),
            albumUrl = this.external_urls?.get("spotify") ?: "" // album-url always has key "spotify"
        )
    }

    private companion object : KLogging()
}
