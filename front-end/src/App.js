import React, { useState, useEffect, useCallback } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Register from './pages/Register';
import Login from './pages/Login';
import NavBar from './components/NavBar';
import Profile from './pages/Profile';
import Achievements from './pages/Achievements';

function App() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [userInformation, setUserInformation] = useState({});

    const checkUserStatus = useCallback(async (email, token) => {
        if (!token) {
            setIsLoggedIn(false);
            setIsAdmin(false);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8090/api/v1/user/info?email=${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (response.ok) {
                const { userInformation } = await response.json();
                const isAdmin = userInformation.role === 'ADMIN';
                setIsLoggedIn(true);
                setIsAdmin(isAdmin);
                setUserInformation(userInformation);
            } else {
                setIsLoggedIn(false);
                setIsAdmin(false);
                localStorage.removeItem('token');
                localStorage.removeItem('email');
            }
        } catch (error) {
            console.error('Error fetching user info:', error);
            setIsLoggedIn(false);
            setIsAdmin(false);
            localStorage.removeItem('token');
            localStorage.removeItem('email');
        }
    }, []);

useEffect(() => {
    if (token && email) {
        checkUserStatus(email, token);
    }
}, [checkUserStatus, token, email]);

    return (
        <Router>
            <div className='app-container'>
                <NavBar 
                  isLoggedIn={isLoggedIn} 
                  isAdmin={isAdmin}
                  setIsLoggedIn={setIsLoggedIn}
                  setIsAdmin={setIsAdmin}
                  userInformation={userInformation}/>
                <div className='content'>
                  <Routes>
                      <Route path="/" exact element={<Home />} />
                      <Route path="/register" exact element={<Register />} />
                      <Route path='/profile' exact element={
                        <Profile 
                            userInformation={userInformation}
                        />}
                      />
                      <Route path='/achievements' exact element={
                        <Achievements 
                            token={token}
                            email={email}
                        />}
                      />
                      <Route path="/login" exact element={
                          <Login 
                              setIsLoggedIn={setIsLoggedIn} 
                              setIsAdmin={setIsAdmin}
                              setUserInformation={setUserInformation}/>} 
                            />
                      <Route path="/not-found" exact element={<NotFound />} />  
                  </Routes>
                </div>        
            </div>
            <ToastContainer />
        </Router>  
    );
}

export default App;