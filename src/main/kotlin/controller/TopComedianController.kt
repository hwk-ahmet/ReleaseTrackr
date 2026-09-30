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
@RequestMapping("/api/comedians")
class TopComedianController(
    private val spotifyClientCredentialsDriver: SpotifyClientCredentialsDriver
) {

    private val webClient = WebClient.builder().build()

    @GetMapping("/top-followed")
    fun getTopFollowedComedians(
        @RequestParam(name = "limit", defaultValue = "100") limit: Int
    ): List<TopComedian> {
        val cappedLimit = limit.coerceIn(1, 100)
        val accessToken = spotifyClientCredentialsDriver.getAccessToken()

        val queries = listOf(
            "genre:comedy",
            "stand-up comedian",
            "stand-up comedy",
            "comedy special"
        )

        val candidatesById = linkedMapOf<String, SpotifySearchResponse.SpotifyArtist>()

        queries.forEach { query ->
            for (offset in 0..200 step 50) {
                val response = searchArtists(query, offset, accessToken)
                response?.artists?.items.orEmpty().forEach { artist ->
                    candidatesById.putIfAbsent(artist.id, artist)
                }
            }
        }

        return candidatesById.values
            .filter { isComedianOnly(it.genres) }
            .sortedByDescending { it.followers?.total ?: 0 }
            .take(cappedLimit)
            .map {
                TopComedian(
                    spotifyArtistId = it.id,
                    name = it.name,
                    imageUrl = selectMediumImageUrl(it.images),
                    followers = it.followers?.total ?: 0,
                    genres = it.genres
                )
            }
    }

    private fun searchArtists(query: String, offset: Int, accessToken: String): SpotifySearchResponse? {
        val uri = UriComponentsBuilder
            .fromHttpUrl("https://api.spotify.com/v1/search")
            .queryParam("q", query)
            .queryParam("type", "artist")
            .queryParam("limit", 50)
            .queryParam("offset", offset)
            .build()
            .toUri()

        return try {
            webClient.get()
                .uri(uri)
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<SpotifySearchResponse>() {})
                .block()
        } catch (_: Exception) {
            null
        }
    }

    private fun isComedianOnly(genres: List<String>): Boolean {
        if (genres.isEmpty()) {
            return false
        }

        val genreText = genres.joinToString(" ").lowercase()
        val hasComedySignal = listOf("comedy", "stand-up", "comic", "sketch").any { genreText.contains(it) }
        val hasMusicSignal = listOf(
            "pop", "rock", "hip hop", "rap", "metal", "house", "techno", "edm", "country", "indie", "r&b", "jazz"
        ).any { genreText.contains(it) }

        return hasComedySignal && !hasMusicSignal
    }

    private fun selectMediumImageUrl(images: List<SpotifyImage>): String {
        if (images.isEmpty()) {
            return ""
        }
        return images.getOrNull(1)?.url ?: images.first().url
    }

    data class TopComedian(
        val spotifyArtistId: String,
        val name: String,
        val imageUrl: String,
        val followers: Int,
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
            val followers: SpotifyFollowers? = null,
            val images: List<SpotifyImage> = emptyList()
        )
    }

    data class SpotifyFollowers(
        val total: Int
    )

    data class SpotifyImage(
        val url: String,
        val height: Int? = null,
        val width: Int? = null
    )
}
