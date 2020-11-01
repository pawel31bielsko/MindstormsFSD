import React, { useState, useEffect } from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
  const [distance, setDistance] = useState(0);

  useEffect(() => {
    fetch('/distance').then(res => res.json()).then(data => {
      setDistance(data.distance);
    });
  }, []);

  return (
    <div className="App">
      <header className="App-header">
      <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>

        <p>The current distance is {distance}.</p>
      </header>
    </div>
  );
}

export default App;