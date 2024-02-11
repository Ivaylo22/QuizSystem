import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import '../styles/login.css';

const useUserStatus = (setIsLoggedIn, setIsAdmin) => {
  // Updated to accept email and token as parameters
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
      } else {
        setIsLoggedIn(false);
        setIsAdmin(false);
        toast.error('Failed to verify user status.');
      }
    } catch (error) {
      console.error('Error fetching user info:', error);
      toast.error('Error fetching user info.');
      setIsLoggedIn(false);
      setIsAdmin(false);
    }
  }, [setIsLoggedIn, setIsAdmin]);

  return checkUserStatus;
};

const Login = ({ setIsLoggedIn, setIsAdmin }) => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const navigate = useNavigate();
  const checkUserStatus = useUserStatus(setIsLoggedIn, setIsAdmin);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevFormData => ({ ...prevFormData, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { email, password } = formData;

    try {
      const response = await fetch('http://localhost:8090/api/v1/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('token', data.token);
        await checkUserStatus(email, data.token);
        navigate("/");
      } else {
        toast.error('Invalid login credentials.');
      }
    } catch (error) {
      console.error('Error logging in:', error);
      toast.error('Login request failed.');
    }
  };

  return (
    <div className="container mt-5 pt-1 pb-2 login-container">
      <form onSubmit={handleSubmit} className="w-50 mx-auto">
        <div className="mb-3">
          <input type="email" name="email" className="form-control" placeholder="Email" value={formData.email} onChange={handleChange} required />
        </div>
        <div className="mb-3">
          <input type="password" name="password" className="form-control" placeholder="Password" value={formData.password} onChange={handleChange} required />
        </div>
        <div className="mb-2 text-center">
          <button type="submit" className="btn btn-login">Login</button>
        </div>
      </form>
    </div>
  );
};

export default Login;
