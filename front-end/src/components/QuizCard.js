import React from 'react';
import '../styles/quizCard.css';

const QuizCard = ({ quiz, onClick }) => {
    const statusText = quiz.haveBeenPassed ? 'Взет' :
                       quiz.haveBeenCompleted ? 'Решен' : 'Нерешаван';
    const statusClass = quiz.haveBeenPassed ? 'status-passed' :
                        quiz.haveBeenCompleted ? 'status-completed' : 'status-not-taken';
    const noDataText = "0";

    const formatSecondsAsTime = (seconds) => {
        if (!seconds && seconds !== 0) return noDataText;
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${String(secs).padStart(2, '0')} мин.`;
    };

    const formatAverageScore = (score, total) => {
        if (!score && score !== 0) return `${noDataText}/${total}`;
        return `${score.toFixed(1)}/${total}`;
    };

    return (
        <div className={`row quiz-card ${statusClass}`} onClick={() => onClick(quiz)}>
            <div className="col-4 quiz-item text-start">{quiz.name}</div>
            <div className="col-2 quiz-item text-start">{quiz.category}</div>
            <div className="col-1 quiz-item">{quiz.questionsCount}</div>
            <div className="col-1 quiz-item">{formatSecondsAsTime(quiz.averageSecondsNeeded)}</div>
            <div className="col-1 quiz-item">
                {formatAverageScore(quiz.averageCorrectAnswers, quiz.questionsCount)}
            </div>
            <div className="col-1 quiz-item">{quiz.personalBestXpGained ?? noDataText}/100</div>
            <div className="col-2 quiz-item">{statusText}</div>
        </div>
    );
};

export default QuizCard;
