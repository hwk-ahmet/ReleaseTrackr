package org.releasetrackr.service

import org.releasetrackr.domain.internal.WatchList
import org.releasetrackr.driver.SpotifyGetArtistAlbumsDriver
import org.releasetrackr.driver.SpotifyGetFollowedArtistsDriver
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class WatchListService(
    private val spotifyGetFollowedArtistsDriver: SpotifyGetFollowedArtistsDriver,
    private val spotifyGetArtistAlbumsDriver: SpotifyGetArtistAlbumsDriver
) {

    suspend fun getWatchList(authCode: String): WatchList {
        val followedArtists = spotifyGetFollowedArtistsDriver.getAllFollowedArtists(authCode)
        val albums = spotifyGetArtistAlbumsDriver.getAlbumsForArtists(authCode, followedArtists.map { it.id })

        val sortedAlbums = albums.sortedByDescending { album ->
            parseReleaseDate(album.release_date)
        }

        return WatchList(sortedAlbums)
    }

    private fun parseReleaseDate(releaseDate: String): LocalDate {
        return when {
            releaseDate.matches(Regex("""\d{4}-\d{2}-\d{2}""")) -> // YYYY-MM-DD
                LocalDate.parse(releaseDate, DateTimeFormatter.ISO_LOCAL_DATE)

            releaseDate.matches(Regex("""\d{4}-\d{2}""")) -> // YYYY-MM
                LocalDate.parse("$releaseDate-01", DateTimeFormatter.ISO_LOCAL_DATE)

            releaseDate.matches(Regex("""\d{4}""")) -> // YYYY
                LocalDate.parse("$releaseDate-01-01", DateTimeFormatter.ISO_LOCAL_DATE)

            else -> LocalDate.MIN
        }
    }
}
