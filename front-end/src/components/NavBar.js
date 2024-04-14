import React, { useState, useRef, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import quizLogo from '../res/logo.png';
import WebSocketService from '../services/WebSocketService';
import "../styles/navbar.css";

const NavBar = ({ isLoggedIn, isAdmin, setIsLoggedIn, setIsAdmin, userInformation }) => {
    const [notifications, setNotifications] = useState([]);
    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const toggleDropdown = () => setIsDropdownOpen(!isDropdownOpen);

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [dropdownRef]);

    // Fetch stored notifications
    const fetchNotifications = async () => {
        try {
            const token = localStorage.getItem('token');
            const email = localStorage.getItem('email');
            const response = await fetch(`http://localhost:8090/api/notifications/${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                const data = await response.json();
                setNotifications(data);
            } else {
                throw new Error('Failed to fetch notifications');
            }
        } catch (error) {
            console.error('Failed to fetch notifications:', error);
        }
    };

    useEffect(() => {
        const onError = (error) => {
            console.error("WebSocket Error: ", error);
        };

        if (!WebSocketService.isConnectionActive()) {
            WebSocketService.connect(notification => {
                setNotifications(prev => [...prev, notification]);
            }, onError);
        }

        if (isLoggedIn) {
            fetchNotifications();
        }

        return () => {
            WebSocketService.disconnect();
        };
    }, [isLoggedIn]); // Depend on isLoggedIn to re-fetch when the user logs in

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
                console.log('Couldn\'t logout');
            }
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    return (
        <nav className="navbar">
            <div className="connection-status">
                {WebSocketService.isConnectionActive() ? "Connected" : "Disconnected"}
            </div>
            <div className="navbar-logo">
                <img src={quizLogo} alt='Logo' />
            </div>
            <ul className="navbar-links">
                <li><NavLink to="/quizzes" className="btn">Решавай куизове</NavLink></li>
                <li><NavLink to="/create-quiz" className="btn">Създай куиз</NavLink></li>
                {isAdmin && <li><NavLink to="/requested" className="btn">Заявени куизове</NavLink></li>}
            </ul>
            <div className="navbar-auth">
                {isLoggedIn ? (
                    <div className="navbar-profile" onClick={toggleDropdown}>
                        <img src={userInformation.avatarUrl || quizLogo} alt="Profile" className="user-avatar" />
                        {isDropdownOpen && (
                            <div ref={dropdownRef} className="navbar-dropdown">
                                <button className='dropdown-item' onClick={() => navigate('/profile')}>Моят профил</button>
                                <button className='dropdown-item' onClick={() => navigate('/achievements')}>Постижения</button>
                                <button className='dropdown-item' onClick={() => navigate('/stats')}>Статистики</button>
                                <button className='dropdown-item' onClick={onLogout}>Отписване</button>
                            </div>
                        )}
                        <div className="notification-area">
                            {notifications.map((notif, index) => (
                                <div key={index} className="notification">{notif.message}</div>
                            ))}
                        </div>
                    </div>
                ) : (
                    <>
                        <NavLink to="/register" className="btn auth">Регистация</NavLink>
                        <NavLink to="/login" className="btn auth">Вписване</NavLink>
                    </>
                )}
            </div>
        </nav>
    );
};

export default NavBar;
