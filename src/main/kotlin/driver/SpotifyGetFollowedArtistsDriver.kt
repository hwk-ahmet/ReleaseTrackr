package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.domain.external.SpotifyArtist
import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Component
class SpotifyGetFollowedArtistsDriver {

    fun getAllFollowedArtists(authToken: String): ArtistsSearchResult {
        val allArtists = mutableListOf<ArtistsSearchResult.Artist>()
        var after: String? = null

        do {
            val spotifyResult = fetchFollowedArtists(authToken, after)
            allArtists += spotifyResult.artists.items.map { artist ->
                ArtistsSearchResult.Artist(
                    id = artist.id,
                    name = artist.name,
                    imageUrl = artist.images
                        .sortedBy { it.height * it.width }
                        .let { sortedImages -> sortedImages.getOrNull(1) ?: sortedImages.firstOrNull() }
                        ?.url.orEmpty()
                )
            }
            after = spotifyResult.artists.cursors.after
        } while (!after.isNullOrBlank())

        return ArtistsSearchResult(artists = allArtists)
    }

    private fun fetchFollowedArtists(authToken: String, after: String?): SpotifySearchResult {
        return try {
            val uri = UriComponentsBuilder
                .fromHttpUrl("https://api.spotify.com/v1/me/following")
                .queryParam("type", "artist")
                .queryParam("limit", 50)
                .apply {
                    if (!after.isNullOrBlank()) {
                        queryParam("after", after)
                    }
                }
                .build()
                .toUri()

            WebClient.builder().build().get()
                .uri(uri)
                .headers { it.setBearerAuth(authToken) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<SpotifySearchResult>() {})
                .block()
                ?: SpotifySearchResult.empty()
        } catch (e: WebClientResponseException) {
            logger.error("Spotify API responded with an error: ${e.statusCode} - ${e.responseBodyAsString}")
            SpotifySearchResult.empty()
        } catch (e: WebClientRequestException) {
            logger.error("Request to Spotify API failed: ${e.message}")
            SpotifySearchResult.empty()
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: ${e.message}")
            SpotifySearchResult.empty()
        }
    }

    data class SpotifySearchResult(
        val artists: ArtistList
    ) {
        data class ArtistList(
            val items: List<SpotifyArtist>,
            val cursors: Cursor
        )

        data class Cursor(
            val after: String?
        )

        companion object {
            fun empty() = SpotifySearchResult(
                artists = ArtistList(
                    items = emptyList(),
                    cursors = Cursor(null)
                )
            )
        }
    }

    private companion object : KLogging()
}
