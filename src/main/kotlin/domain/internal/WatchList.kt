package org.releasetrackr.domain.internal

import org.releasetrackr.domain.external.SpotifyAlbum

data class WatchList(
    var albums: List<SpotifyAlbum> = emptyList()
) {

    data class Album(
        var releaseDate: String,
        var albumName: String,
        var artistName: String,
        var imageUrl: String,
        var albumUrl: String
    )

    companion object {
        fun empty() = WatchList(emptyList())
    }

}
