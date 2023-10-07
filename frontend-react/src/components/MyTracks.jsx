import React from 'react';

const tracks = [
  {
    id: 1,
    name: 'The Fat of the Land',
    artist: 'The Prodigy',
    album: 'Album 1',
    imageUrl: 'src/assets/TheProdigy-TheFatOfTheLand.jpg',
    duration: '3.54 min',
    dateAdded: '2022-01-15',
  },
  {
    id: 2,
    name: 'Track 2',
    artist: 'Artist 2',
    album: 'Album 2',
    imageUrl: 'src/assets/TheProdigy-TheFatOfTheLand.jpg',
    duration: '3.54 min',
    dateAdded: '2022-01-10',
  },
  {
    id: 3,
    name: 'Track 3',
    artist: 'Artist 3',
    album: 'Album 3',
    imageUrl: 'src/assets/TheProdigy-TheFatOfTheLand.jpg',
    duration: '3.54 min',
    dateAdded: '2022-01-05',
  },
  // ... add more tracks as you see fit
];

const styles = {
  container: {
    overflowY: 'scroll',
    height: '100vh',
    color: 'forestgreen',
  },
  header: {
    display: 'flex',
    marginBottom: '1rem',
    fontWeight: 'bold',
  },
  trackContainer: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '1rem',
  },
  trackImage: {
    width: '120px',
    position: 'relative',
    cursor: 'pointer',
  },
  image: {
    width: '100px',
    height: '100px',
    transition: 'filter 0.3s',
  },
  playButton: {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    fontSize: '1.5rem',
    display: 'none',
    color: 'white',
  },
  trackDetails: {
    width: '300px',
    display: 'flex',
    flexDirection: 'column',
  },
  fixedWidth: (width) => ({
    width,
  }),
};

const MyTracks = () => (
  <div style={styles.container}>
    <h2 className="font-bold text-3xl text-white text-left mt-4 mb-10">
      My Tracks
    </h2>

    <div style={styles.header}>
      <div style={styles.fixedWidth('120px')}>Image</div>
      <div style={styles.fixedWidth('300px')}>Track Details</div>
      <div style={styles.fixedWidth('150px')}>Album</div>
      <div style={styles.fixedWidth('120px')}>Date Added</div>
      <div style={styles.fixedWidth('40px')}>Like</div>
      <div style={styles.fixedWidth('100px')}>Duration</div>
    </div>

    {tracks.map((track) => (
      <div key={track.id} style={styles.trackContainer}>
        <div style={styles.trackImage} className="image-container">
          <img src={track.imageUrl} alt={track.name} style={styles.image} className="track-image" />
          <div style={styles.playButton} className="play-button">▶</div>
        </div>
        <div style={styles.trackDetails}>
          <div style={{ fontWeight: 'bold' }}>{track.name}</div>
          <div>{track.artist}</div>
        </div>
        <div style={styles.fixedWidth('150px')}>{track.album}</div>
        <div style={styles.fixedWidth('120px')}>{track.dateAdded}</div>
        <div style={{ ...styles.fixedWidth('40px'), textAlign: 'center' }}>♡</div>
        <div style={styles.fixedWidth('100px')}>{track.duration}</div>
      </div>
    ))}
  </div>
);

export default MyTracks;
