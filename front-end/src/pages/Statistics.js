import React from 'react';
import "../styles/statistics.css";

const Statistics = ({ userInformation }) => {
    return (
        <div className="statistics-container">
            <div className="statistics-info">
                <p><strong>Куизове завършени за под една минута:</strong> {userInformation.quizzesUnderOneMinuteCount}</p>
                <p><strong>Брой перфектни куизове:</strong> {userInformation.perfectQuizzesCount}</p>
                <p><strong>Поредни куизове преминати:</strong> {userInformation.consecutiveQuizzesPassedCount}</p>
                <p><strong>Поредни дневни куизове завършени:</strong> {userInformation.consecutiveDailyQuizzesCount}</p>
                <p><strong>Общо завършени дневни куизове:</strong> {userInformation.dailyQuizzesCount}</p>
                <p><strong>Общо преминати куизове:</strong> {userInformation.quizzesPassedCount}</p>
            </div>
        </div>
    );
};

export default Statistics;
