package org.releasetrackr.domain.internal

data class ArtistsSearchResult(
    var artists: List<Artist> = emptyList()
) {

    data class Artist(
        var name: String,
        var id: String,
        var imageUrl: String
    )

    companion object {
        fun empty() = ArtistsSearchResult(emptyList())
    }

}
