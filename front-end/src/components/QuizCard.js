import React from 'react';
import '../styles/quizCard.css';

const QuizCard = ({ quiz }) => {
    const statusText = quiz.haveBeenPassed ? 'Взет' :
                       quiz.haveBeenCompleted ? 'Решен' : 'Нерешаван';
    const statusClass = quiz.haveBeenPassed ? 'status-passed' :
                        quiz.haveBeenCompleted ? 'status-completed' : 'status-not-taken';
    const noDataText = "0";

    return (
        <div className="row quiz-card">
            <div className={`col-5 quiz-item ${statusClass}`}>{quiz.name}</div>
            <div className="col-2 quiz-item">{quiz.category}</div>
            <div className="col-1 quiz-item">{quiz.questionsCount}</div>
            <div className="col-1 quiz-item">{quiz.averageSecondsNeeded ?? noDataText}</div>
            <div className="col-1 quiz-item">{quiz.averageCorrectAnswers ?? noDataText}</div>
            <div className="col-2 quiz-item">{statusText}</div>
        </div>
    );
};

export default QuizCard;
