import React, { useEffect, useState, useCallback } from 'react';
import Achievement from '../components/Achievement'
import "../styles/achievements.css";
import { useLoading } from '../context/LoadingContext';

const Achievements = ({ token, email }) => {
    const [allAchievements, setAllAchievements] = useState([]);
    const [earnedAchievements, setEarnedAchievements] = useState([]);
    const { setLoading } = useLoading();

    const fetchAllAchievements = useCallback(async () => {
        const response = await fetch('http://localhost:8090/api/v1/achievement/list', {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const data = await response.json();
            const achievementsWithOrder = data.achievements.map(achievement => ({
                ...achievement,
                isEarned: earnedAchievements.some(earned => earned.id === achievement.id)
            }));
            achievementsWithOrder.sort((a, b) => (b.isEarned - a.isEarned));
            setAllAchievements(achievementsWithOrder);
        }
    }, [token, earnedAchievements]);

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
        setLoading(true);
        fetchAllAchievements();
        fetchEarnedAchievements();
        setLoading(false);

    }, [fetchAllAchievements, fetchEarnedAchievements, setLoading]);

    useEffect(() => {
        console.log("All Achievements:", allAchievements);
        console.log("Earned Achievements:", earnedAchievements);
    }, [allAchievements, earnedAchievements]);

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
