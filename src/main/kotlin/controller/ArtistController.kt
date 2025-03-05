package org.releasetrackr.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ArtistController {

    /**
     * TODO:
     * - Get all artists based on searchstring
     * - Add artist to watchlist
     * - Remove artist from watchlist
     */

    @GetMapping("/hello")
    fun sayHello(): String {
        return "Hello, Spring Boot with Kotlin!"
    }
}