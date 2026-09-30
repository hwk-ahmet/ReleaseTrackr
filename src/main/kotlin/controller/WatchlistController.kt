package org.releasetrackr.controller

import org.releasetrackr.domain.internal.WatchlistArtist
import org.releasetrackr.repository.WatchlistRepository
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Base64

@RestController
@RequestMapping("/api/watchlist")
class WatchlistController(
    private val watchlistRepository: WatchlistRepository
) {

    private val webClient = WebClient.builder()
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs { configurer: ClientCodecConfigurer ->
                    configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)
                }
                .build()
        )
        .build()

    @GetMapping
    fun getWatchlist(): List<WatchlistArtist> {
        return watchlistRepository.findAll().sortedBy { it.name.lowercase() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addToWatchlist(@RequestBody request: AddToWatchlistRequest) {
        val imageBase64 = fetchImageAsBase64DataUri(request.imageUrl)
        watchlistRepository.save(
            WatchlistArtist(
                id = request.spotifyArtistId,
                name = request.name,
                dateAdded = Instant.now(),
                imageUrl = imageBase64
            )
        )
    }

    @DeleteMapping("/{spotifyArtistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFromWatchlist(@PathVariable spotifyArtistId: String) {
        watchlistRepository.deleteById(spotifyArtistId)
    }

    data class AddToWatchlistRequest(
        val spotifyArtistId: String,
        val name: String,
        val imageUrl: String? = null
    )

    private fun fetchImageAsBase64DataUri(imageUrl: String?): String {
        if (imageUrl.isNullOrBlank()) {
            return ""
        }

        return try {
            val response = webClient.get()
                .uri(imageUrl)
                .retrieve()
                .toEntity(ByteArray::class.java)
                .block()

            val bytes = response?.body ?: return ""
            val contentType = response.headers.contentType?.toString() ?: "image/jpeg"
            val base64 = Base64.getEncoder().encodeToString(bytes)
            "data:$contentType;base64,$base64"
        } catch (_: Exception) {
            ""
        }
    }
}
