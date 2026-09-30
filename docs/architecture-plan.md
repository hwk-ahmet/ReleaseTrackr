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

## Explore Roadmap (Draft)

- Near-term plan:
  - rename `Top 100` UX mode to `Explore`
  - paginate existing top list in small pages (6 per page)
  - keep page logic stable while source size changes (100 -> 200 -> more)
- Source expansion plan (not implemented yet):
  - Spotify has no global "top comedians" endpoint
  - build larger pool by running multiple Spotify artist search queries
  - paginate each query with `limit` + `offset`, then merge + dedupe
  - apply comedy heuristics and rank by followers/popularity
- This section is planning-only and intentionally not finalized.
