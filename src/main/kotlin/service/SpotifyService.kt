package org.releasetrackr.service

import org.releasetrackr.domain.external.SpotifySearchResult
import org.releasetrackr.driver.SpotifySearchArtistsDriver
import org.springframework.stereotype.Service

@Service
class SpotifyService(private val spotifySearchArtistsDriver: SpotifySearchArtistsDriver) {

    fun searchArtists(query: String): SpotifySearchResult? {
        return spotifySearchArtistsDriver.searchSpotify(query)
    }
}