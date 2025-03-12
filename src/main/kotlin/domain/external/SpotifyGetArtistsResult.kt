package org.releasetrackr.domain.external

data class SpotifyGetArtistsResult(
    var artists: List<Artist>
) {

    data class Artist(
        var id: String,
        var name: String,
        var images: List<Image> = emptyList() // Spotify may return an empty list
    )

    data class Image(
        var url: String,
        var height: Int,
        var width: Int
    )
}