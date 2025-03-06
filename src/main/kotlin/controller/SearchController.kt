package org.releasetrackr.controller

import org.releasetrackr.domain.internal.SearchResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SearchController {

    @GetMapping("/search")
    fun search(@RequestParam searchString: String): List<SearchResult> {
        return listOf(
            SearchResult(
                "Richard Pryor", "K13TX655", "https://spotify.com/rich.png"
            )
        )
    }
}