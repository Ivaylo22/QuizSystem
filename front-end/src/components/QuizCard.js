import React from 'react';
import '../styles/quizCard.css';

const QuizCard = ({ quiz, onClick }) => {
    const statusText = quiz.haveBeenPassed ? 'Взет' :
                       quiz.haveBeenCompleted ? 'Решен' : 'Нерешаван';
    const statusClass = quiz.haveBeenPassed ? 'status-passed' :
                        quiz.haveBeenCompleted ? 'status-completed' : 'status-not-taken';
    const noDataText = "0";

    return (
        <div className={`row quiz-card ${statusClass}`} onClick={() => onClick(quiz)}>
            <div className="col-4 quiz-item text-start">{quiz.name}</div>
            <div className="col-2 quiz-item text-start">{quiz.category}</div>
            <div className="col-1 quiz-item">{quiz.questionsCount}</div>
            <div className="col-1 quiz-item">{quiz.averageSecondsNeeded ?? noDataText} сек.</div>
            <div className="col-1 quiz-item">{quiz.averageCorrectAnswers ?? noDataText}</div>
            <div className="col-1 quiz-item">{quiz.personalBestXpGained ?? noDataText}/100</div>
            <div className="col-2 quiz-item">{statusText}</div>
        </div>
    );
};

export default QuizCard;
