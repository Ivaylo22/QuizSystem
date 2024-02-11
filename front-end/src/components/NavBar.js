import React from 'react';
import { NavLink } from 'react-router-dom';
import "../styles/navbar.css";

const NavBar = ({ isLoggedIn, isAdmin, onLogout }) => {
    return (
        <nav className="navbar">
            <div className="navbar-logo">
                {/* Your logo */}
            </div>
            <ul className="navbar-links">
                <li><NavLink to="/solve-quiz">Solve Quiz</NavLink></li>
                <li><NavLink to="/create-quiz">Create Quiz</NavLink></li>
                <li><NavLink to="/filter">Filter</NavLink></li>
                <li><NavLink to="/user-info">User Info</NavLink></li>
                {isAdmin && <li><NavLink to="/view-requests">View Requests</NavLink></li>}
            </ul>
            <div className="navbar-auth">
                {isLoggedIn ? (
                    <button className='btn'onClick={onLogout}>Logout</button>
                ) : (
                    <>
                        <NavLink to="/register" className="btn">Register</NavLink>
                        <NavLink to="/login" className="btn">Login</NavLink>
                    </>
                )}
            </div>
        </nav>
    );
};

export default NavBar;
