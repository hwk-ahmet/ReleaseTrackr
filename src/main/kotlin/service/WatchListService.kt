package org.releasetrackr.service

import WatchListFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.releasetrackr.domain.internal.Album
import org.releasetrackr.domain.internal.Artist
import org.releasetrackr.driver.SpotifyClientCredentialsDriver
import org.releasetrackr.driver.SpotifyGetArtistAlbumsDriver
import org.releasetrackr.repository.WatchlistRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class WatchListService(
    private val watchlistRepository: WatchlistRepository,
    private val spotifyClientCredentialsDriver: SpotifyClientCredentialsDriver,
    private val spotifyGetArtistAlbumsDriver: SpotifyGetArtistAlbumsDriver
) {

    suspend fun getWatchListFlow(): Flow<WatchListFlow> = flow {
        emit(WatchListFlow.Status("Fetching artists from watchlist"))
        val watchlistArtists = watchlistRepository.findAll()
        emit(WatchListFlow.Status("Fetching all artist albums"))
        val accessToken = spotifyClientCredentialsDriver.getAccessToken()
        val albums =
            spotifyGetArtistAlbumsDriver.getAlbumsForArtists(accessToken, watchlistArtists.map { it.id })
        emit(WatchListFlow.Status("Shuffling and sorting"))

        val sortedAlbums = albums.sortedByDescending { album ->
            parseReleaseDate(album.releaseDate)
        }

        emit(WatchListFlow.Result(sortedAlbums))
    }


    suspend fun getWatchList(): List<Album> {
        val watchlistArtists = watchlistRepository.findAll()
        val accessToken = spotifyClientCredentialsDriver.getAccessToken()
        val albums = spotifyGetArtistAlbumsDriver.getAlbumsForArtists(accessToken, watchlistArtists.map { it.id })

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
