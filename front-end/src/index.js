import React from 'react';
import { createRoot } from 'react-dom/client';
import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/global.css'
import '@fortawesome/fontawesome-free/css/all.css';

import App from './App';
import {LoadingProvider} from './context/LoadingContext';

const root = createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
      <LoadingProvider>
          <App/>
      </LoadingProvider>
  </React.StrictMode>
);