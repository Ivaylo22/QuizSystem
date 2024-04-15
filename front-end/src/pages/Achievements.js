import React, { useEffect, useState, useCallback } from 'react';
import Achievement from '../components/Achievement';
import "../styles/achievements.css";
import { useLoading } from '../context/LoadingContext';

const Achievements = ({ token, email }) => {
    const [allAchievements, setAllAchievements] = useState([]);
    const [earnedAchievements, setEarnedAchievements] = useState([]);
    const { setLoading } = useLoading();

    const fetchEarnedAchievements = useCallback(async () => {
        const response = await fetch(`http://localhost:8090/api/v1/achievement/list-earned?email=${encodeURIComponent(email)}`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const data = await response.json();
            setEarnedAchievements(data.achievements);
            return data.achievements;
        }
        return [];
    }, [email, token]);

    const fetchAllAchievements = useCallback(async (earned) => {
        const response = await fetch('http://localhost:8090/api/v1/achievement/list', {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const data = await response.json();
            const achievementsWithOrder = data.achievements.map(achievement => ({
                ...achievement,
                isEarned: earned.some(earned => earned.id === achievement.id)
            }));
            achievementsWithOrder.sort((a, b) => (b.isEarned - a.isEarned));
            setAllAchievements(achievementsWithOrder);
        }
    }, [token]);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const earned = await fetchEarnedAchievements();
            await fetchAllAchievements(earned);
            setLoading(false);
        };
        fetchData();
    }, [fetchAllAchievements, fetchEarnedAchievements, setLoading, token, email]);

    return (
        <div className="achievements-container">
            {allAchievements.map((achievement) => (
                <Achievement
                    key={achievement.id}
                    name={achievement.name}
                    description={achievement.description}
                    points={achievement.achievementPoints}
                    isEarned={earnedAchievements.some(earned => earned.id === achievement.id)}
                />
            ))}
        </div>
    );
};

export default Achievements;
