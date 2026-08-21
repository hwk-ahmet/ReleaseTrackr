import React, { useEffect, useState } from 'react';
import './App.css';

function App() {
  const [albums, setAlbums] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/albums')
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch albums');
        }
        return response.json();
      })
      .then(data => {
        setAlbums(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  return (
    <div className="app-wrapper">
      <div className="nes-container is-rounded is-dark">
        <h1 className="nes-text is-primary">RELEASE TRACKR</h1>
        <br />
        <h2 className="nes-text is-normal">Latest Releases from Your Watchlist</h2>
        <br />
        
        {loading && <p className="nes-text">Loading albums...</p>}
        
        {error && (
          <div className="nes-container is-error is-rounded">
            <p>Error: {error}</p>
          </div>
        )}
        
        {!loading && !error && albums.length === 0 && (
          <p className="nes-text">No albums found. Add artists to your watchlist!</p>
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
      </div>
    </div>
  );
}

export default App;
