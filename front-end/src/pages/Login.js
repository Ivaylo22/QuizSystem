import React, { useState, useCallback, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import '../styles/login.css';

const Login = ({setIsLoggedIn, setIsAdmin, setUserInformation}) => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const navigate = useNavigate();

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
            navigate("/");
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
}, [setUserInformation, setIsAdmin, setIsLoggedIn, navigate]);

useEffect(() => {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    if (token && email) {
        checkUserStatus(email, token);
    }
}, [checkUserStatus]);

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
              localStorage.setItem('email', email);
              toast.success("Успешно вписване");
              checkUserStatus(email, data.token);
              navigate("/");
          } else {
              toast.error('Невалидни данни');
          }
      } catch (error) {
          console.error('Error logging in:', error);
          toast.error('Възникна грешка. Моля опитайте по-късно');
      }
  };

  return (
      <div className="container pt-5 pb-2 login-container">
          <form onSubmit={handleSubmit} className="w-50 mx-auto">
              <div className="mb-3">
                  <label htmlFor="email" className="form-label">Имейл:</label>
                  <input type="email" name="email" className="form-control" placeholder="Имейл" value={formData.email} onChange={handleChange} required />
              </div>
              <div className="mb-3">
                  <label htmlFor="password" className="form-label">Парола:</label>
                  <input type="password" name="password" className="form-control" placeholder="Парола" value={formData.password} onChange={handleChange} required />
              </div>
              <div className="mb-2 text-center">
                  <button type="submit" className="btn">Вписване</button>
              </div>
          </form>
      </div>
  );
};

export default Login;