package org.releasetrackr.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class WatchListController {

    /**
     * TODO:
     * - Get all artists based on searchstring
     * - Add artist to watchlist
     * - Remove artist from watchlist
     */

    @GetMapping("/watchlist")
    fun list(): String {
        return "Hello, Spring Boot with Kotlin!"
    }

    @PutMapping("/watchlist")
    fun add(): String {
        return "Hello, Spring Boot with Kotlin!"
    }

    @DeleteMapping("/watchlist")
    fun delete(): String {
        return "Hello, Spring Boot with Kotlin!"
    }
}