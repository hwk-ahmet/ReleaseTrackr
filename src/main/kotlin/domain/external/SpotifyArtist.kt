package org.releasetrackr.domain.external

data class SpotifyArtist(
    var id: String,
    var name: String,
    var images: List<SpotifyImage> = emptyList() // Spotify may return an empty list
) {

    data class SpotifyImage(
        var url: String,
        var height: Int,
        var width: Int
    )

}
