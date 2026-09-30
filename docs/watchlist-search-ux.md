# Watchlist Search UX

## Goal

Help users add comedians quickly with a compact, image-based list.

## Current UX

- One modal with two modes:
  - `Search`
  - `Top 100`
- Modal body uses a two-panel layout on desktop:
  - Left panel: source list (`Search` or `Top 100`)
  - Right panel: current selected watchlist
  - Stacks into two full-width panels on small screens
- `Search` uses Spotify artist search and shows flat results.
- `Top 100` shows an ad-hoc list from `/api/comedians/top-followed`.
- User can `Select` or `Remove` with autosave.
- Each panel has a single scrollable list area to avoid nested scroll behavior.

## Behavior

1. User types into search input.
2. Frontend debounces request (1s).
3. Backend returns flat artist list from Spotify.
4. User selects artist.
5. Selection is persisted to `watchlist.json`.

## API Shape

- `GET /api/artists/search?q={query}` returns:

```json
[
  {
    "spotifyArtistId": "1PflWU7nPUElTWqYUBkK6W",
    "name": "Rory Scovel",
    "imageUrl": "https://...",
    "genres": ["comedy", "stand-up"]
  }
]
```

## Notes

- Search results are intentionally flat (no score/group split).
- UI prioritizes fast manual curation over strict classifier logic.
- Rows stay compact but readable (thumbnail + name + action) to preserve retro density while improving scanability.

## Explore Plan (Draft)

- Planned rename: `Top 100` -> `Explore`.
- Planned paging behavior: show 6 at a time and replace current 6 when moving to next page.
- Planned filtering: do not suggest artists already in watchlist (and likely dismissed in a later phase).
- Planned phased growth:
  - start with existing top-100 source
  - later increase source size (e.g. 200+) without changing UI pagination model
- Dataset caveat: Spotify supports `offset` for artist search pagination, but does not provide a native global top-comedians feed.
- This is an early planning note; final behavior and API contracts may change.
