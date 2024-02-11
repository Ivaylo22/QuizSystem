import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Register from './pages/Register';
import Login from './pages/Login';
import NavBar from './components/NavBar';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    const handleLogout = () => {
      localStorage.removeItem('token');
      setIsLoggedIn(false);
      setIsAdmin(false);
    };

    console.log(isAdmin, isLoggedIn)

    return (
      <Router>
          <div className='app-container'>
              <NavBar isLoggedIn={isLoggedIn} isAdmin={isAdmin} onLogout={handleLogout}/>
              <div className='content'>
                <Routes>
                    <Route path="/" exact element={<Home />} />
                    <Route path="/register" exact element={<Register />} />
                    <Route path="/login" exact element={
                        <Login 
                            setIsLoggedIn={setIsLoggedIn} 
                            setIsAdmin={setIsAdmin}/>} 
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