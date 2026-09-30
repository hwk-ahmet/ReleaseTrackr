# Watchlist Search UX

## Goal

Help users add comedians quickly with a compact, image-based list.

## Current UX

- One modal with two modes:
  - `Search`
  - `Top 100`
- `Search` uses Spotify artist search and shows flat results.
- `Top 100` shows an ad-hoc list from `/api/comedians/top-followed`.
- User can `Select` or remove (`X`) with autosave.

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
