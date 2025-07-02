package org.releasetrackr.domain.external

data class SpotifyAlbum(
    val name: String,
    val release_date: String,
    var release_date_precision: String,
    val images: List<Image>,
    val artists: List<Artist>,
    val external_urls: Map<String, String>?
) {
    data class Image(
        val url: String,
        val height: Int,
        val width: Int
    )

    data class Artist(
        val name: String
    )
}