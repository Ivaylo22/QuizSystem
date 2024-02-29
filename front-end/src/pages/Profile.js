import React, { useEffect, useState } from 'react';
import "../styles/profile.css";

const Profile = ({ userInformation }) => {
    const token = localStorage.getItem('token');
    const [xpProgress, setXpProgress] = useState({
        level: userInformation.level,
        xpTowardsNextLevel: 0,
        xpRequirementForNextLevel: 0,
    });

    useEffect(() => {
        const fetchXpProgress = async () => {
            try {
                const response = await fetch(`http://localhost:8090/api/v1/user/xp-info?totalXp=${userInformation.experience}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                });
                const data = await response.json();
                setXpProgress({
                    level: data.level,
                    xpTowardsNextLevel: data.xpTowardsNextLevel,
                    xpRequirementForNextLevel: data.xpRequirementForNextLevel,
                });
            } catch (error) {
                console.error("Error fetching XP progress:", error);
            }
        };

        fetchXpProgress();
    }, [userInformation.experience, token]);

    const xpPercentage = xpProgress.xpTowardsNextLevel < xpProgress.xpRequirementForNextLevel
    ? (xpProgress.xpTowardsNextLevel / xpProgress.xpRequirementForNextLevel) * 100
    : 0;

    return (
        <div className="profile-container pt-4">
            <div className="profile-avatar">
                <img src={userInformation.avatarUrl} alt="Avatar" className="avatar-img" />
            </div>
            <div className="profile-info">
                <p><strong>Имейл:</strong> {userInformation.email}</p>
                <p><strong>Ниво:</strong> {userInformation.level}</p>
                <p><strong>Точки от постижения:</strong> {userInformation.achievementPoints}</p>
                <p><strong>Опит до следващо ниво:</strong> {xpProgress.xpTowardsNextLevel}/{xpProgress.xpRequirementForNextLevel}</p>
                <div className="xp-bar-container">
                    <div className="xp-bar" style={{ width: `${xpPercentage}%` }}></div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
