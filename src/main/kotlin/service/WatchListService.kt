package org.releasetrackr.service

import kotlinx.coroutines.runBlocking
import org.releasetrackr.domain.internal.WatchList
import org.releasetrackr.driver.SpotifyGetArtistAlbumsDriver
import org.releasetrackr.driver.SpotifyGetFollowedArtistsDriver
import org.springframework.stereotype.Service

@Service
class WatchListService(
    private val spotifyGetFollowedArtistsDriver: SpotifyGetFollowedArtistsDriver,
    private val spotifyGetArtistAlbumsDriver: SpotifyGetArtistAlbumsDriver
) {

    fun getWatchList(authCode: String): WatchList = runBlocking {
        val followedArtists = spotifyGetFollowedArtistsDriver.getAllFollowedArtists(authCode)
        val artistIds = followedArtists.artists.map { it.id }

        val allAlbums = spotifyGetArtistAlbumsDriver.getAlbumsForArtists(authCode, artistIds)

        val albums = allAlbums.map {
            WatchList.Album(
                releaseDate = it.release_date,
                albumName = it.name,
                artistName = it.artists.firstOrNull()?.name.orEmpty(),
                imageUrl = it.images
                    .sortedBy { img -> img.height * img.width }
                    .firstOrNull()?.url.orEmpty(),
                albumUrl = it.external_urls?.get("spotify").orEmpty()
            )
        }

        WatchList(albums = albums)
    }
}
