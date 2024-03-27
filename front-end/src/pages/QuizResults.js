import React from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/quizResults.css';

const QuizResults = () => {
    const {state} = useLocation();
    const {quiz, userAnswers, result} = state;

    const isOpenAnswerCorrect = (questionId, userAnswer) => {
        const correctAnswers = quiz.questions.find(q => q.id === questionId).answers.filter(a => a.isCorrect).map(a => a.content);
        return correctAnswers.includes(userAnswer[0]);
    };

    const countCorrectAnswers = quiz.questions.reduce((acc, question) => {
        const userAnswer = userAnswers[question.id];
        if (question.questionType === 'OPEN') {
            if (isOpenAnswerCorrect(question.id, userAnswer)) {
                return acc + 1;
            }
        } else {
            const correctAnswers = question.answers.filter(a => a.isCorrect).map(a => a.content);
            const isCorrect = correctAnswers.length === userAnswer.length && correctAnswers.every(answer => userAnswer.includes(answer));
            if (isCorrect) {
                return acc + 1;
            }
        }
        return acc;
    }, 0);

    return (
        <div className="container mt-5">
            <h3>Натрупан опит: {result.experienceGained}</h3>
            <h3>Верни отговори: {countCorrectAnswers}/{quiz.questions.length}</h3>
            {quiz.questions.map((question, qIndex) => (
                <div key={qIndex} className="mb-4 p-3 question-container">
                    <h4>{question.question}</h4>
                    {question.image && (
                        <img src={question.image ?? 'default-placeholder.png'} alt={`Question ${qIndex + 1}`}
                             className="img-fluid mb-3"/>
                    )}
                    {question.questionType === 'OPEN' ? (
                        <>
                            <div
                                className={`user-answer ${isOpenAnswerCorrect(question.id, userAnswers[question.id]) ? "user-correct" : "user-incorrect"}`}>
                                Твой отговор: {userAnswers[question.id][0]}
                            </div>
                            {!isOpenAnswerCorrect(question.id, userAnswers[question.id]) && (
                                <div className="correct-answer-display">
                                    Верен
                                    отговор: {question.answers.filter(a => a.isCorrect).map(a => a.content).join(', ')}
                                </div>
                            )}
                        </>
                    ) : (
                        question.answers.map((answer, aIndex) => {
                            const isCorrectAnswer = answer.isCorrect;
                            const isUserSelected = userAnswers[question.id]?.includes(answer.content);
                            let className = isCorrectAnswer ? "question-correct" : "answer-option";
                            if (isUserSelected) {
                                className += isCorrectAnswer ? " user-correct" : " user-incorrect";
                            }

                            return (
                                <div key={aIndex} className={className}>
                                    {answer.content}
                                </div>
                            );
                        })
                    )}
                </div>
            ))}
        </div>
    );
};

export default QuizResults;
