package org.releasetrackr.domain.internal

import java.time.Instant

data class WatchlistArtist(
    val id: String,
    val name: String,
    val dateAdded: Instant,
    val imageUrl: String = ""
)
