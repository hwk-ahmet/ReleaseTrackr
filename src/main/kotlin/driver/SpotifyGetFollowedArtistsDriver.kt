package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.domain.internal.Artist
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Component
class SpotifyGetFollowedArtistsDriver {

    fun getAllFollowedArtists(authToken: String): List<Artist> {
        var allArtists: MutableList<Artist>
        var after: String? = null

        do {
            val spotifyResult = fetchFollowedArtists(authToken, after)
            allArtists = spotifyResult.artists.items.map { artist ->
                Artist(
                    id = artist.id,
                    name = artist.name,
                    imageUrl = artist.images
                        .sortedBy { it.height * it.width }
                        .let { sortedImages -> sortedImages.getOrNull(1) ?: sortedImages.firstOrNull() }
                        ?.url.orEmpty()
                )
            }.toMutableList()
            after = spotifyResult.artists.cursors.after
        } while (!after.isNullOrBlank())

        return allArtists
    }

    private fun fetchFollowedArtists(authToken: String, after: String?): SpotifyFollowedArtistsResult {
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
                .bodyToMono(object : ParameterizedTypeReference<SpotifyFollowedArtistsResult>() {})
                .block()
                ?: SpotifyFollowedArtistsResult.empty()
        } catch (e: WebClientResponseException) {
            logger.error("Spotify API responded with an error: ${e.statusCode} - ${e.responseBodyAsString}")
            SpotifyFollowedArtistsResult.empty()
        } catch (e: WebClientRequestException) {
            logger.error("Request to Spotify API failed: ${e.message}")
            SpotifyFollowedArtistsResult.empty()
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: ${e.message}")
            SpotifyFollowedArtistsResult.empty()
        }
    }

    data class SpotifyFollowedArtistsResult(
        val artists: SpotifyArtistList
    ) {
        data class SpotifyArtistList(
            val items: List<SpotifyArtist>,
            val cursors: Cursor
        )

        data class Cursor(
            val after: String?
        )

        data class SpotifyArtist(
            var id: String,
            var name: String,
            var images: List<SpotifyImage> = emptyList()
        ) {

            data class SpotifyImage(
                var url: String,
                var height: Int,
                var width: Int
            )

        }

        companion object {
            fun empty() = SpotifyFollowedArtistsResult(
                artists = SpotifyArtistList(
                    items = emptyList(),
                    cursors = Cursor(null)
                )
            )
        }
    }


    private companion object : KLogging()
}
