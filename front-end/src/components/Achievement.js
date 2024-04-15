import React from 'react';
import '../styles/achievements.css';

const Achievement = ({ name, description, points, isEarned }) => {
    const achievementClass = isEarned ? 'achievement-card earned' : 'achievement-card unearned';

    return (
        <div className={achievementClass}>
            <h3>{name}</h3>
            <p>{description}</p>
            <p>{points} Points</p>
        </div>
    );
};

export default Achievement;