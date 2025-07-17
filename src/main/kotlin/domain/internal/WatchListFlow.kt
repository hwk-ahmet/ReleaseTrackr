import org.releasetrackr.domain.internal.Album

sealed class WatchListFlow {
    data class Status(val message: String) : WatchListFlow()
    data class Result(val albums: List<Album>) : WatchListFlow()
}
