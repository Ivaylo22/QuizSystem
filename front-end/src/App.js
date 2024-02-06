import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Register from './pages/Register';

import NavBar from './components/NavBar';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        setIsLoggedIn(!!token);
        
        const fetchUserInfo = async () => {
            const response = await fetch('/api/v1/user/info', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log(response);
            const userData = await response.json();
            console.log(userData);
            setIsAdmin(userData.isAdmin);
        };
        if (isLoggedIn) {
            fetchUserInfo();
        }
    }, [isLoggedIn]);

    return (
      <Router>
          <div className='app-container'>
              <NavBar isLoggedIn={isLoggedIn} isAdmin={isAdmin} />
              <div className='content'>
                <Routes>
                    <Route path="/" exact element={<Home />} />
                    <Route path="/register" exact element={<Register />} />
                    <Route path="/not-found" exact element={<NotFound />} />  
                </Routes>
              </div>        
          </div>
      </Router>  
  );
}

export default App;