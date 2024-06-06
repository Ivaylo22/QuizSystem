import React, {useState, useEffect} from 'react';
import "../styles/statistics.css";
import {useLoading} from '../context/LoadingContext';

const Statistics = ({token, email}) => {
    const [userInformation, setUserInformation] = useState({});
    const {setLoading} = useLoading();

    useEffect(() => {
        const fetchUserInformation = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8090/api/v1/user/info?email=${encodeURIComponent(email)}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch user information.');
                }
                const data = await response.json();
                setUserInformation(data.userInformation);
            } catch (error) {
                console.error('Error fetching user information:', error);
            }
            setLoading(false);
        };

        fetchUserInformation();
    }, [email, token, setLoading]);

    return (
        <div className="statistics-container">
            <div className="statistics-info">
                <div className="statistic-item">
                    <strong>Куизове завършени за под една минута:</strong>
                    <span>{userInformation.quizzesUnderOneMinuteCount}</span>
                </div>
                <div className="statistic-item">
                    <strong>Брой перфектни куизове:</strong>
                    <span>{userInformation.perfectQuizzesCount}</span>
                </div>
                <div className="statistic-item">
                    <strong>Поредни куизове преминати:</strong>
                    <span>{userInformation.consecutiveQuizzesPassedCount}</span>
                </div>
                <div className="statistic-item">
                    <strong>Поредни дневни куизове завършени:</strong>
                    <span>{userInformation.consecutiveDailyQuizzesCount}</span>
                </div>
                <div className="statistic-item">
                    <strong>Общо завършени дневни куизове:</strong>
                    <span>{userInformation.dailyQuizzesCount}</span>
                </div>
                <div className="statistic-item">
                    <strong>Общо преминати куизове:</strong>
                    <span>{userInformation.quizzesPassedCount}</span>
                </div>
            </div>
        </div>
    );
};

export default Statistics;
