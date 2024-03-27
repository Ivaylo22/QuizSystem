import React from 'react';
import { useLocation } from 'react-router-dom';

const QuizResults = () => {
    const location = useLocation();
    const { results, timer } = location.state;

    console.log(results)

    return (
        <div>
            <h2>Quiz Results</h2>
            <p>Time taken: {Math.floor(timer / 60)} minutes {timer % 60} seconds</p>
            {results.map((result, index) => (
                <div key={index} style={{ border: `2px solid ${result.isCorrect ? 'green' : 'red'}` }}>
                    <p>Question ID: {result.questionId}</p>
                    <p>Your Answer: {Array.isArray(result.userAnswer) ? result.userAnswer.join(', ') : result.userAnswer}</p>
                    { !result.isCorrect && <p>Correct Answer: {Array.isArray(result.correctAnswer) ? result.correctAnswer.join(', ') : result.correctAnswer}</p> }
                </div>
            ))}
        </div>
    );
};

export default QuizResults;