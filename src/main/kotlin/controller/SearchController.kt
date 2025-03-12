package org.releasetrackr.controller

import mu.KLogging
import org.releasetrackr.domain.external.SpotifySearchResult
import org.releasetrackr.domain.internal.SearchResult
import org.releasetrackr.service.SpotifyService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SearchController(private val spotifyService: SpotifyService) {

    @GetMapping("/search")
    fun search(@RequestParam searchString: String): SpotifySearchResult? {
        return spotifyService.searchArtists(searchString)
    }

    private companion object : KLogging() {

    }
}
