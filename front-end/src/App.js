import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import NotFound from './components/NotFound';
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
          <div>
              <NavBar isLoggedIn={isLoggedIn} isAdmin={isAdmin} />
              <Routes>
                  <Route path="/" exact element={<Home />} />
                  <Route path="/not-found" exact element={<NotFound />} />  
              </Routes>
          </div>
      </Router>  
  );
}

export default App;