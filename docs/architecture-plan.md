# ReleaseTrackr Architecture Plan (Current)

## Goal

Track comedy releases from a local watchlist of Spotify artist IDs.

## Current Data Model

- `src/main/resources/watchlist.json`
  - selected artists only
  - stores `id`, `name`, `dateAdded`, `imageUrl` (base64 data URI)

## Current Backend Flow

- `GET /api/watchlist`
  - returns selected artists (alphabetical)
- `POST /api/watchlist`
  - adds artist and stores image as base64
- `DELETE /api/watchlist/{spotifyArtistId}`
  - removes selection
- `GET /api/artists/search?q=...`
  - Spotify artist search (flat list)
- `GET /api/comedians/top-followed?limit=100`
  - ad-hoc top comedian list from Spotify, filtered by comedy-related genres
- `GET /api/albums`
  - fetches/sorts releases for selected watchlist artists

## Current Frontend Flow

- Landing page shows actions: `Update Watchlist`, `Track 'em`
- `Track 'em` manually triggers release fetch
- Watchlist modal supports:
  - Search mode (Spotify artist search)
  - Top 100 mode (ad-hoc comedian list)
  - Select / remove with autosave

## Notes

- Spotify genres are treated as a practical heuristic.
- This architecture is intentionally ad-hoc and simple for fast iteration.
