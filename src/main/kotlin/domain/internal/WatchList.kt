package org.releasetrackr.domain.internal

data class WatchList(
    var albums: List<Album> = emptyList()
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
