package org.releasetrackr.driver

import mu.KLogging
import org.releasetrackr.domain.external.SpotifyArtist
import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class SpotifyGetFollowedArtistsDriver {

    fun getFollowedArtists(authToken: String): ArtistsSearchResult {

        return try {
            val response = WebClient.builder().build().get()

                .uri("https://api.spotify.com/v1/me/following?type=artist&limit=50")
                .headers { it.setBearerAuth(authToken) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<SpotifySearchResult>() {})
                .block()

            response?.toDto() ?: ArtistsSearchResult.empty() // Handle null safely
        } catch (e: WebClientResponseException) {
            logger.error("Spotify API responded with an error: ${e.statusCode} - ${e.responseBodyAsString}")
            ArtistsSearchResult()
        } catch (e: WebClientRequestException) {
            logger.error("Request to Spotify API failed: ${e.message}")
            ArtistsSearchResult()
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: ${e.message}")
            ArtistsSearchResult()
        }
    }

    data class SpotifySearchResult(
        var artists: ArtistList = ArtistList(emptyList())
    ) {

        data class ArtistList(
            var items: List<SpotifyArtist>
        )
    }

    fun SpotifySearchResult.toDto(): ArtistsSearchResult = ArtistsSearchResult(
        artists = this.artists.items.map { artist ->
            ArtistsSearchResult.Artist(
                id = artist.id,
                name = artist.name,
                imageUrl = artist.images  // get second-smallest image if exists, get smallest otherwise
                    .sortedBy { it.height * it.width }
                    .let { sortedImages -> sortedImages.getOrNull(1) ?: sortedImages.firstOrNull() }
                    ?.url.toString()
            )
        }
    )

    private companion object : KLogging()
}
