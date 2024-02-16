import React from 'react';
import { NavLink } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import quizLogo from '../res/logo.png'
import "../styles/navbar.css";

const NavBar = ({ isLoggedIn, isAdmin, setIsLoggedIn, setIsAdmin, userInformation }) => {
    const navigate = useNavigate();

    console.log(userInformation);

    const onLogout = async () => {
        const token = localStorage.getItem('token');
    
        try {
            const response = await fetch('http://localhost:8090/api/v1/user/logout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },

            });
    
            if (response.ok) {
                toast.success('Успешно отписване.');
    
                localStorage.removeItem('token');
                localStorage.removeItem('email');
                
                setIsLoggedIn(false);
                setIsAdmin(false);
    
                navigate('/login');
            } else {
                console.log('Couldnt logout');
            }
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-logo">
                <img src={quizLogo} alt='Logo'/>
            </div>
            <ul className="navbar-links">
                <li><NavLink to="/solve-quiz" className="btn">Решавай куизове</NavLink></li>
                <li><NavLink to="/create-quiz" className="btn">Създай куиз</NavLink></li>
                <li><NavLink to="/user-info" className="btn">User Info</NavLink></li>
                {isAdmin && <li><NavLink to="/view-requests" className="btn">View Requests</NavLink></li>}
            </ul>
            <div className="navbar-auth">
                {isLoggedIn ? (
                    <div className="navbar-profile">
                    <img src={userInformation.avatarUrl || quizLogo} alt="Profile" className="user-avatar" />
                    <button className='btn' onClick={onLogout}>Logout</button>
                </div>
                    
                ) : (
                    <>
                        <NavLink to="/register" className="btn">Регистация</NavLink>
                        <NavLink to="/login" className="btn">Вписване</NavLink>
                    </>
                )}
            </div>
        </nav>
    );
};

export default NavBar;
