package org.releasetrackr.service

import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.releasetrackr.driver.SpotifySearchArtistsDriver
import org.springframework.stereotype.Service

@Service
class SpotifyService(private val spotifySearchArtistsDriver: SpotifySearchArtistsDriver) {

    fun searchArtists(query: String): ArtistsSearchResult {
        return spotifySearchArtistsDriver.searchSpotify(query)
    }
}