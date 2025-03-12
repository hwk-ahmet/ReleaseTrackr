package org.releasetrackr.driver

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@Validated
@ConfigurationProperties(prefix = "api.spotify")
class SpotifyConfiguration {
    @NotBlank
    lateinit var clientId: String

    @NotBlank
    lateinit var clientSecret: String

    @NotBlank
    lateinit var accessTokenUrl: String

}
