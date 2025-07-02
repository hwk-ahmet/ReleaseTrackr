package org.releasetrackr.service

import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.releasetrackr.driver.SpotifyGetFollowedArtistsDriver
import org.springframework.stereotype.Service

@Service
class WatchListService(
    private val spotifyGetFollowedArtistsDriver: SpotifyGetFollowedArtistsDriver
) {

    fun getWatchList(authCode: String): ArtistsSearchResult? {
        return spotifyGetFollowedArtistsDriver.getFollowedArtists(authCode)
    }
}
