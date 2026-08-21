package org.releasetrackr.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.releasetrackr.domain.internal.WatchlistArtist
import org.springframework.stereotype.Repository
import java.io.File
import java.time.Instant

@Repository
class WatchlistRepository {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    private val watchlistFile = File("src/main/resources/watchlist.json")

    data class WatchlistFile(
        val artists: List<WatchlistArtistData>
    )

    data class WatchlistArtistData(
        val id: String,
        val name: String,
        val dateAdded: String
    )

    fun findAll(): List<WatchlistArtist> {
        if (!watchlistFile.exists()) {
            return emptyList()
        }
        val watchlistFile = objectMapper.readValue<WatchlistFile>(watchlistFile)
        return watchlistFile.artists.map { it.toDomain() }
    }

    fun findById(id: String): WatchlistArtist? {
        return findAll().find { it.id == id }
    }

    fun save(artist: WatchlistArtist) {
        val artists = findAll().toMutableList()
        val existingIndex = artists.indexOfFirst { it.id == artist.id }
        
        if (existingIndex >= 0) {
            artists[existingIndex] = artist
        } else {
            artists.add(artist)
        }
        
        saveAll(artists)
    }

    fun deleteById(id: String) {
        val artists = findAll().filter { it.id != id }
        saveAll(artists)
    }

    fun existsById(id: String): Boolean {
        return findAll().any { it.id == id }
    }

    private fun saveAll(artists: List<WatchlistArtist>) {
        val watchlistFile = WatchlistFile(
            artists = artists.map { it.toData() }
        )
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(watchlistFile)
            .let { File("src/main/resources/watchlist.json").writeText(it) }
    }

    private fun WatchlistArtistData.toDomain(): WatchlistArtist {
        return WatchlistArtist(
            id = id,
            name = name,
            dateAdded = Instant.parse(dateAdded)
        )
    }

    private fun WatchlistArtist.toData(): WatchlistArtistData {
        return WatchlistArtistData(
            id = id,
            name = name,
            dateAdded = dateAdded.toString()
        )
    }
}
