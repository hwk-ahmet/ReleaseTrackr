package org.releasetrackr.service

import org.releasetrackr.domain.external.SpotifyGetArtistsResult
import org.releasetrackr.driver.SpotifyGetArtistsByIdDriver
import org.springframework.stereotype.Service

@Service
class WatchListService(private val spotifyGetArtistsByIdDriver: SpotifyGetArtistsByIdDriver) {

    fun getWatchList(): SpotifyGetArtistsResult? {
        return try {
            val inputStream = object {}.javaClass.getResourceAsStream("/watchlist.txt")
            val ids = inputStream?.bufferedReader()?.readLines()
            return ids?.let { spotifyGetArtistsByIdDriver.getByIds(it) }
        } catch (e: Exception) {
            println("Error reading resource file: ${e.message}")
            null
        }
    }
}
