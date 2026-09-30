package org.releasetrackr.controller

import org.releasetrackr.driver.SpotifyClientCredentialsDriver
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/artists")
class ArtistSearchController(
    private val spotifyClientCredentialsDriver: SpotifyClientCredentialsDriver
) {

    @GetMapping("/search")
    fun searchArtists(@RequestParam("q") query: String): List<SearchArtist> {
        if (query.isBlank()) {
            return emptyList()
        }

        val accessToken = spotifyClientCredentialsDriver.getAccessToken()
        val uri = UriComponentsBuilder
            .fromHttpUrl("https://api.spotify.com/v1/search")
            .queryParam("q", query)
            .queryParam("type", "artist")
            .queryParam("limit", 20)
            .build()
            .toUri()

        val result = WebClient.builder().build().get()
            .uri(uri)
            .headers { it.setBearerAuth(accessToken) }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<SpotifySearchResponse>() {})
            .block()

        val mapped = result?.artists?.items?.map { artist ->
            SearchArtist(
                spotifyArtistId = artist.id,
                name = artist.name,
                imageUrl = selectMediumImageUrl(artist.images),
                genres = artist.genres
            )
        } ?: emptyList()

        return mapped.filter { matchesQuery(it.name, query) }
    }

    private fun selectMediumImageUrl(images: List<SpotifySearchResponse.SpotifyImage>): String {
        if (images.isEmpty()) {
            return ""
        }
        return images.getOrNull(1)?.url ?: images.first().url
    }

    private fun matchesQuery(artistName: String, query: String): Boolean {
        val q = query.trim().lowercase()
        if (q.isBlank()) return false

        val queryTokens = q.split(" ").filter { it.isNotBlank() }
        val name = artistName.lowercase()

        return queryTokens.all { token -> name.contains(token) }
    }

    data class SearchArtist(
        val spotifyArtistId: String,
        val name: String,
        val imageUrl: String,
        val genres: List<String>
    )

    data class SpotifySearchResponse(
        val artists: SpotifyArtistList
    ) {
        data class SpotifyArtistList(
            val items: List<SpotifyArtist>
        )

        data class SpotifyArtist(
            val id: String,
            val name: String,
            val genres: List<String> = emptyList(),
            val images: List<SpotifyImage> = emptyList()
        )

        data class SpotifyImage(
            val url: String,
            val height: Int? = null,
            val width: Int? = null
        )
    }
}
