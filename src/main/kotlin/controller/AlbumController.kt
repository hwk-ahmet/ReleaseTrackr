package org.releasetrackr.controller

import org.releasetrackr.domain.internal.Album
import org.releasetrackr.service.WatchListService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/albums")
class AlbumController(
    private val watchListService: WatchListService
) {

    @GetMapping
    suspend fun getAlbums(): List<Album> {
        return watchListService.getWatchList()
    }
}
