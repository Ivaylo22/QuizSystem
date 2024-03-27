import React, { useEffect, useState } from 'react';
import "../styles/profile.css";
import { toast } from 'react-toastify';

const Profile = () => {
    const [userInformation, setUserInformation] = useState(null);
    const [xpProgress, setXpProgress] = useState({
        level: 0,
        xpTowardsNextLevel: 0,
        xpRequirementForNextLevel: 0,
    });

    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    useEffect(() => {
        // Define fetchXpProgress inside useEffect to not depend on it as a dependency
        const fetchXpProgress = async (totalXp) => {
            try {
                const xpResponse = await fetch(`http://localhost:8090/api/v1/user/xp-info?totalXp=${totalXp}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
                const xpData = await xpResponse.json();
                setXpProgress({
                    level: xpData.level,
                    xpTowardsNextLevel: xpData.xpTowardsNextLevel,
                    xpRequirementForNextLevel: xpData.xpRequirementForNextLevel,
                });
            } catch (error) {
                console.error("Error fetching XP progress:", error);
                toast.error('Failed to fetch XP progress.');
            }
        };

        const fetchUserInfo = async () => {
            try {
                const userInfoResponse = await fetch(`http://localhost:8090/api/v1/user/info?email=${encodeURIComponent(email)}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                if (!userInfoResponse.ok) {
                    throw new Error('Failed to fetch user info');
                }
                const userInfoData = await userInfoResponse.json();
                setUserInformation(userInfoData);

                // Check for experience directly from userInfoData instead of userInfoData.userInformation
                if (userInfoData.userInformation.experience !== undefined) {
                    fetchXpProgress(userInfoData.userInformation.experience);
                }
            } catch (error) {
                console.error("Error fetching user info:", error);
                toast.error('Failed to fetch user info.');
            }
        };

        if (email && token) {
            fetchUserInfo();
        }
    }, [email, token]); // Removed fetchXpProgress from dependencies since it's defined within useEffect

    const xpPercentage = xpProgress.xpTowardsNextLevel < xpProgress.xpRequirementForNextLevel
        ? (xpProgress.xpTowardsNextLevel / xpProgress.xpRequirementForNextLevel) * 100
        : 0;

    if (!userInformation) {
        return <div>Loading...</div>;
    }

    return (
        <div className="profile-container pt-4">
            <div className="profile-avatar">
                <img src={userInformation.userInformation.avatarUrl || 'default-avatar.png'} alt="Avatar" className="avatar-img" />
            </div>
            <div className="profile-info">
                <p><strong>Имейл:</strong> {userInformation.userInformation.email}</p>
                <p><strong>Ниво:</strong> {xpProgress.level}</p>
                <p><strong>Точки от постижения:</strong> {userInformation.userInformation.achievementPoints}</p>
                <p><strong>Опит до следващо ниво:</strong> {xpProgress.xpTowardsNextLevel}/{xpProgress.xpRequirementForNextLevel}</p>
                <div className="xp-bar-container">
                    <div className="xp-bar" style={{ width: `${xpPercentage}%` }}></div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
