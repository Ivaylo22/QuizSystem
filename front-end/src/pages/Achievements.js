import React, { useEffect, useState, useCallback } from 'react';
import Achievement from '../components/Achievement'
import "../styles/achievements.css"; // Assume you have corresponding CSS

const Achievements = ({ token, email }) => {
    const [allAchievements, setAllAchievements] = useState([]);
    const [earnedAchievements, setEarnedAchievements] = useState([]);

    const fetchAllAchievements = useCallback(async () => {
        const response = await fetch('http://localhost:8090/api/v1/achievement/list', {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const data = await response.json();
            setAllAchievements(data.achievements);
        }
    }, [token]);

    const fetchEarnedAchievements = useCallback(async () => {
        const response = await fetch(`http://localhost:8090/api/v1/achievement/list-earned?email=${encodeURIComponent(email)}`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const data = await response.json();
            setEarnedAchievements(data.achievements);
        }
    }, [email, token]);

    useEffect(() => {
        fetchAllAchievements();
        fetchEarnedAchievements();
    }, [fetchAllAchievements, fetchEarnedAchievements]);

    return (
        <div className="achievements-container">
            {allAchievements.map((achievement) => (
                <Achievement
                    key={achievement.id}
                    name={achievement.name}
                    description={achievement.description}
                    points={achievement.achievementPoints}
                    isEarned={earnedAchievements.some((earned) => earned.id === achievement.id)}
                />
            ))}
        </div>
    );
};

export default Achievements;
