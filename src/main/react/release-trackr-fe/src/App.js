import React, { useEffect, useRef, useState } from 'react';
import './App.css';

function App() {
  const [albums, setAlbums] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasTracked, setHasTracked] = useState(false);
  const [watchlistModalOpen, setWatchlistModalOpen] = useState(false);
  const [watchlist, setWatchlist] = useState([]);
  const [watchlistLoading, setWatchlistLoading] = useState(false);
  const [watchlistError, setWatchlistError] = useState(null);
  const [searchValue, setSearchValue] = useState('');
  const [searchLoading, setSearchLoading] = useState(false);
  const [searchError, setSearchError] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
  const [watchlistMode, setWatchlistMode] = useState('search');
  const [topComedians, setTopComedians] = useState([]);
  const [topLoading, setTopLoading] = useState(false);
  const [topError, setTopError] = useState(null);
  const searchInputRef = useRef(null);

  const trackReleases = () => {
    setLoading(true);
    setError(null);
    fetch('/api/albums')
      .then(response => {
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Albums endpoint not found. Is the backend running?');
          }
          if (response.status === 500) {
            throw new Error('Server error. Check backend logs.');
          }
          throw new Error(`Failed to fetch albums (${response.status})`);
        }
        return response.json();
      })
      .then(data => {
        setHasTracked(true);
        setAlbums(data);
        setLoading(false);
      })
      .catch(err => {
        if (err.message.includes('Failed to fetch')) {
          setError('Cannot connect to backend. Make sure Spring Boot is running on port 8080.');
        } else {
          setError(err.message);
        }
        setLoading(false);
      });
  };

  const openWatchlistModal = () => {
    setWatchlistModalOpen(true);
    setWatchlistMode('search');
    setWatchlistLoading(true);
    setWatchlistError(null);

    fetch('/api/watchlist')
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to load watchlist');
        }
        return response.json();
      })
      .then(data => {
        setWatchlist(data);
        setWatchlistLoading(false);
      })
      .catch(() => {
        setWatchlistError('Could not load watchlist right now.');
        setWatchlistLoading(false);
      });
  };

  const loadTopComedians = () => {
    if (topComedians.length > 0 || topLoading) {
      return;
    }

    setTopLoading(true);
    setTopError(null);

    fetch('/api/comedians/top-followed?limit=100')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to load top comedians');
        }
        return response.json();
      })
      .then((data) => {
        setTopComedians(data);
        setTopLoading(false);
      })
      .catch(() => {
        setTopError('Could not load top comedians right now.');
        setTopLoading(false);
      });
  };

  const switchToTopMode = () => {
    setWatchlistMode('top');
    loadTopComedians();
  };

  const deselectComedian = (artistId) => {
    fetch(`/api/watchlist/${artistId}`, { method: 'DELETE' })
      .then(response => {
        if (!response.ok && response.status !== 204) {
          throw new Error('Failed to update watchlist');
        }
        setWatchlist((current) => current.filter((comedian) => comedian.id !== artistId));
      })
      .catch(() => {
        setWatchlistError('Could not update watchlist right now.');
      });
  };

  const addComedianToWatchlist = (artist) => {
    fetch('/api/watchlist', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        spotifyArtistId: artist.spotifyArtistId,
        name: artist.name,
        imageUrl: artist.imageUrl
      })
    })
      .then(response => {
        if (!response.ok && response.status !== 204) {
          throw new Error('Failed to update watchlist');
        }
        setSearchValue('');
        setSearchResults([]);
        setSearchError(null);
        setHasSearched(false);
        if (searchInputRef.current) {
          searchInputRef.current.focus();
        }
        setWatchlist((current) => {
          if (current.some((item) => item.id === artist.spotifyArtistId)) {
            return current;
          }
          return [
            {
              id: artist.spotifyArtistId,
              name: artist.name,
              dateAdded: new Date().toISOString(),
              imageUrl: artist.imageUrl || ''
            },
            ...current
          ];
        });
      })
      .catch(() => {
        setWatchlistError('Could not update watchlist right now.');
      });
  };

  useEffect(() => {
    if (!watchlistModalOpen) {
      return;
    }

    if (searchValue.trim().length < 2) {
      setSearchResults([]);
      setSearchError(null);
      setHasSearched(false);
      return;
    }

    const timeoutId = setTimeout(() => {
      setSearchLoading(true);
      setSearchError(null);

      fetch(`/api/artists/search?q=${encodeURIComponent(searchValue.trim())}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error('Search failed');
          }
          return response.json();
        })
        .then((data) => {
          setSearchResults(data);
          setHasSearched(true);
          setSearchLoading(false);
        })
        .catch(() => {
          setSearchError('Could not load search results right now.');
          setHasSearched(true);
          setSearchLoading(false);
        });
    }, 1000);

    return () => clearTimeout(timeoutId);
  }, [searchValue, watchlistModalOpen]);

  const isSelected = (spotifyArtistId) => watchlist.some((item) => item.id === spotifyArtistId);
  const mergedSearchResults = searchResults;

  return (
    <div className="app-wrapper">
      <div className="nes-container is-rounded is-dark">
        <h1 className="nes-text is-primary">RELEASE TRACKR</h1>
        <br />
        <h2 className="nes-text is-normal">Latest Releases from Your Watchlist</h2>
        <br />

        <div className="actions-row">
          <button type="button" className="nes-btn" onClick={openWatchlistModal}>
            Update Watchlist
          </button>
          <button type="button" className="nes-btn is-primary" onClick={trackReleases} disabled={loading}>
            {loading ? 'Tracking...' : "Track 'em"}
          </button>
        </div>
        <br />
        
        {loading && <p className="nes-text">Loading albums...</p>}
        
        {error && (
          <div className="nes-container is-error is-rounded">
            <p>Error: {error}</p>
          </div>
        )}
        
        {!loading && !error && hasTracked && albums.length === 0 && (
          <p className="nes-text">No albums found. Add artists to your watchlist!</p>
        )}

        {!loading && !error && !hasTracked && (
          <p className="nes-text">Click "Track 'em" to fetch latest releases.</p>
        )}
        
        {!loading && !error && albums.length > 0 && (
          <div className="albums-grid">
            {albums.map((album, index) => (
              <div key={index} className="album-card nes-container is-rounded">
                <img 
                  src={album.imageUrl} 
                  alt={album.albumName}
                  className="album-cover"
                />
                <h3 className="nes-text is-primary album-title">{album.albumName}</h3>
                <p className="nes-text album-artist">{album.artistName}</p>
                <p className="nes-text is-muted album-date">{album.releaseDate}</p>
                <a 
                  href={album.albumUrl} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  className="nes-btn is-primary"
                >
                  Listen on Spotify
                </a>
              </div>
            ))}
          </div>
        )}

        {watchlistModalOpen && (
          <div className="modal-backdrop" role="dialog" aria-modal="true">
            <div className="modal-panel nes-container is-rounded is-dark">
              <div className="modal-header">
                <h3 className="nes-text is-primary">Update Watchlist</h3>
                <button
                  type="button"
                  className="nes-btn is-error"
                  onClick={() => setWatchlistModalOpen(false)}
                >
                  Close
                </button>
              </div>

              <div className="mode-switch-row">
                <button
                  type="button"
                  className={`nes-btn ${watchlistMode === 'search' ? 'is-primary' : ''}`}
                  onClick={() => setWatchlistMode('search')}
                >
                  Search
                </button>
                <button
                  type="button"
                  className={`nes-btn ${watchlistMode === 'top' ? 'is-primary' : ''}`}
                  onClick={switchToTopMode}
                >
                  Top 100
                </button>
              </div>

              {watchlistMode === 'search' && (
                <div className="search-area">
                  <div className="search-input-row">
                    <input
                      ref={searchInputRef}
                      type="text"
                      className="nes-input"
                      placeholder="Search comedians"
                      value={searchValue}
                      onChange={(event) => setSearchValue(event.target.value)}
                    />
                    {searchValue && (
                      <button
                        type="button"
                        className="search-clear-btn nes-btn is-error"
                        onClick={() => {
                          setSearchValue('');
                          setSearchResults([]);
                          setSearchError(null);
                          setHasSearched(false);
                        }}
                        aria-label="Clear search"
                      >
                        x
                      </button>
                    )}
                  </div>

                  {searchValue.trim().length >= 2 && (
                    <div className="search-results-shell">
                      {searchLoading && <p className="nes-text">Searching...</p>}

                      {searchError && <p className="nes-text is-error">{searchError}</p>}

                      {!searchLoading && !searchError && hasSearched && (
                        <div className="search-results">
                          {mergedSearchResults.length === 0 ? (
                            <p className="nes-text">No artists found.</p>
                          ) : (
                            <ul className="search-list">
                              {mergedSearchResults.map((artist) => (
                                <li key={artist.spotifyArtistId} className="search-item">
                                  <div className="search-artist-info">
                                    {artist.imageUrl ? (
                                      <img src={artist.imageUrl} alt={artist.name} className="search-artist-image" />
                                    ) : (
                                      <div className="search-artist-image search-artist-placeholder" />
                                    )}
                                    <span>{artist.name}</span>
                                  </div>
                                  {isSelected(artist.spotifyArtistId) ? (
                                    <span className="nes-text is-success">Selected</span>
                                  ) : (
                                    <button
                                      type="button"
                                      className="nes-btn is-success"
                                      onClick={() => addComedianToWatchlist(artist)}
                                    >
                                      Select
                                    </button>
                                  )}
                                </li>
                              ))}
                            </ul>
                          )}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}

              {watchlistMode === 'top' && (
                <div className="search-area">
                  <div className="search-results-shell">
                    {topLoading && <p className="nes-text">Loading top comedians...</p>}
                    {topError && <p className="nes-text is-error">{topError}</p>}
                    {!topLoading && !topError && topComedians.length > 0 && (
                      <div className="search-results">
                        <ul className="search-list">
                          {topComedians.map((artist) => (
                            <li key={artist.spotifyArtistId} className="search-item">
                              <div className="search-artist-info">
                                {artist.imageUrl ? (
                                  <img src={artist.imageUrl} alt={artist.name} className="search-artist-image" />
                                ) : (
                                  <div className="search-artist-image search-artist-placeholder" />
                                )}
                                <span>{artist.name}</span>
                              </div>
                              {isSelected(artist.spotifyArtistId) ? (
                                <span className="nes-text is-success">Selected</span>
                              ) : (
                                <button
                                  type="button"
                                  className="nes-btn is-success"
                                  onClick={() => addComedianToWatchlist(artist)}
                                >
                                  Select
                                </button>
                              )}
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </div>
                </div>
              )}

              <div className="selected-section">
                <p className="nes-text selected-label">Currently selected: ({watchlist.length})</p>

                {watchlistLoading && <p className="nes-text">Loading watchlist...</p>}
                {watchlistError && <p className="nes-text is-error">{watchlistError}</p>}
                {!watchlistLoading && !watchlistError && watchlist.length === 0 && (
                  <p className="nes-text">No selected comedians yet.</p>
                )}

                {!watchlistLoading && !watchlistError && watchlist.length > 0 && (
                  <ul className="watchlist-list">
                    {watchlist.map((comedian) => (
                      <li key={comedian.id} className="watchlist-item">
                        <div className="watchlist-artist-info">
                          {comedian.imageUrl ? (
                            <img src={comedian.imageUrl} alt={comedian.name} className="watchlist-artist-image" />
                          ) : (
                            <div className="watchlist-artist-image watchlist-artist-placeholder" />
                          )}
                          <span>{comedian.name}</span>
                        </div>
                        <button
                          type="button"
                          className="nes-btn is-error"
                          onClick={() => deselectComedian(comedian.id)}
                        >
                          X
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
