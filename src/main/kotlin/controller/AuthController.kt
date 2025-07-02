package org.releasetrackr.controller

import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.releasetrackr.config.SpotifyConfiguration
import org.releasetrackr.domain.internal.WatchList
import org.releasetrackr.service.SpotifyAuthHandlerService
import org.releasetrackr.service.WatchListService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val spotifyConfiguration: SpotifyConfiguration,
    private val watchListService: WatchListService,
    private val spotifyAuthHandlerService: SpotifyAuthHandlerService
) {

    @GetMapping("/login")
    fun login(response: HttpServletResponse) {
        val authRedirectUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", spotifyConfiguration.clientId)
            .queryParam("scope", SCOPES)
            .queryParam("redirect_uri", "http://127.0.0.1:8080/auth/callback")
            .queryParam("state", UUID.randomUUID())
            .build()
            .toUriString()
        response.sendRedirect(authRedirectUrl)
    }

    @GetMapping("/callback")
    fun callback(@RequestParam("code") code: String): WatchList {
        val accessToken = spotifyAuthHandlerService.exchangeAccessCode(code)
        // return spotifySearchService.searchArtists("pryor")
        return watchListService.getWatchList(accessToken) ?: throw RuntimeException("Could not generate watchlist")
    }

    private companion object : KLogging() {
        const val SCOPES = "user-follow-read" // ALL required app scopes
    }
}
