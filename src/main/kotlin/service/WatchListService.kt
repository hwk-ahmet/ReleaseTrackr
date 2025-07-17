package org.releasetrackr.service

import WatchListFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.releasetrackr.domain.internal.Album
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

    suspend fun getWatchListFlow(authCode: String): Flow<WatchListFlow> = flow {
        emit(WatchListFlow.Status("Fetching all followed artists"))
        val followedArtists = spotifyGetFollowedArtistsDriver.getAllFollowedArtists(authCode)
        emit(WatchListFlow.Status("Fetching all artist albums"))
        val albums =
            spotifyGetArtistAlbumsDriver.getAlbumsForArtists(authCode, followedArtists.map { it.id })
        emit(WatchListFlow.Status("Shuffling and sorting"))

        val sortedAlbums = albums.sortedByDescending { album ->
            parseReleaseDate(album.releaseDate)
        }

        emit(WatchListFlow.Result(sortedAlbums))
    }


    suspend fun getWatchList(authCode: String): List<Album> {
        val followedArtists = spotifyGetFollowedArtistsDriver.getAllFollowedArtists(authCode)
        val albums = spotifyGetArtistAlbumsDriver.getAlbumsForArtists(authCode, followedArtists.map { it.id })

        val sortedAlbums = albums.sortedByDescending { album ->
            parseReleaseDate(album.releaseDate)
        }

        return sortedAlbums
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
