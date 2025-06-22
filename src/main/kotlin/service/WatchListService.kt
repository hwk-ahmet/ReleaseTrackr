package org.releasetrackr.service

import org.releasetrackr.domain.internal.ArtistsSearchResult
import org.releasetrackr.driver.SpotifyGetAccessTokenDriver
import org.releasetrackr.driver.SpotifyGetArtistsByIdDriver
import org.releasetrackr.driver.SpotifyGetFollowedArtistsDriver
import org.springframework.stereotype.Service

@Service
class WatchListService(
    private val spotifyGetArtistsByIdDriver: SpotifyGetArtistsByIdDriver,
    private val spotifyGetFollowedArtistsDriver: SpotifyGetFollowedArtistsDriver,
    private val spotifyGetAccessTokenDriver: SpotifyGetAccessTokenDriver
) {

    fun getWatchList(authCode: String): ArtistsSearchResult? {
        return spotifyGetFollowedArtistsDriver.getFollowedArtists(authCode)
    }
}
