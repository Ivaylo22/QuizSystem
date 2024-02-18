import React from 'react';
import '../styles/achievements.css';

const Achievement = ({ name, description, points, isEarned }) => {
    return (
        <div className={`achievement-card ${isEarned ? 'earned' : 'unearned'}`}>
            <h3>{name}</h3>
            <p>{description}</p>
            <span>Points: {points}</span>
        </div>
    );
};

export default Achievement;