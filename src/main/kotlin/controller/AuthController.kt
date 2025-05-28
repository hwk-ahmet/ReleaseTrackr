package org.releasetrackr.controller

import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.releasetrackr.config.SpotifyConfiguration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/auth")
class AuthController(private val spotifyConfiguration: SpotifyConfiguration) {

    @GetMapping("/login")
    fun login(response: HttpServletResponse) {
        val state = generateRandomString()
        val scope = "user-read-private user-read-email"

        val spotifyAuthUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", spotifyConfiguration.clientId)
            .queryParam("scope", scope)
            .queryParam("redirect_uri", "http://127.0.0.1:8080/auth/callback")
            .queryParam("state", state)
            .build()
            .toUriString()

        response.sendRedirect(spotifyAuthUrl)
    }

    // TODO: This below method should be integrated in the existing "getAccessTokenDriver" with a switch. otherwise duplicate code.

    @GetMapping("/callback")
    fun callback(@RequestParam("code") code: String, response: HttpServletResponse): String {
        val tokenResponse = WebClient.create()
            .post()
            .uri("https://accounts.spotify.com/api/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "grant_type=authorization_code&" +
                        "code=$code&" +
                        "redirect_uri=http://127.0.0.1:8080/auth/callback&" +
                        "client_id=${spotifyConfiguration.clientId}&" +
                        "client_secret=${spotifyConfiguration.clientSecret}"
            )
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return "Token response: $tokenResponse"
    }


    private fun generateRandomString(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..16)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private companion object : KLogging()
}
