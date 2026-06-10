import fs from 'fs';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { StaticRouter } from 'react-router-dom/server.js';
import App from './src/App.jsx';

try {
  console.log(renderToString(React.createElement(StaticRouter, { location: "/" }, React.createElement(App))));
  console.log("Render successful!");
} catch (e) {
  console.error("Render failed:");
  console.error(e);
}
