package org.releasetrackr.service

import org.releasetrackr.driver.SpotifyUserAuthorizationDriver
import org.springframework.stereotype.Service

@Service
class SpotifyAuthHandlerService(
    private val spotifyUserAuthorizationDriver: SpotifyUserAuthorizationDriver
) {

    fun exchangeAccessCode(authCode: String): String {
        return spotifyUserAuthorizationDriver.getAccessToken(authCode)
    }
}