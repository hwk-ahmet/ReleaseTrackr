package org.releasetrackr.service

import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.releasetrackr.driver.SpotifySearchArtistsDriver
import org.releasetrackr.driver.SpotifyUserAuthorizationDriver
import org.springframework.stereotype.Service

@Service
class SpotifySearchService(
    private val spotifySearchArtistsDriver: SpotifySearchArtistsDriver,
    private val spotifyUserAuthorizationDriver: SpotifyUserAuthorizationDriver
) {

    fun searchArtists(query: String): ArtistsSearchResult {
        return spotifyUserAuthorizationDriver.getAccessToken().let {
            spotifySearchArtistsDriver.searchSpotify(query, it)
        }
    }
}